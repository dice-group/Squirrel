package org.dice_research.squirrel.predictor;

import de.jungblut.math.DoubleVector;
import de.jungblut.math.dense.SingleEntryDoubleVector;
import de.jungblut.math.sparse.SequentialSparseDoubleVector;
import de.jungblut.online.ml.FeatureOutcomePair;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.Stream;

public class TrainingDataProviderImpl implements TrainingDataProvider {

    private static final SingleEntryDoubleVector POSITIVE_CLASS = new SingleEntryDoubleVector(1d);
    private static final SingleEntryDoubleVector NEGATIVE_CLASS = new SingleEntryDoubleVector(0d);
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingDataProviderImpl.class);
    private Predictor predictor = new PredictorImpl();

    @Override
    public Stream<FeatureOutcomePair> setUpStream(String filePath) {
        URL url = null;
        try {
            url = new URL(filePath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader((new InputStreamReader(url.openStream())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return br.lines().map((s) -> parseFeature(s));
    }


    public FeatureOutcomePair parseFeature(String line) {
        String[] split = line.split(",");

        URI furi = null;
        try {
            //System.out.println(split[0].replace("\"", ""));
            furi = new URI(split[0].replace("\"", ""));
        } catch (URISyntaxException e) {
            try {
                furi = new URI("http://scoreboard.lod2.eu/data/scoreboardDataCube.rdf");
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
            //e.printStackTrace();
        }
        CrawleableUri uri = new CrawleableUri(furi);
        predictor.featureHashing(uri);
        Object featureArray = uri.getData(Constants.FEATURE_VECTOR);
        double[] doubleFeatureArray = (double[]) featureArray;
        DoubleVector features = new SequentialSparseDoubleVector(doubleFeatureArray);
        return new FeatureOutcomePair(features, split[1].equals("dereferenceable") ? POSITIVE_CLASS : NEGATIVE_CLASS);
    }

}

