package org.dice_research.squirrel.predictor;

import de.jungblut.online.ml.FeatureOutcomePair;

import java.util.stream.Stream;

public  interface TrainingDataProvider{

    Stream<FeatureOutcomePair> setUpStream(String filePath);

}
