package competition

import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}

object competition06 {
  def main(args: Array[String]): Unit = {
    //      创建SparkSql的运行环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("spark_on_hive")
    val spark = SparkSession.builder().enableHiveSupport().config(sparkConf).getOrCreate()
    //      逻辑操作
    spark.sql("use competition")
    val dataFrame = spark.sql("select u,count(distinct invoiceno) as countreturn from E_Commerce_Data_Clean where invoiceno like 'C%' group by u order by countreturn desc limit 10")
    //      导入数据库
    dataFrame.write.format("jdbc").options(Map("url"->"jdbc:mysql://localhost:3306/sparksql","user"->"root","password"->"123456","dbtable"->"questions06")).mode(SaveMode.Append).save()
    //    关闭环境
    spark.close()
  }
}
