package competition

import org.apache.spark.sql.SparkSession


object competition08 {

  def main(args: Array[String]): Unit = {
    // 创建SparkSession对象
    val spark = SparkSession.builder()
      .master("local[1]").appName("req8").getOrCreate()
    // 修改默认配置
    spark.conf.set("spark.sql.crossJoin.enabled", true)
    // 读取文件
    val df = spark.read.csv("data/etl.csv")
    // 给获取的列表取别名
    val newNames = Seq("InvoiceNo", "StockCode", "Description", "Quantity", "InvoiceDate", "UnitPrice", "CustomerID", "Country")
    //    // 将集合转换为DATa Frame对象并给标题名
    val dfRenamed = df.toDF(newNames: _*)
    dfRenamed.createOrReplaceTempView("etl")
    //    dfRenamed.show();
    //    月销售额随时间的变化趋势   2/13/2011 12:05
      val trade = dfRenamed.select("InvoiceDate","Quantity","UnitPrice")
      .rdd
      .map(line=>{
        val data = line
          .toString()
          .replace("[","")
          .replace("]","")
          .split(",")
        val i = data(0).indexOf(" ")
        val date = data(0)
          .substring(0, i)
          .split("/")
        val monthyear=date(2)+"-"+date(0)
        val quantity = data(1).toInt
        val unitPrice = data(2).toDouble
        (monthyear,quantity*unitPrice)
      })
      .reduceByKey(_+_)
      .repartition(1)
      .sortByKey()
    import spark.implicits._
    val frame = trade.toDF("date", "sale")
    frame.write.csv("result/req8_res")
//
//    trade.foreach(println)

    spark.stop();
  }


}
