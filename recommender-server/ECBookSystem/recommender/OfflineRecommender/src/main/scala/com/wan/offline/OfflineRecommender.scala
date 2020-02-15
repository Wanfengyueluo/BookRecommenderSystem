package com.wan.offline

import org.apache.spark.SparkConf
import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.sql.SparkSession
import org.jblas.DoubleMatrix


case class BookRating(userId: Int, bookId: Int, score: Int)

case class MongoConfig(uri: String, db: String)

case class Recommenderation(bookId: Int, score: Double)

case class UserRecs(userId: Int, recs: Seq[Recommenderation])

case class BookRecs(bookId: Int, recs: Seq[Recommenderation])

object OfflineRecommender {

  val MONGODB_RATING_COLLECTION = "Rating"

  // 定义推荐表名
  val USER_RECS = "UserRecs"
  val BOOK_RECS = "BookRecs"

  //定义推荐数量
  val USER_MAX_RECOMMENDATION = 20

  def main(args: Array[String]): Unit = {

    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://192.168.2.2:27017/recommender",
      "mongo.db" -> "recommender"
    )
    val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("OfflineRecommender")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    import spark.implicits._

    implicit val mongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))

    val ratingRDD = spark.read
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_RATING_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[BookRating]
      .rdd
      .map(
        rating => (rating.userId, rating.bookId, rating.score)
      ).cache()

    val userRDD = ratingRDD.map(_._1).distinct()
    val bookRDD = ratingRDD.map(_._2).distinct()


    //1. 训练隐语义模型
    val trainData = ratingRDD.map(x => Rating(x._1, x._2, x._3))
    val (rank, iterations, lambda) = (50, 5, 0.01)
    val model = ALS.train(trainData, rank, iterations, lambda)


    //2. 获得预评分矩阵，得到用户的推荐列表
    val userBooks = userRDD.cartesian(bookRDD)
    val preRating = model.predict(userBooks)

    val userRecs = preRating.filter(_.rating > 0)
      .map(
        rating => (rating.user, (rating.product, rating.rating))
      )
      .groupByKey()
      .map {
        case (userId, recs) =>
          UserRecs(
            userId, recs.toList.sortWith(_._2 > _._2).take(USER_MAX_RECOMMENDATION).map(x => Recommenderation(x._1, x._2))
          )
      }
      .toDF()

    userRecs.write
      .option("uri", mongoConfig.uri)
      .option("collection", USER_RECS)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    //3. 利用书籍的特征向量，计算书籍的相似度列表
    val productFeatures = model.productFeatures.map {
      case (bookId, features) => (bookId, new DoubleMatrix(features))
    }

    val bookRecs = productFeatures.cartesian(productFeatures)
      .filter {
        case (a, b) => a._1 != b._1
      }
      .map {
        case (a, b) =>
          val simScore = consinSim(a._2, b._2)
          (a._1, (b._1, simScore))
      }
      .filter(_._2._2 > 0.4)
      .groupByKey()
      .map {
        case (bookId, recs) =>
          BookRecs(
            bookId, recs.toList.map(x => Recommenderation(x._1, x._2))
          )
      }
      .toDF()

    bookRecs
      .write
      .option("uri", mongoConfig.uri)
      .option("collection", BOOK_RECS)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    spark.stop()
  }

  def consinSim(product1: DoubleMatrix, product2: DoubleMatrix): Double = {
    product1.dot(product2) / (product1.norm2() * product2.norm2())
  }
}
