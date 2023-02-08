package competition

import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}

object competition03 {
  def main(args: Array[String]): Unit = {
    //      创建SparkSql的运行环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("spark_on_hive")
    val spark = SparkSession.builder().enableHiveSupport().config(sparkConf).getOrCreate()
    //      逻辑操作
    spark.sql("use competition")
    val dataFrame = spark.sql("select u,sum(quantity*unitprice) sum from E_Commerce_Data_Clean  GROUP BY u ORDER BY sum DESC")
    dataFrame.show()
//    val dataFrame = spark.sql("select * from E_Commerce_Data_Clean").show()
    dataFrame.write.format("jdbc").options(Map("url"->"jdbc:mysql://localhost:3306/sparksql","user"->"root","password"->"123456","dbtable"->"questions03")).mode(SaveMode.Append).save()
    //    关闭环境
    spark.close()
  }
}
