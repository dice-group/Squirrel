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
import java.util.ArrayList;
import java.util.stream.Stream;

public class TrainingDataProviderImpl implements TrainingDataProvider {

    //private static final SingleEntryDoubleVector POSITIVE_CLASS = new SingleEntryDoubleVector(1d);
    //private static final SingleEntryDoubleVector NEGATIVE_CLASS = new SingleEntryDoubleVector(0d);
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingDataProviderImpl.class);
    private Predictor predictor = new MultinomialPredictor.MultinomialPredictorBuilder().withFile("multiNomialTrainData.txt").build();
    private static final ArrayList<String> classList = new ArrayList<>();

    static {

        classList.add("SPARQL");
        classList.add("DUMP");
        classList.add("CKAN");

    }


    @Override
    public void createTrainDataFile(String dataUri, String trainFilePath) {
        URL url = null;
        BufferedReader br = null;
        String line;
        try {
            PrintWriter writer = new PrintWriter(trainFilePath, "UTF-8");
            url = new URL(dataUri);
            br = new BufferedReader((new InputStreamReader(url.openStream())));
            br.readLine();
            while ((line = br.readLine()) != null) {
                writer.println(line);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Stream<FeatureOutcomePair> setUpStream(String filePath) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return br.lines().map((s) -> parseFeature(s));
    }


    public FeatureOutcomePair parseFeature(String line) {
        DoubleVector[] classes = new DoubleVector[3];
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
                ex.printStackTrace();
            }
        }
        CrawleableUri uri = new CrawleableUri(furi);
        predictor.featureHashing(uri);
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

}

