package org.dice_research.squirrel.predictor.impl;

import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.predictor.FeatureVectorGenerator;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class FeatureVectorGeneratorTest {
    private FeatureVectorGenerator generator;

    @Test
    public void featureHashing(){
        int flag1 = 0;
        int flag2 = 0;
        generator = new FeatureVectorGenerator();
        try {
            CrawleableUri uri1 = new CrawleableUri(new URI("https://dbpedia.org/resource/New_York"));
            CrawleableUri uri2 = new CrawleableUri(new URI("https://wikipedia.org/resource/New_York"));
            CrawleableUri uri3 = new CrawleableUri(new URI("abc:///xyz/zyx/lmn.uvw"));
            generator.featureHashing(uri1);
            generator.featureHashing(uri2);
            generator.featureHashing(uri3);
            Object feature1 = uri1.getData(Constants.FEATURE_VECTOR);
            Object feature2 = uri2.getData(Constants.FEATURE_VECTOR);
            Object feature3 = uri3.getData(Constants.FEATURE_VECTOR);
            double[] featureArray1 = ((double[]) feature1);
            double[] featureArray2 = ((double[]) feature2);
            double[] featureArray3 = ((double[]) feature3);
            for(int i=0; i<featureArray2.length; i++){
                if(featureArray2[i] == featureArray1[i] && featureArray1[i] != 0.0) {
                    flag1 = 1;
                }
                if(featureArray1[i] == featureArray3[i] && featureArray1[i]!= 0.0 ){
                    flag2 = 1;
                }
            }
            assertEquals(1, flag1);
            assertEquals(0, flag2);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
