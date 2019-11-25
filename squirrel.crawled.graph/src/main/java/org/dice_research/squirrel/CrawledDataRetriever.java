package org.dice_research.squirrel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import scala.Tuple2;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.runtime.AbstractFunction1;
import scala.runtime.AbstractFunction2;
import scala.runtime.BoxedUnit;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.graphx.*;
import org.apache.spark.rdd.RDD;
import org.apache.spark.storage.StorageLevel;

public class CrawledDataRetriever
{
    // sendMsg and mergeMsg supplied to aggregateMessages()need to be
    // both Scala (for GraphX API) and Serializable (for Spark)
    static abstract class SerializableFunction1<T1,R>
        extends AbstractFunction1<T1,R> implements Serializable {}

    static abstract class SerializableFunction2<T1,T2,R>
        extends AbstractFunction2<T1,T2,R> implements Serializable {}

    public static void main(String[] args) {
        JavaSparkContext sc = new JavaSparkContext(
            new SparkConf().setMaster("local").setAppName("EdgeCount"));

        JavaRDD<Tuple2<Object, String>> myVertices =
            sc.parallelize(Arrays.asList(new Tuple2<Object,String>(1L, "Ann"),
                new Tuple2<Object,String>(2L, "Bill"),

                new Tuple2<Object,String>(3L, "Charles"),
                new Tuple2<Object,String>(4L, "Diane"),
                new Tuple2<Object,String>(5L, "Went to gym this morning")));

        JavaRDD<Edge<String>> myEdges = sc.parallelize(Arrays.asList(
            new Edge<String>(1L, 2L, "is-friends-with"),
            new Edge<String>(2L, 3L, "is-friends-with"),
            new Edge<String>(3L, 4L, "is-friends-with"),
            new Edge<String>(4L, 5L, "Likes-status"),
            new Edge<String>(3L, 5L, "Wrote-status")));

        Graph<String,String> myGraph = Graph.apply(myVertices.rdd(),
            myEdges.rdd(), "", StorageLevel.MEMORY_ONLY(),
            StorageLevel.MEMORY_ONLY(), tagString, tagString);

        Graph<Integer,String> initialGraph = myGraph.mapVertices(
            new SerializableFunction2<Object,String,Integer>() {
                public Integer apply(Object o, String s) { return 0; }
            },
            tagInteger, null);

        List<Tuple2<Object,Integer>> ls = toJavaPairRDD(
            propagateEdgeCount(initialGraph).vertices(), tagInteger).collect();

        for (Tuple2<Object,Integer> t : ls)
            System.out.print(t + " ** ");

        System.out.println();

        sc.stop();
    }

    // Must explicitly provide for implicit Scala parameters in various
    // function calls
    private static final ClassTag<Integer> tagInteger =
        ClassTag$.MODULE$.apply(Integer.class);
    private static final ClassTag<String> tagString =
        ClassTag$.MODULE$.apply(String.class);
    private static final ClassTag<Object> tagObject =
        ClassTag$.MODULE$.apply(Object.class);

    // sendMsg
    private static final SerializableFunction1<
        EdgeContext<Integer, String, Integer>, BoxedUnit> sendMsg =
        new SerializableFunction1<
            EdgeContext<Integer, String, Integer>, BoxedUnit>() {
            public BoxedUnit apply(EdgeContext<Integer, String, Integer> ec) {
                ec.sendToDst(ec.srcAttr()+1);
                return BoxedUnit.UNIT;
            }
        };

    // mergeMsg
    private static final SerializableFunction2<Integer, Integer, Integer>
        mergeMsg = new SerializableFunction2<Integer, Integer, Integer>() {
        public Integer apply(Integer a, Integer b) {
            return Math.max(a,b);
        }
    };


    private static <T> JavaPairRDD<Object,T>
    toJavaPairRDD(VertexRDD<T> v, ClassTag<T> tagT) {
        return new JavaPairRDD<Object,T>((RDD<Tuple2<Object,T>>)v,
            tagObject, tagT);
    }

    private static Graph<Integer,String> propagateEdgeCount(
        Graph<Integer,String> g) {
        VertexRDD<Integer> verts = g.aggregateMessages(
            sendMsg, mergeMsg, TripletFields.All, tagInteger);
        Graph<Integer,String> g2 = Graph.apply(verts, g.edges(), 0,
            StorageLevel.MEMORY_ONLY(), StorageLevel.MEMORY_ONLY(),
            tagInteger, tagString);
        int check = toJavaPairRDD(g2.vertices(), tagInteger)
            .join(toJavaPairRDD(g.vertices(), tagInteger))
            .map(new Function<Tuple2<Object,Tuple2<Integer,Integer>>,
                Integer>() {
                public Integer call(Tuple2<Object,Tuple2<Integer,Integer>> t) {
                    return t._2._1 - t._2._2;
                }
            })
            .reduce(new Function2<Integer, Integer, Integer>() {
                public Integer call(Integer a, Integer b) {return a+b;}
            });
        if (check > 0)
            return propagateEdgeCount(g2);
        else
            return g;
    }
}
   /* int check = toJavaPairRDD(g2.vertices(), tagInteger)
        .join(toJavaPairRDD(g.vertices(), tagInteger))
        .map(t -> t._2._1 - t._2._2)
        .reduce((a,b) -> a+b); */


