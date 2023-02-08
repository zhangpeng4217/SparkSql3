package competition

import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}

object competition07 {
  def main(args: Array[String]): Unit = {
    //      创建SparkSql的运行环境
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("spark_on_hive")
    val spark = SparkSession.builder().enableHiveSupport().config(sparkConf).getOrCreate()
    //      逻辑操作
    spark.sql("use competition")
    val dataFrame = spark.sql("select stockcode, avg(distinct unitprice) as avgprice, sum(quantity) as sumQuantity from E_Commerce_Data_Clean group by stockcode")
//    dataFrame.show()
    //      导入数据库
    dataFrame.write.format("jdbc").options(Map("url"->"jdbc:mysql://localhost:3306/sparksql","user"->"root","password"->"123456","dbtable"->"questions07")).mode(SaveMode.Append).save()
    //    关闭环境
    spark.close()
  }
}
