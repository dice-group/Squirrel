package org.dice_research.squirrel.predictor;

import de.jungblut.online.ml.FeatureOutcomePair;

import java.util.ArrayList;
import java.util.stream.Stream;

public  interface TrainingDataProvider{

    Stream<FeatureOutcomePair> setUpStream(String filePath);

    void createTrainDataFile(String dataUri, String trainFilePath);



}
