import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.SparkSession
import train.{LogisticRegressionTrainer, RandomForestTrainer}

/**
  * Created by ataman on 21.06.2017.
  */
object App {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("Big Data Project - Text Classification")
      .master("local[*]")
      .getOrCreate()

//    import spark.implicits._

    val sc = spark.sparkContext
    val sqlContext = spark.sqlContext

    for (i <- args.indices) {
      println(i + " " + args(i))
    }

    if (args.length < 2) {
      println(
        """
          |Argument count must 2 or 3.
          |Possible runs:
          |   ./TextClassifier train rf
          |   ./TextClassifier predict rf "hello world"
          |   ./TextClassifier train lr
          |   ./TextClassifier predict lr "hello world"
        """.stripMargin)
      System.exit(0)
    }

    val trainer = if (args(0) == "train") {
      if (args(1) == "rf") {
        new RandomForestTrainer(spark)
      } else if (args(1) == "lr") {
        new LogisticRegressionTrainer(spark)
      }
    } else



    val predictor = if (args(1) == "predict") {
      if (args(1) == "rf") {
        PipelineModel.load("RandomForestModel")
      } else if (args(1) == "lr") {

      }
    }




  }
}
