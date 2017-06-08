import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by KursatSahin on 5.06.2017.
 */
public class WordVector {

    private String word;

    private ArrayList<Double> features = new ArrayList<> ();

    public String getWord () {
        return word;
    }

    public ArrayList<Double> getFeatures () {
        return features;
    }

    public WordVector (String line) {
        StringTokenizer st = new StringTokenizer (line);

        if(st.hasMoreTokens ()){
             word = st.nextToken ().toString ();
        }

        while (st.hasMoreElements()) {
            features.add (Double.parseDouble (st.nextToken ()));
        }

    }

}


