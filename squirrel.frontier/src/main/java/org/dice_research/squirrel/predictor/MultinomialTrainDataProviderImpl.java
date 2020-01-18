package org.dice_research.squirrel.predictor;

import de.jungblut.math.DoubleVector;
import de.jungblut.math.dense.DenseDoubleVector;
import de.jungblut.math.sparse.SequentialSparseDoubleVector;
import de.jungblut.online.ml.FeatureOutcomePair;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.stream.Stream;

public class MultinomialTrainDataProviderImpl implements TrainingDataProvider {

    Logger LOGGER = LoggerFactory.getLogger(MultinomialTrainDataProviderImpl.class);
    private FeatureVectorGenerator featureGenerator = new FeatureVectorGenerator();

    /**
     * Used to convert the data in the training file into a stream which can be fed into the learner to learn
     * @param filePath path of the file containing the training data
     * @param classList list containing the class names of the URI
     * @return
     */
    @Override
    public Stream<FeatureOutcomePair> setUpStream(String filePath, ArrayList classList) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filePath)
                , Charset.defaultCharset()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return br.lines().map((s) -> parseFeature(s, classList));
    }

    public FeatureOutcomePair parseFeature(String line, ArrayList classList) {
        DoubleVector[] classes = new DoubleVector[classList.size()];

        for (int i = 0; i < classes.length; i++) {
            classes[i] = new DenseDoubleVector(classes.length);
            classes[i].set(i, 1d);
        }
        String[] split = line.split(",");
        URI furi = null;
        try {
            furi = new URI(split[0].replace("\"", ""));
        } catch (URISyntaxException e) {
            try {
                furi = new URI("http://scoreboard.lod2.eu/data/scoreboardDataCube.rdf");
            } catch (URISyntaxException ex) {
                LOGGER.warn("Exception happened while parsing train data file", ex);
            }
        }
        CrawleableUri uri = new CrawleableUri(furi);
        featureGenerator.featureHashing(uri);
        Object featureArray = uri.getData(Constants.FEATURE_VECTOR);
        double[] doubleFeatureArray = (double[]) featureArray;
        DoubleVector features = new SequentialSparseDoubleVector(doubleFeatureArray);
        split[1] = split[1].replace("\"", "");
        DoubleVector predVector;
        if(classList.indexOf(split[1]) != -1)
            predVector = classes[classList.indexOf(split[1])];
        else
            predVector = classes[0];

        return new FeatureOutcomePair(features, predVector);
    }

    /**
     * Used to create a file using the data from an online source
     * @param dataUri The location of the online source
     * @param trainFilePath The location of the local file to which the data should be written
     */
    public void createTrainDataFile(String dataUri, String trainFilePath) {
        BufferedReader br = null;
        URL url = null;
        String line;
        try {
            PrintWriter writer = new PrintWriter(trainFilePath, "UTF-8");
            url = new URL(dataUri);
            br = new BufferedReader((new InputStreamReader(url.openStream())));
            br.readLine();
            while((line = br.readLine()) != null){
                writer.println(line);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
