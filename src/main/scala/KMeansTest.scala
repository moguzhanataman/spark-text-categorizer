package BigDataProject

import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.feature.{CountVectorizer, CountVectorizerModel}
import org.apache.spark.sql.SparkSession

/**
  * Created by ataman on 05.06.2017.
  */

object KMeansTest {
  val datasetBase = "dataset/"

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("Big Data Project - Text Classification")
      .master("local[*]")
      .getOrCreate()
    val sc = spark.sparkContext
    val sqlContext = spark.sqlContext

    sc.setLogLevel("ERROR")

    val trainVectorContainer = new VectorContainer("train", spark)
    val testVectorContainer = new VectorContainer("test", spark)
    val allVectorContainer = new VectorContainer("", spark)

    val categories = Seq(
      "ekonomi",
      "magazin",
      "saglik",
      "spor",
      "siyasi"
    )

    categories.map(category => {
      val trainFiles = trainVectorContainer.categoryToFiles(category)
      val testFiles = testVectorContainer.categoryToFiles(category)

      // For CountVectorizing all documents
      val allFiles = allVectorContainer.categoryToFiles(category)

      trainVectorContainer.vectorTuples(trainFiles)
      testVectorContainer.vectorTuples(testFiles)
      allVectorContainer.vectorTuples(allFiles)
    })

    val trainDf = trainVectorContainer.tuplesToDf()
    val testDf = testVectorContainer.tuplesToDf()
    val allDf = allVectorContainer.tuplesToDf()

    val cvModelTrain: CountVectorizerModel = new CountVectorizer()
      .setInputCol("words")
      .setOutputCol("features")
      .fit(trainDf)

    val cvModelTest: CountVectorizerModel = new CountVectorizer()
      .setInputCol("words")
      .setOutputCol("features")
      .fit(testDf)

    val cvModelAll: CountVectorizerModel = new CountVectorizer()
      .setInputCol("words")
      .setOutputCol("features")
      .fit(allDf)


    val trainDataWithFeatures = cvModelTrain.transform(trainDf)
    val testDataWithFeatures = cvModelTest.transform(testDf)
    val allDataWithFeatures = cvModelAll.transform(allDf)

    val trainToModel = allDataWithFeatures.intersect(trainDataWithFeatures)
    val testToModel = allDataWithFeatures.intersect(testDataWithFeatures)

    val kmeans = new KMeans()
      .setK(5)
      .setFeaturesCol("features")
      .setPredictionCol("prediction")
    val model = kmeans
      .fit(allDataWithFeatures)

    val result = model.transform(allDataWithFeatures)

    import spark.implicits._

    val idAndPreds = result.select($"id", $"prediction")

    println("ekonomi:")
    idAndPreds.filter($"id".startsWith("ekonomi")).select($"prediction").groupBy($"prediction").count().show()
    println("magazin")
    idAndPreds.filter($"id".startsWith("magazin")).select($"prediction").groupBy($"prediction").count().show()
    println("saglik:")
    idAndPreds.filter($"id".startsWith("saglik")).select($"prediction").groupBy($"prediction").count().show()
    println("spor")
    idAndPreds.filter($"id".startsWith("spor")).select($"prediction").groupBy($"prediction").count().show()
    println("siyasi")
    idAndPreds.filter($"id".startsWith("siyasi")).select($"prediction").groupBy($"prediction").count().show()

  }
}
