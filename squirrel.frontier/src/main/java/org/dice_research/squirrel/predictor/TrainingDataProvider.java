package org.dice_research.squirrel.predictor;

import de.jungblut.online.ml.FeatureOutcomePair;

import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Interface to provide training data to the predictor
 */

public  interface TrainingDataProvider{

    /**
     * Function takes the file containing the train data and converts it to a stream that can be used by a predictor to train.
     * @param filePath path of the file containing the training data
     * @param classList list containing the class names of the URI
     * @return a stream of train data
     */
    Stream<FeatureOutcomePair> setUpStream(String filePath, ArrayList classList);

    /**
     * Function reads the data URIs in the given link (@dataUri) and writes it to a textfile (@trainFilePath)
     * @param dataUri
     * @param trainFilePath
     */
    void createTrainDataFile(String dataUri, String trainFilePath);



}
