package com.wan.online

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import redis.clients.jedis.Jedis

/**
  * @author wanfeng
  * @date 2020/1/31 15:27 实时推荐模块
  */

//定义一个连接助手对象，建立Redis到MongoDB的连接
object ConnHelper extends Serializable {
  lazy val jedis = new Jedis("192.168.2.2")
  lazy val mongoClient = MongoClient(MongoClientURI("mongodb://wan:27017/recommender"))
}

case class MongoConfig(uri: String, db: String)

case class Recommenderation(bookId: Int, score: Double)

case class UserRecs(userId: Int, recs: Seq[Recommenderation])

case class BookRecs(bookId: Int, recs: Seq[Recommenderation])

object OnlineRecommender {

  val MONGODB_RATING_COLLECTION = "Rating"
  // 定义表名
  val STREAM_RECS = "StreamRecs"
  val BOOK_RECS = "BookRecs"

  val MAX_USER_RATINGS_NUM = 20
  val MAX_SIM_BOOKS_NUM = 20

  def main(args: Array[String]): Unit = {

    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://192.168.2.2:27017/recommender",
      "mongo.db" -> "recommender",
      "kafka.topic" -> "recommender"
    )

    val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("OnlineRecommender")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    val sc = spark.sparkContext
    val ssc = new StreamingContext(sc, Seconds(2))

    import spark.implicits._
    implicit val mongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))


    //加载数据，相似度矩阵，广播出去
    val simBooksMatrix = spark.read
      .option("uri", mongoConfig.uri)
      .option("collection", BOOK_RECS)
      .format("com.mongodb.spark.sql")
      .load()
      .as[BookRecs]
      .rdd
      .map { item =>
        (
          item.bookId, item.recs.map(x => (x.bookId, x.score)).toMap
        )
      }
      .collectAsMap()

    //定义广播变量
    val simBooksMatrixBC = sc.broadcast(simBooksMatrix)

    //创建kafka的连接
    val kafkaPara = Map(
      "bootstrap.servers" -> "wan:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "recommender",
      "auto.offset.reset" -> "latest"
    )
    //创建一个DStream
    val kafkaStream = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Array(config("kafka.topic")), kafkaPara)
    )

    //对kafkaStream进行处理，产生评分流，
    val ratingStream = kafkaStream.map {
      msg =>
        val attr = msg.value().split("\\|")
        (
          attr(0).toInt,
          attr(1).toInt,
          attr(2).toDouble
        )
    }

    //核心算法，定义评分流的处理流程
    ratingStream.foreachRDD {
      rdds =>
        rdds.map {
          case (userId, bookId, score) =>
            println("rating data coming...")
            println(userId + "----")
            println(bookId + "----")
            println(score + "----")

            //TODO：核心算法流程
            //1.从redis中取出当前用户的最近评分，保存成一个数组Array[(bookId,score)]

            val userRecentlyRatings = getUserRecentlyRating(MAX_USER_RATINGS_NUM, userId, ConnHelper.jedis)
            //2.从相似度矩阵中获取当前商品最相似的商品列表，作为备选列表
            val candidateBooks = getTopSimBooks(MAX_SIM_BOOKS_NUM, bookId, userId, simBooksMatrixBC.value)
            //3.计算每个备选商品的推荐优先级，得到当前用户的实时推荐列表
            val streamRecs = computeBookScore(candidateBooks, userRecentlyRatings, simBooksMatrixBC.value)

            //4.把推荐列表保存到mongodb
            saveDataToMongoDB(userId, streamRecs)
        }.count()
    }
    //启动streaming
    ssc.start()
    println("streaming started!")
    ssc.awaitTermination()
  }

  import scala.collection.JavaConversions._

  def getUserRecentlyRating(num: Int, userId: Int, jedis: Jedis): Array[(Int, Double)] = {
    jedis.lrange("userId:" + userId.toString, 0, num)
      .map {
        item =>
          val attr = item.split("\\:")
          (
            attr(0).trim.toInt,
            attr(1).trim.toDouble
          )
      }
      .toArray

  }

  def getTopSimBooks(num: Int,
                     bookId: Int,
                     userId: Int,
                     simBooks: scala.collection.Map[Int, scala.collection.immutable.Map[Int, Double]])
                    (implicit mongoConfig: MongoConfig): Array[Int] = {
    val allSimBooks = simBooks(bookId).toArray

    val ratingCollection = ConnHelper.mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION)
    val ratingExist = ratingCollection.find(MongoDBObject("userId" -> userId))
      .toArray
      .map {
        item =>
          item.get("bookId").toString.toInt
      }
    allSimBooks.filter(x => !ratingExist.contains(x._1))
      .sortWith(_._2 > _._2)
      .take(num)
      .map(x => x._1)
  }

  def computeBookScore(candidateBooks: Array[Int],
                       userRecentlyRatings: Array[(Int, Double)],
                       simBooks: scala.collection.Map[Int, scala.collection.immutable.Map[Int, Double]])
  : Array[(Int, Double)] = {
    val scores = scala.collection.mutable.ArrayBuffer[(Int, Double)]()

    val increMap = scala.collection.mutable.HashMap[Int, Int]()
    val decreMap = scala.collection.mutable.HashMap[Int, Int]()

    for (candidateBook <- candidateBooks; userRecentlyRating <- userRecentlyRatings) {
      val simScore = getBooksSimScore(candidateBook, userRecentlyRating._1, simBooks)
      if (simScore > 0.4) {
        scores += ((candidateBook, simScore * userRecentlyRating._2))
        if (userRecentlyRating._2 > 6) {
          increMap(candidateBook) = increMap.getOrDefault(candidateBook, 0) + 1
        } else {
          decreMap(candidateBook) = decreMap.getOrDefault(candidateBook, 0) + 1
        }
      }
    }

    scores.groupBy(_._1).map {
      case (bookId, scoreList) =>
        (bookId, scoreList.map(_._2).sum / scoreList.length + log(increMap.getOrDefault(bookId, 1)) - log(decreMap.getOrDefault(bookId, 1)))
    }
      .toArray
      .sortWith(_._2 > _._2)
  }

  def getBooksSimScore(book1: Int, book2: Int, simBooks: scala.collection.Map[Int, scala.collection.immutable.Map[Int, Double]]): Double = {
    simBooks.get(book1) match {
      case Some(sims) => sims.get(book2) match {
        case Some(score) => score
        case None => 0.0
      }
      case None => 0.0
    }
  }

  def log(m: Int): Double = {
    val N = 10
    math.log(m) / math.log(N)
  }

  def saveDataToMongoDB(userId: Int, streamRecs: Array[(Int, Double)])(implicit mongoConfig: MongoConfig): Unit = {
    val streamCollection = ConnHelper.mongoClient(mongoConfig.db)(STREAM_RECS)

    streamCollection.findAndRemove(MongoDBObject("userId" -> userId))
    streamCollection.insert(MongoDBObject("userId" -> userId, "recs" ->
      streamRecs.map(x => MongoDBObject("bookId" -> x._1, "score" -> x._2)))
    )
  }
}
