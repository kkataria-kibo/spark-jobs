package com.scuti.spark.jobs;

/**
 * Created by kkataria on 9/18/2016.
 */

import com.mongodb.hadoop.BSONFileInputFormat;
import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.MongoOutputFormat;
import com.scuti.spark.jobs.model.Customer;
import com.scuti.spark.jobs.model.Product;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.slf4j.Logger;
import scala.Tuple2;

import java.util.Date;


public class ScutiRecommendation {

    private static String MONGODB_HOST = "mongodb://127.0.0.1:27017/";

    public static void main(String[] args) {

        String ratingsURI       = MONGODB_HOST + "predictive.ratings";
        String customerURI      = MONGODB_HOST + "predictive.Customer";
        String productURI       = MONGODB_HOST + "predictive.product";

        //OUTPUT collection URI
        String mongodbUri = MONGODB_HOST + "predictive.recommendations";

        //Application Name
        SparkConf conf = new SparkConf().setAppName("ScutiRecommender");

        JavaSparkContext sc = new JavaSparkContext(conf);

        Logger log = sc.sc().log();

        //Rating CONFIG
        Configuration ratingsConfig = new Configuration();
        ratingsConfig.set("mongo.job.input.format", "com.mongodb.hadoop.MongoInputFormat");
        ratingsConfig.set("mongo.input.uri", ratingsURI);
        //Customers CONFIG
        Configuration customerConfig = new Configuration();
        customerConfig.set("mongo.job.input.format", "com.mongodb.hadoop.MongoInputFormat");
        customerConfig.set("mongo.input.uri", customerURI);
        //product CONFIG
        Configuration productConfig = new Configuration();
        productConfig.set("mongo.job.input.format", "com.mongodb.hadoop.MongoInputFormat");
        productConfig.set("mongo.input.uri", productURI);

        //OUTPUT CONFIG
        Configuration predictionsConfig = new Configuration();
        predictionsConfig.set("mongo.output.uri", mongodbUri);

        JavaPairRDD<Object, BSONObject> ratingData = sc.newAPIHadoopRDD(
                ratingsConfig,             // Configuration
                MongoInputFormat.class,   // InputFormat: read from a live cluster.
                Object.class,             // Key class
                BSONObject.class          // Value class
        );
        JavaPairRDD<Object, BSONObject> usersData = sc.newAPIHadoopRDD(
                customerConfig,             // Configuration
                MongoInputFormat.class,   // InputFormat: read from a live cluster.
                Object.class,             // Key class
                BSONObject.class          // Value class
        );
        JavaPairRDD<Object, BSONObject> productData = sc.newAPIHadoopRDD(
                productConfig,             // Configuration
                MongoInputFormat.class,   // InputFormat: read from a live cluster.
                Object.class,             // Key class
                BSONObject.class          // Value class
        );

        JavaRDD<Rating> ratingsData = ratingData.map(
                new Function<Tuple2<Object, BSONObject>, Rating>() {
                    public Rating call(Tuple2<Object, BSONObject> doc) throws Exception {
                        Integer userid = (Integer) doc._2.get("userid");
                        Integer productid = (Integer) doc._2.get("productid");
                        String rating = (String) doc._2.get("rating");
                        return new Rating(userid, productid, Double.valueOf(rating));
                    }
                }
        );
        log.warn("ratings = " + ratingsData.count());

        JavaRDD<Object> customersData = usersData.map(
                new Function<Tuple2<Object, BSONObject>, Object>() {
                    public Object call(Tuple2<Object, BSONObject> doc) throws Exception {
                        return doc._2.get("userid");
                    }
                }
        );

        log.warn("Customers = " + customersData.count());

        JavaRDD<Object> productsData = productData.map(
                new Function<Tuple2<Object, BSONObject>, Object>() {
                    public Object call(Tuple2<Object, BSONObject> doc) throws Exception {
                        return doc._2.get("productid");
                    }
                }
        );

        log.warn("products = " + productsData.count());


    /*    // generate complete pairing for all possible (customers,products) combinations
        JavaPairRDD<Object, Object> usersRatings = customersData.cartesian(productsData);

        System.out.println("============================================== usersRatings ========================================================" +
                " " + usersRatings.count());


        // create the model from existing ratings data
        MatrixFactorizationModel model = ALS.train(ratingsData.rdd(), 10, 10, 0.01);

        // predict ratings
        JavaRDD<Rating> predictions = model.predict(usersRatings.rdd()).toJavaRDD();

        // create BSON RDD from normalized predictions
        JavaPairRDD<Object, BSONObject> predictionsOutput = predictions.mapToPair(
                new PairFunction<Rating, Object, BSONObject>() {
                    @Override
                    public Tuple2<Object, BSONObject> call(Rating rating) throws Exception {
                        BSONObject doc = new BasicBSONObject();
                        doc.put("userid", rating.user());
                        doc.put("productid", rating.product());
                        doc.put("rating", rating.rating());
                        doc.put("timestamp", new Date());
                        // null key means an ObjectId will be generated on insert
                        return new Tuple2<Object, BSONObject>(null, doc);
                    }
                }
        );

//        predictionsOutput.repartition(4);

        log.warn("writing " + predictionsOutput.count() + " documents to " + mongodbUri);

        predictionsOutput.saveAsNewAPIHadoopFile("file:///notapplicable",
                Object.class, Object.class, MongoOutputFormat.class, predictionsConfig);

        sc.sc().log().info("predictionsOutput.splits() = " + predictionsOutput.splits().size());
*/
    }
}
