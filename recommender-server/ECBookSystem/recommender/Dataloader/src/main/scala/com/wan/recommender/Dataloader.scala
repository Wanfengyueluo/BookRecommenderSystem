package com.wan.recommender

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}


/*
* 书籍数据集
*
*"ISBN";"Book-Title";"Book-Author";"Year-Of-Publication";"Publisher";"Image-URL-S";"Image-URL-M";"Image-URL-L"
*"0195153448";                       书籍编号
*"Classical Mythology";              书籍名称
*"Mark P. O. Morford";               书籍作者
*"2002";                             书籍出版日期
*"Oxford University Press";          书籍出版社
*"http://images.amazon.com/images/P/0195153448.01.THUMBZZZ.jpg"; 书籍封面链接
*"http://images.amazon.com/images/P/0195153448.01.MZZZZZZZ.jpg";
*"http://images.amazon.com/images/P/0195153448.01.LZZZZZZZ.jpg"
* */

case class Book(bookId: Int, bookTitle: String, bookAuthor: String, publishDate: String, press: String, bookImageUrl: String)

/*
* 评分数据集
*
* "User-ID";"ISBN";"Book-Rating"
  "276725";       用户ID
  "034545104X";   书籍编号
  "0"             用户评分
* */
case class Rating(userId: Int, bookId: Int, score: Int)


/*
* MongoDB连接配置
* */
case class MongoConfig(uri: String, db: String)

object Dataloader {

  //定义源文件地址
  val BOOK_DATA_PATH = "E:\\ECBookSystem\\recommender\\Dataloader\\src\\main\\resources\\BX-Books.csv"
  val RATEING_DATA_PATH = "E:\\ECBookSystem\\recommender\\Dataloader\\src\\main\\resources\\BX-Book-Ratings.csv"

  //定义表名
  val MONGODB_BOOK_COLLECTION = "Book"
  val MONGODB_RATING_COLLECTION = "Rating"

  def main(args: Array[String]): Unit = {
    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://192.168.2.2:27017/recommender",
      "mongo.db" -> "recommender"
    )

    //创建一个sparkConfig
    val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("DataLoader")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    import spark.implicits._

    //加载数据
    val bookRDD = spark.sparkContext.textFile(BOOK_DATA_PATH)
    val bookDF = bookRDD.map(item => {
      val attr = item.split("\\;")

      Book(
        bookIdToInt(attr(0).replace("\"", "").trim()),
        attr(1).replace("\"", "").trim(),
        attr(2).replace("\"", "").trim(),
        attr(3).replace("\"", "").trim(),
        attr(4).replace("\"", "").trim(),
        attr(7).replace("\"", "").trim()
      )
    }).toDF()

    val ratingRDD = spark.sparkContext.textFile(RATEING_DATA_PATH)
    val ratingDF = ratingRDD.map(item => {
      val attr = item.split("\\;")

      Rating(
        attr(0).replace("\"", "").trim().toInt,
        bookIdToInt(attr(1).replace("\"", "").trim()),
        attr(2).replace("\"", "").trim().toInt
      )
    }).toDF()

    implicit val mongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))


    storeDataInMongoDB(bookDF, ratingDF)

    spark.stop()
  }

  def storeDataInMongoDB(bookDF: DataFrame, ratingDF: DataFrame)(implicit mongoConfig: MongoConfig): Unit = {
    val mongoClient = MongoClient(MongoClientURI(mongoConfig.uri))

    val bookCollection = mongoClient(mongoConfig.db)(MONGODB_BOOK_COLLECTION)
    val ratingCollection = mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION)

    bookCollection.dropCollection()
    ratingCollection.dropCollection()

    bookDF.write
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_BOOK_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()
    ratingDF.write
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_RATING_COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()


    bookCollection.createIndex(MongoDBObject("bookId" -> 1))
    ratingCollection.createIndex(MongoDBObject("bookId" -> 1))
    ratingCollection.createIndex(MongoDBObject("userId" -> 1))

    mongoClient.close()
  }

  //源数据集中的bookId无法直接转化为Int，这里缩短长度并简单处理
  def bookIdToInt(bookId: String): Int = {
    var str: Array[Char] = bookId.toCharArray()
    var res = ""
    if (str(0).toInt > 1) {
      res += '1'
    }
    if (str.length >= 10) {
      for (i <- 1 until 10) {
        if (!isIntByRegex(str(i))) {
          str(i) = '1'
        }
        res += str(i)
      }
    } else {
      for (i <- 0 until (str.length)) {
        if (!isIntByRegex(str(i))) {
          str(i) = '1'
        }
        res += str(i)
      }
    }
    var resBookId = res.trim.toInt
    resBookId
  }

  def isIntByRegex(s: Char) = {
    val pattern = """^(\d+)$""".r
    s match {
      case pattern(_*) => true
      case _ => false
    }
  }

}
