package train

import java.io.File

import types._
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.feature._
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.sql.SparkSession

import scala.io.Source

/**
  * Created by ataman on 21.06.2017.
  */
class RandomForestTrainer(spark: SparkSession) {
  val datasetBase = "dataset/"

  def train(): Unit = {
    import spark.implicits._

    val sc = spark.sparkContext
    val sqlContext = spark.sqlContext

    val documents = new File(datasetBase).listFiles
      .filter(!_.getName.endsWith("file_list.txt"))


    // how to split file name like 'ekonomi089.txt'
    val categorySplit =
      """([a-z]+)(\d+).txt""".r

    // All category folders
    val dsBaseDir = new File(datasetBase).listFiles().filter(_.isDirectory)

    // All files in all categories
    val allFiles = dsBaseDir.flatMap(dir => {
      dir.listFiles().filter(f => f.isFile && !f.getName.contains("file_list.txt"))
    })

    // Convert files to LabeledText
    val labeledTexts = allFiles.map(file => {
      file.getName match {
        case categorySplit(categoryName, documentNumber) => {
          val category = Category.fromString(categoryName).get
          val id = documentNumber.toInt
          LabeledText(id, category.name, Source.fromFile(file.getAbsolutePath).getLines.mkString("\n"))
        }
        case _ => LabeledText(0, "InvalidCategory", "invalid")
      }
    })

    val labeledTextsDS = labeledTexts.toSeq.toDF.as[LabeledText]

    //    val topic2Label: String => Double =
    //      category => Category.fromString(category).getOrElse(InvalidCategory).label
    //    val toLabel = udf(topic2Label)
    //    val labelled = labeledTextsDS.withColumn("label", toLabel($"category")).cache
    //
    val Array(trainDF, testDF) = labeledTextsDS.randomSplit(Array(0.75, 0.25), 10L)

    val transformers = Array(
      new StringIndexer().setInputCol("category").setOutputCol("label"),
      new Tokenizer().setInputCol("text").setOutputCol("tokens"),
      new CountVectorizer().setInputCol("tokens").setOutputCol("features")
    )

    val rf = new RandomForestClassifier()
      .setLabelCol("label")
      .setFeaturesCol("features")

    val model = new Pipeline().setStages(transformers :+ rf).fit(trainDF)

    model.write.overwrite().save("RandomForestModel")
    model
  }

}
