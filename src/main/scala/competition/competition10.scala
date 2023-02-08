package competition

import org.apache.spark.sql.SparkSession


object competition10 {

  def main(args: Array[String]): Unit = {
    // 创建SparkSession对象
    val spark = SparkSession.builder()
      .master("local[1]").appName("req10").getOrCreate()
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
    //    各国的购买订单量和退货订单量的关系    [国家名称,购买订单数,退货订单数]
    val dataFrame = spark.sql("select e1.country,count(distinct e1.InvoiceNo) as countreturn,e2.countpay from etl e1 inner join ((select e3.country,count(distinct e3.InvoiceNo) as countpay from etl e3 where e3.InvoiceNo  not like 'C%' group by e3.country ) as e2) on e1.country=e2.country where InvoiceNo like 'C%' group by e1.country,e2.countpay ")
//        .show()
    dataFrame.repartition(1).write.format("csv").save("result/req10_res");

    spark.stop();
  }


}
