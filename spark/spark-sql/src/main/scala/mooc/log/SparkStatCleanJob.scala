package mooc.log

import mooc.log.util.AccessConvertUtil
import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * @Description: 使用spark完成数据清洗
  *               输入：访问时间、访问URL、耗费的流量、访问IP地址信息
  *               输出：URL、cmsType(video/article)、cmsId(编号)、流量、ip、城市信息、访问时间、天
  * @author: HuangYn 
  * @date: 2018/10/26 20:02
  */

object SparkStatCleanJob {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .master("local[2]").appName("etl log")
      .getOrCreate()
    val logRDD = spark.sparkContext.textFile("G:\\access.log")
    val logDF = spark.createDataFrame(
      logRDD.map(log => AccessConvertUtil.convert(log)), AccessConvertUtil.struct)

    logDF.coalesce(1).write
      .format("parquet")
      .partitionBy("day")
      .mode(SaveMode.Append)
      .save("G:\\access")

    spark.stop()
  }

}