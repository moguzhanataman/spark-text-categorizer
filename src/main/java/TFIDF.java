import org.apache.spark.SparkContext;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.IDF;
import org.apache.spark.ml.feature.IDFModel;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ataman on 03.06.2017.
 */
public class TFIDF {

    private static Logger logger = Logger.getLogger(TFIDF.class.getName());

    public static List<File> getFilesInCategory(String folder) {
        String basePath = "/home/ataman/datasets/haber";
        String folderPath = basePath + "/" + folder;

//            d.listFiles().filter(f => f.isFile && !f.isHidden).toList
        File categoryFolder = new File(folderPath);
        List<File> listFiles = null;
        List<File> f2 = new ArrayList<>();
        if (categoryFolder.exists() && categoryFolder.isDirectory()) {
            listFiles = Arrays.asList(categoryFolder.listFiles());
            int dirLength = listFiles.size();

            int j = 0;
            for (int i = 0; i < dirLength; ++i) {
                if (listFiles.get(i).isFile() && !listFiles.get(i).getName().contains("file_list.txt")) {
                    f2.add(listFiles.get(i));
                    ++j;
                }
            }

            return f2;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .appName("Big Data Project - Text Classification")
                .master("local[*]")
                .getOrCreate();

        SparkContext sc = spark.sparkContext();
        sc.setLogLevel("INFO");

        List<File> ekonomi = getFilesInCategory("ekonomi");

        StringBuilder[] sbArr = new StringBuilder[ekonomi.size()];
        for (int i = 0; i < ekonomi.size(); i++) {
            StringBuilder sb = new StringBuilder();
            sbArr[i] = sb;

            try (BufferedReader br = new BufferedReader(new FileReader(ekonomi.get(i)))) {
                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null) {
                    sb.append(sCurrentLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<Row> data = new ArrayList<>();
        for (int i = 0; i < sbArr.length - 1; i++) {
            data.add(RowFactory.create("ekonomi", sbArr[i].toString()));
        }

        StructType schema = new StructType(new StructField[]{
                new StructField("label", DataTypes.StringType, false, Metadata.empty()),
                new StructField("document", DataTypes.StringType, false, Metadata.empty())
        });
        Dataset<Row> documentData = spark.createDataFrame(data, schema);

        Tokenizer tokenizer = new Tokenizer().setInputCol("document").setOutputCol("words");
        Dataset<Row> wordsData = tokenizer.transform(documentData);

        int numFeatures = 20;
        HashingTF hashingTF = new HashingTF()
                .setInputCol("words")
                .setOutputCol("rawFeatures")
                .setNumFeatures(numFeatures);

        Dataset<Row> featurizedData = hashingTF.transform(wordsData);

        IDF idf = new IDF().setInputCol("rawFeatures").setOutputCol("features");
        IDFModel idfModel = idf.fit(featurizedData);

        Dataset<Row> rescaledData = idfModel.transform(featurizedData);
        rescaledData.select("label", "features").show(false);

        TFIDF.logger.log(Level.INFO, "Feature Count: " + rescaledData.count());

        File tfidfFile = new File("TFIDF.parquet");
        if (!tfidfFile.exists()) {
            rescaledData.write().save(tfidfFile.getPath());
            TFIDF.logger.log(Level.INFO, "Data saved to TFIDF.parquet file.");
        } else {
            TFIDF.logger.log(Level.INFO, "TFIDF file already exists.");
        }

    }
}
