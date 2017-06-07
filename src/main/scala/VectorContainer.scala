package BigDataProject

import java.io.File
import java.util.Optional

import org.apache.spark.sql
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.io.Source

/**
  * Created by ataman on 06.06.2017.
  *
  * Utils for KMeans clustering
  */

class VectorContainer(mode: String, spark: SparkSession) {
  val datasetBase = "dataset/"
  var lastCategory: String = ""
  var lastId = 0
  var idVectorArray: Array[(String, Array[String])] = _

  def categoryToFiles(cat: String): Array[File] = {
    lastCategory = cat
    val catDir = new File(datasetBase + cat + "/" + mode)

    val catFiles = catDir.listFiles()
    val validFiles = catFiles.filter(f => f.isFile && !f.getName.contains("file") && f.getName.size > 10)

    validFiles
  }

  def vectorTuples(files: Array[File]): Unit = {
    val idVectorArray = files.map(f => {
      val catLength = lastCategory.length
      lastId = lastId + 1
      val id = f.getName
      val contentArray = Source.fromFile(f.getAbsolutePath)
        .getLines
        .mkString
        .split(" ")
        .map(_.trim)
        .filter(!_.isEmpty)

      (id, contentArray)
    })

    if (this.idVectorArray != null) {
      this.idVectorArray ++= idVectorArray
    } else {
      this.idVectorArray = idVectorArray
    }
  }

  def tuplesToDf(): DataFrame = {
    val df = spark.createDataFrame(idVectorArray).toDF("id", "words")
    df
  }
}