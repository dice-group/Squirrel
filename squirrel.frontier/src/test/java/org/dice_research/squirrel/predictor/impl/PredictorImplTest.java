package org.dice_research.squirrel.predictor.impl;

import de.jungblut.math.activation.SigmoidActivationFunction;
import de.jungblut.math.dense.DenseDoubleVector;
import de.jungblut.online.regression.RegressionModel;
import org.dice_research.squirrel.Constants;
import org.dice_research.squirrel.data.uri.CrawleableUri;
import org.dice_research.squirrel.predictor.PredictorImpl;
import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class PredictorImplTest {
    private CrawleableUri curi;
    private PredictorImpl predictor;

    @Test
    public void train() throws Exception {
        // //Initialization
        curi = new CrawleableUri(new URI("https://mcloud.de/web/guest/suche/-/results/search/55?_mcloudsearchportlet_sort=latest"));
        CrawleableUri curiPos = new CrawleableUri(new URI("https://mcloud.de/export/datasets/037388ba-52a7-4d7e-8fbd-101a4202be7f"));
        CrawleableUri curiNeg = new CrawleableUri(new URI("https://ckan.govdata.de"));

        predictor = new PredictorImpl();
        predictor.TRAINING_SET_PATH = "trainDataset.txt";

        // train the learner on two URIs: one RDF and one non RDF
        predictor.train();

        // predict for a random URI(HTML) example
        predictor.featureHashing(curi);
        double pred = predictor.predict(curi);
        double pround = Math.round(pred*100.0)/100.0;
        Assert.assertEquals(0.41,pround,0.1);

        // predict for a Positive (RDF) example
        predictor.featureHashing(curiPos);
        double pred2 = predictor.predict(curiPos);
        double pround2 = Math.round(pred2*100.0)/100.0;
        Assert.assertEquals(0.45, pround2,0.1);

        // predict for a negative (non RDF) example
        predictor.featureHashing(curiNeg);
        double pred3 = predictor.predict(curiNeg);
        double pround3 = Math.round(pred3*100.0)/100.0;
        Assert.assertEquals(0.43, pround3,0.1);
    }

    @Test
    public void featureHashing() throws Exception {
        int flag1 = 0;
        int flag2 = 0;
        CrawleableUri uri1 = new CrawleableUri(new URI("https://dbpedia.org/resource/New_York"));
        CrawleableUri uri2 = new CrawleableUri(new URI("https://wikipedia.org/resource/New_York"));
        CrawleableUri uri3 = new CrawleableUri(new URI("abc:///xyz/zyx/lmn.uvw"));
        PredictorImpl predictor = new PredictorImpl();
        predictor.featureHashing(uri1);
        predictor.featureHashing(uri2);
        predictor.featureHashing(uri3);
        Object feature1 = uri1.getData(Constants.FEATURE_VECTOR);
        Object feature2 = uri2.getData(Constants.FEATURE_VECTOR);
        Object feature3 = uri3.getData(Constants.FEATURE_VECTOR);
        double[] featureArray1 = ((double[]) feature1);
        double[] featureArray2 = ((double[]) feature2);
        double[] featureArray3 = ((double[]) feature3);
        for(int i=0; i<featureArray2.length; i++){
            if(featureArray2[i] == featureArray1[i]) {
                if(featureArray1[i] != 0.0) {
                    flag1 = 1;
                }
            }
            if(featureArray1[i] == featureArray3[i] ){
                if(featureArray1[i]!= 0.0){
                    flag2 = 1;
                }
            }
        }
        assertEquals(1, flag1);
        assertEquals(0, flag2);
    }

    @Test
    public void predict() throws Exception {

        //Initialization
        curi = new CrawleableUri(new URI("https://mcloud.de/export/datasets/037388ba-52a7-4d7e-8fbd-101a4202be7f"));
        predictor = new PredictorImpl();
        String uriType = "notRDF";

        // Weight Intialized with the train weight
        DenseDoubleVector weights = new DenseDoubleVector(new double[] {
            0.0,-0.035450507057501746,0.20397810132303507,-0.02062038864133986,0.7879000088536361,-0.3855672104268757,0.027825042294426172,-0.007488254748866352,0.8859857647598568,-0.7592530323216173,-0.7524914914074725,0.8689916413158338,-0.9108776958602585,0.9290822957478488,0.9360499537802094,0.0,0.7605845412618151,0.7322896495455662,-0.4783602678158889,0.7291427826045802,0.7896861840377427,-0.6415862529711116,0.5041560826244282,-0.3629353870437184,0.06006215868725584,0.0,-0.6620541834427909,-0.47497877513142317,0.013277761865288498,0.8144534767251606,0.9413768161104159,0.7001622490964856,0.7508711005758022,-0.4206049180669704,0.6393758061696719,0.02333236829684493,0.4746611455904599,-0.3663410975269692,-0.7466318465049278,0.9732489783088307,0.0450598185515414,0.5988780257330053,-0.4762611994406447,0.8803134917515896,-0.1114859919149036,-0.9116965457213846,0.08313760420376815,-0.8398040778916374,-0.40989078399844137,0.8737696955608871,0.7946548324394,0.013227846423833425,-0.8859069958653953,0.9556009538057373,-0.5729046562681448,-0.02957848677335284,0.0,-0.6451446799457794,0.36028440280276786,-0.22132759526117018,-0.04423977553337255,-0.597618545315385,-0.703460235380944,0.9782072438858687,-0.5450161903481157,0.31674154278656674,0.0,0.4189464621895964,0.07985148034403067,0.9431315147412587,-0.10510953861674,0.013182987961040048,-0.5311106087882633,0.6680082981732478,-0.07964777166969239,-0.13809729595850007,-0.011856684128325634,0.7292112824516861,0.380980324843998,0.00820595273978042,-0.31992173088714626,0.40894412395631763,0.6175237149352584,0.5544840274089184,-0.7917613498170886,-0.07293942124061115,-0.21128002156278214,0.5572092365597376,0.700560569243676,-0.7553236052220942,0.8717821247773696,-0.36567587850716876,0.6461842504424669,-0.5567929728875847,0.17344675349227745,0.2828942359768001,0.8150643825536323,0.8615102039758413,-0.8109540262880961,-0.3182975511010171,0.546631753936814,-0.24611160204266014,0.5552707953228442,-0.8199825207014733,0.6932113729260052,0.8742872738025764,-0.6870052967769869,-0.90240199222938,0.005855574299146626,-0.9028986586220258,0.72973051200378,0.9473213565390635,0.8147046319260187,0.9525781592653209,0.5269964541560124,0.7406540316598478,0.4829390972156824,0.7848900003194359,0.5160703004124072,-0.35249054903098354,0.3028420819436428,0.13351976653833497,-0.00632676976738557,-0.044133463429707254,0.25444314141032454,0.8936859479285315,0.013215561233962234,-0.9871489459654377,0.36166736579434944,0.8291108998023329,0.06090065112146692,0.0,-0.7421322524722898,0.09051236030578536,0.9354180704946382,0.0,0.012070972954354664,-0.46422135277651666,0.0,0.39120432181572684,0.26922054055477807,-0.14713483539890904,0.0,0.931441139013194,0.3221540620342249,0.7540271725375476,-0.7574299724694253,0.6316618156116438,0.860004322825124,-0.15991656700161117,0.5961308662482729,-0.7574290926306919,-0.1728322852337869,-0.8740174872868105,0.7827081921538448,-0.5906174775863704,-0.9210642425617075,-0.32597861000768513,-0.15431101417127202,-0.009763989354663494,0.3343393509523125,-0.7013547091133017,0.40895603161441874,-0.06134539367645364,0.3037080822603342,0.8868346462289314,-0.11690055249913156,-0.7674696013282696,0.807899948619224,-0.4793362917542143,0.4592278413571942,-0.2440542525241356,0.0,-0.706306541552838,-0.8287979521173168,0.5264713080414056,-0.4703765737018084,0.0,-0.09718347374035896,0.20940714918678305,0.013204101708873237,0.013193283868401882,-0.6253415549307264,-0.5282369572763717,0.0,0.0,-0.13392400990018216,0.0,0.9041523795162163,0.06500216204692545,0.7108909560575294,-0.03730881304928668,0.3866970378833414,-0.15389022512273187,0.45119470496746494,-0.7747363933898324,-0.7508373179870333,-0.41836198429517646,-0.058231619789080424,0.0,0.38159475233708173,0.40544432151299303,-0.33162785277568063,-0.396081103120987,0.24727856501508017,-0.9262208093840645,0.0,-0.33772179306153216,0.718418782685206,-0.500039098213032,-0.19828901383672548,-0.39484959108475604,0.7689073805492193,0.0,-0.6449897063780701,-0.5304714226383649,0.9895721843361411,0.5819532253193906,-0.9708617830101876,-0.47565089596595667,0.14325315554183926,0.046531422142544354,0.07888214622872797,-0.3947045136857956,0.7915053351862686,0.0,0.6166664702464373,0.0,0.40168067735496216,0.0,0.3993928498715953,0.0,-0.7174053735040369,-0.8668539374091333,0.0,0.0,-0.8010636762418368,-0.5749793435798507,0.5049207182824056,-0.11535838345002847,0.5790335291633681,-0.3350648150063633,0.6249598816301656,0.9139658336980172,0.0,0.04300686955630861,-0.503027889011687,0.7523370138183296,-0.3159509611078217,0.01152694671588687,-0.2864458564208383,0.013173130008878195,-0.5193539230796274,0.03267849048624183,0.30412450654857026,0.022517784845301758 });

        // Model and Classifier set up
        predictor.model = new RegressionModel(weights,  new SigmoidActivationFunction());

        // Generate feature vector
        predictor.featureHashing(curi);

        // Prediction
        double pred = predictor.predict(curi);
        if (pred > 0.5) {
            uriType = "RDF";
        }
        Assert.assertEquals("RDF", uriType);

    }

    @Test
    public void updateWeight() throws URISyntaxException {

        curi = new CrawleableUri(new URI("https://mcloud.de/export/datasets/037388ba-52a7-4d7e-8fbd-101a4202be7f"));
        predictor = new PredictorImpl();
        predictor.train();

        // Weight Intialized with the train weight
        DenseDoubleVector test_weights = new DenseDoubleVector(new double[]{
            0.0, -0.035450507057501746, 0.20397810132303507, -0.02062038864133986, 0.7879000088536361, -0.3855672104268757, 0.027825042294426172, -0.007488254748866352, 0.8859857647598568, -0.7592530323216173, -0.7524914914074725, 0.8689916413158338, -0.9108776958602585, 0.9290822957478488, 0.9360499537802094, 0.0, 0.7605845412618151, 0.7322896495455662, -0.4783602678158889, 0.7291427826045802, 0.7896861840377427, -0.6415862529711116, 0.5041560826244282, -0.3629353870437184, 0.06006215868725584, 0.0, -0.6620541834427909, -0.47497877513142317, 0.013277761865288498, 0.8144534767251606, 0.9413768161104159, 0.7001622490964856, 0.7508711005758022, -0.4206049180669704, 0.6393758061696719, 0.02333236829684493, 0.4746611455904599, -0.3663410975269692, -0.7466318465049278, 0.9732489783088307, 0.0450598185515414, 0.5988780257330053, -0.4762611994406447, 0.8803134917515896, -0.1114859919149036, -0.9116965457213846, 0.08313760420376815, -0.8398040778916374, -0.40989078399844137, 0.8737696955608871, 0.7946548324394, 0.013227846423833425, -0.8859069958653953, 0.9556009538057373, -0.5729046562681448, -0.02957848677335284, 0.0, -0.6451446799457794, 0.36028440280276786, -0.22132759526117018, -0.04423977553337255, -0.597618545315385, -0.703460235380944, 0.9782072438858687, -0.5450161903481157, 0.31674154278656674, 0.0, 0.4189464621895964, 0.07985148034403067, 0.9431315147412587, -0.10510953861674, 0.013182987961040048, -0.5311106087882633, 0.6680082981732478, -0.07964777166969239, -0.13809729595850007, -0.011856684128325634, 0.7292112824516861, 0.380980324843998, 0.00820595273978042, -0.31992173088714626, 0.40894412395631763, 0.6175237149352584, 0.5544840274089184, -0.7917613498170886, -0.07293942124061115, -0.21128002156278214, 0.5572092365597376, 0.700560569243676, -0.7553236052220942, 0.8717821247773696, -0.36567587850716876, 0.6461842504424669, -0.5567929728875847, 0.17344675349227745, 0.2828942359768001, 0.8150643825536323, 0.8615102039758413, -0.8109540262880961, -0.3182975511010171, 0.546631753936814, -0.24611160204266014, 0.5552707953228442, -0.8199825207014733, 0.6932113729260052, 0.8742872738025764, -0.6870052967769869, -0.90240199222938, 0.005855574299146626, -0.9028986586220258, 0.72973051200378, 0.9473213565390635, 0.8147046319260187, 0.9525781592653209, 0.5269964541560124, 0.7406540316598478, 0.4829390972156824, 0.7848900003194359, 0.5160703004124072, -0.35249054903098354, 0.3028420819436428, 0.13351976653833497, -0.00632676976738557, -0.044133463429707254, 0.25444314141032454, 0.8936859479285315, 0.013215561233962234, -0.9871489459654377, 0.36166736579434944, 0.8291108998023329, 0.06090065112146692, 0.0, -0.7421322524722898, 0.09051236030578536, 0.9354180704946382, 0.0, 0.012070972954354664, -0.46422135277651666, 0.0, 0.39120432181572684, 0.26922054055477807, -0.14713483539890904, 0.0, 0.931441139013194, 0.3221540620342249, 0.7540271725375476, -0.7574299724694253, 0.6316618156116438, 0.860004322825124, -0.15991656700161117, 0.5961308662482729, -0.7574290926306919, -0.1728322852337869, -0.8740174872868105, 0.7827081921538448, -0.5906174775863704, -0.9210642425617075, -0.32597861000768513, -0.15431101417127202, -0.009763989354663494, 0.3343393509523125, -0.7013547091133017, 0.40895603161441874, -0.06134539367645364, 0.3037080822603342, 0.8868346462289314, -0.11690055249913156, -0.7674696013282696, 0.807899948619224, -0.4793362917542143, 0.4592278413571942, -0.2440542525241356, 0.0, -0.706306541552838, -0.8287979521173168, 0.5264713080414056, -0.4703765737018084, 0.0, -0.09718347374035896, 0.20940714918678305, 0.013204101708873237, 0.013193283868401882, -0.6253415549307264, -0.5282369572763717, 0.0, 0.0, -0.13392400990018216, 0.0, 0.9041523795162163, 0.06500216204692545, 0.7108909560575294, -0.03730881304928668, 0.3866970378833414, -0.15389022512273187, 0.45119470496746494, -0.7747363933898324, -0.7508373179870333, -0.41836198429517646, -0.058231619789080424, 0.0, 0.38159475233708173, 0.40544432151299303, -0.33162785277568063, -0.396081103120987, 0.24727856501508017, -0.9262208093840645, 0.0, -0.33772179306153216, 0.718418782685206, -0.500039098213032, -0.19828901383672548, -0.39484959108475604, 0.7689073805492193, 0.0, -0.6449897063780701, -0.5304714226383649, 0.9895721843361411, 0.5819532253193906, -0.9708617830101876, -0.47565089596595667, 0.14325315554183926, 0.046531422142544354, 0.07888214622872797, -0.3947045136857956, 0.7915053351862686, 0.0, 0.6166664702464373, 0.0, 0.40168067735496216, 0.0, 0.3993928498715953, 0.0, -0.7174053735040369, -0.8668539374091333, 0.0, 0.0, -0.8010636762418368, -0.5749793435798507, 0.5049207182824056, -0.11535838345002847, 0.5790335291633681, -0.3350648150063633, 0.6249598816301656, 0.9139658336980172, 0.0, 0.04300686955630861, -0.503027889011687, 0.7523370138183296, -0.3159509611078217, 0.01152694671588687, -0.2864458564208383, 0.013173130008878195, -0.5193539230796274, 0.03267849048624183, 0.30412450654857026, 0.022517784845301758});

        // Generate feature vector
        predictor.featureHashing(curi);

        // Set True Label for c as 1
        curi.addData(Constants.URI_TRUE_LABEL, 1);

        // Update Parameters and set the model with New Weights
        predictor.weightUpdate(curi);

        // Sum of Old weights and Sum of New Weights must differ
        Assert.assertNotEquals(predictor.model.getWeights().sum(), test_weights.sum(), 0.00000000001);
    }

}


