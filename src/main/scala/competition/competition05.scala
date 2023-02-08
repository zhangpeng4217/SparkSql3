package competition

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SaveMode, SparkSession}

object competition05 {
  def main(args: Array[String]): Unit = {
    //      创建SparkSql的运行环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("spark_on_hive")
    val spark = SparkSession.builder().enableHiveSupport().config(sparkConf).getOrCreate()
    import spark.implicits._
    //      逻辑操作    spark.sql("use competition")
    val dataFrame = spark.sql("select lower(description) as description from E_Commerce_Data_Clean")
      .rdd
      .flatMap(line => {
        line
          .toString()
          .replace("]","")
          .replace("[","")
          .split(" ").map(word => {
          (word, 1)
        })
      })
      .reduceByKey((a, b) => {
        a + b
      })
      .repartition(1)
      .sortBy(_._2,false)
      .take(300)
      val list = dataFrame.toList
      val sc = spark.sparkContext
      val rdd = sc.makeRDD(list)
      val data = rdd.toDF("word","count")
    //      导入数据库
    data.write.format("jdbc").options(Map("url"->"jdbc:mysql://localhost:3306/sparksql","user"->"root","password"->"123456","dbtable"->"questions05")).mode(SaveMode.Append).save()
    //    关闭环境
    spark.close()
  }
}
