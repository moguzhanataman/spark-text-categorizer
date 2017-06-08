import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by KursatSahin on 5.06.2017.
 */
public class DocumentVectorGenerator {

    Map<String,ArrayList<Double>> words;
    int mode=1;

    public DocumentVectorGenerator (Map<String, ArrayList<Double>> words, int mode) {
        this.words = words;
        this.mode = mode;
    }

    public DocumentVectorGenerator(){}

    public Double[] generate(File file){
        int wordcount = 0;

        try(BufferedReader br = new BufferedReader(new FileReader(file.getPath ()))) {

            String sCurrentLine;

            StringBuilder content = new StringBuilder();

            Double[] vectorAvg = new Double[200];
            Double[] vectorMin = new Double[200];
            Double[] vectorMax = new Double[200];

            for ( int i = 0; i < 200; i++ ) {
                vectorAvg[i] = new Double(0);
                vectorMax[i] = new Double(0);
                vectorMin[i] = new Double(1);
            }

            while ((sCurrentLine = br.readLine()) != null) {
                content.append(sCurrentLine);
            }

            StringTokenizer st = new StringTokenizer (content.toString ());

            while (st.hasMoreTokens ()) {

                wordcount++;
                String token = st.nextToken ();

                token = (token.length ()>5) ? token.substring (0,5).toLowerCase () : token.toLowerCase ();

                if( null !=words.get (token)){
                    ArrayList<Double> wordVector = words.get (token);

                    for ( int i = 0; i < 200; i++ ) {
                        if(wordVector.get (i) != null)
                            vectorAvg[i] += wordVector.get (i);
                            vectorMin[i] = (vectorMin[i] < wordVector.get (i)) ? vectorMin[i] : wordVector.get (i);
                            vectorMax[i] = (vectorMax[i] > wordVector.get (i)) ? vectorMax[i] : wordVector.get (i);

                    }
                }
            }

            for ( int i = 0; i < 200; i++ ) {
                vectorAvg[i] = vectorAvg[i]/wordcount;
            }

            switch ( mode ){
                case 0: return vectorMin;
                case 1: return vectorAvg;
                case 2: return vectorMax;
                default: return vectorAvg;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
