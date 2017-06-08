import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by KursatSahin on 5.06.2017.
 */
public class Word2VecManager {

    public Map<String,ArrayList<Double>> words = new HashMap<String, ArrayList<Double>> ();

    public void init(){
        String FILENAME = "vectors.txt";

        try (BufferedReader br = new BufferedReader(new FileReader (FILENAME))) {

            String sCurrentLine;

            sCurrentLine = br.readLine();

            while ((sCurrentLine = br.readLine()) != null) {
                WordVector word = new WordVector (sCurrentLine);
                words.put (word.getWord (),word.getFeatures ());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
