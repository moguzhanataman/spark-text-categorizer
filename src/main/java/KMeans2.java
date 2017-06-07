/**
 * Created by ataman on 05.06.2017.
 */

import org.apache.spark.sql.catalyst.expressions.aggregate.Collect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * K-means clustering using cosine similarity function.
 */
public class KMeans2 {

    List<List<Double>> centers;
    List<List<Double>> clusters;
    List<List<Double>> vectors;

    public KMeans2(int k, List<List<Double>> vectors) {
        Collections.shuffle(vectors);

        this.vectors = vectors;

        for (int i = 0; i < k; i++) {
            centers.add(vectors.get(i));
        }

        for (int i = 0; i < centers.size(); i++) {
            clusters.add(new ArrayList<>());
        }
    }

    public void updateClusters() {

    }

    public static void main(String[] args) {
//        double[][] doubleArr = new double[][]{
//                {1, 2},
//                {3, 4}
//        };
//        new KMeans2(1, doubleArr);
    }
}
