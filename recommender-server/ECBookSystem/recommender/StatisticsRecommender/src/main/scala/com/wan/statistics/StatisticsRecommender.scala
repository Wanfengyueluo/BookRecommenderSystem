package com.wan.statistics

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}


case class Rating(userId: Int, bookId: Int, score: Int)

case class MongoConfig(uri: String, db: String)

object StatisticsRecommender {

  val MONGODB_RATING_COLLECTION = "Rating"

  //定义统计表名
  val RATE_MORE_BOOKS = "RateMoreBooks"
  val AVERAGE_BOOKS = "AverageBooks"

  def main(args: Array[String]): Unit = {

    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://192.168.2.2:27017/recommender",
      "mongo.db" -> "recommender"
    )
    val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("StatisticsRecommender")
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    import spark.implicits._

    implicit val mongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))

    val ratingDF = spark.read
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_RATING_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[Rating]
      .toDF()

    ratingDF.createTempView("ratings")

    //1. 历史热门书籍，按照评分个数统计
    val rateMoreBooksDF = spark.sql("select bookId, count(bookId) as countNum from ratings group by bookId order by countNum desc")
    storeDFInMongoDB(rateMoreBooksDF, RATE_MORE_BOOKS)

    //2. 优质书籍
    val averageBooksDF = spark.sql("select bookId, avg(score) as avgScore from ratings group by bookId order by avgScore desc")
    storeDFInMongoDB(averageBooksDF, AVERAGE_BOOKS)

    spark.stop()
  }

  def storeDFInMongoDB(df: DataFrame, collection_name: String)(implicit mongoConfig: MongoConfig): Unit = {
    df.write
      .option("uri", mongoConfig.uri)
      .option("collection", collection_name)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()
  }


}
