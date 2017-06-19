import java.io.File

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{HashingTF, RegexTokenizer}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf

import scala.io.Source

/**
  * Created by ataman on 19.06.2017.
  */
object LRTest {
  val datasetBase = "dataset/"

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("Big Data Project - Text Classification")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val sc = spark.sparkContext
    val sqlContext = spark.sqlContext

    sc.setLogLevel("ERROR")

    Category.fromString("saglik")

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

    val topic2Label: String => Double =
      category => Category.fromString(category).getOrElse(InvalidCategory).label
    val toLabel = udf(topic2Label)
    val labelled = labeledTextsDS.withColumn("label", toLabel($"category")).cache

    val Array(trainDF, testDF) = labelled.randomSplit(Array(0.75, 0.25))

    val tokenizer = new RegexTokenizer()
      .setInputCol("text")
      .setOutputCol("words")

    val hashingTF = new HashingTF()
      .setInputCol(tokenizer.getOutputCol) // it does not wire transformers -- it's just a column name
      .setOutputCol("features")
      .setNumFeatures(5000)

    val lr = new LogisticRegression().setMaxIter(20).setRegParam(0.01)

    val pipeline = new Pipeline().setStages(Array(tokenizer, hashingTF, lr))

    val model = pipeline.fit(trainDF)

    val trainPredictions = model.transform(trainDF)
    val testPredictions = model.transform(testDF)

//    trainPredictions.select('id, 'category, 'text, 'label, 'prediction).show
//    testPredictions.select('id, 'category, 'text, 'label, 'prediction).show

    // Evaluate for confusion matrix metric
//    val evaluator = new MulticlassClassificationEvaluator().setMetricName("confusionMatrix")
//    val evaluatorParams = ParamMap(evaluator.metricName -> "confusionMatrix")
//    val areaTrain = evaluator.evaluate(trainPredictions, evaluatorParams)
//    println("Train ROC: " + areaTrain)
//    val areaTest = evaluator.evaluate(testPredictions, evaluatorParams)
//    println("Test ROC: " + areaTest)

    val testPreds = testPredictions.select('prediction, 'label)

    val metrics = new MulticlassMetrics(testPreds.rdd.map(row => (row.getDouble(0), row.getDouble(1))))
    println("Confusion matrix: ")
    println(metrics.confusionMatrix)

    // Overall Statistics
    val accuracy = metrics.accuracy
    println("Summary Statistics")
    println(s"Accuracy = $accuracy")

    // Precision by label
    val labels = metrics.labels
    labels.foreach { l =>
      println(s"Precision($l) = " + metrics.precision(l))
    }

    // Recall by label
    labels.foreach { l =>
      println(s"Recall($l) = " + metrics.recall(l))
    }

    // False positive rate by label
    labels.foreach { l =>
      println(s"FPR($l) = " + metrics.falsePositiveRate(l))
    }

    // F-measure by label
    labels.foreach { l =>
      println(s"F1-Score($l) = " + metrics.fMeasure(l))
    }

    // Weighted stats
    println(s"Weighted precision: ${metrics.weightedPrecision}")
    println(s"Weighted recall: ${metrics.weightedRecall}")
    println(s"Weighted F1 score: ${metrics.weightedFMeasure}")
    println(s"Weighted false positive rate: ${metrics.weightedFalsePositiveRate}")
  }
}