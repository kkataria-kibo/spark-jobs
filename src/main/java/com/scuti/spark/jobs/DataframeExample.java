package com.scuti.spark.jobs;

import com.mongodb.hadoop.MongoInputFormat;

import com.scuti.spark.jobs.model.Customer;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import org.apache.spark.api.java.function.Function;

import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;

import org.bson.BSONObject;
import scala.Tuple2;

import java.io.Serializable;


public class DataframeExample implements Serializable {


    public void run() {

        JavaSparkContext sc = new JavaSparkContext(new SparkConf());
        Configuration mongodbConfig = new Configuration();
        mongodbConfig.set("mongo.job.input.format",
                "com.mongodb.hadoop.MongoInputFormat");

        mongodbConfig.set("mongo.input.uri",
                "mongodb://localhost:27017/predictive.Customer");

        JavaPairRDD<Object, BSONObject> documents = sc.newAPIHadoopRDD(
                mongodbConfig,            // Configuration
                MongoInputFormat.class,   // InputFormat: read from a live cluster.
                Object.class,             // Key class
                BSONObject.class          // Value class
        );

        JavaRDD<Customer> customers = documents.map(

                new Function<Tuple2<Object, BSONObject>, Customer>() {

                    public Customer call(final Tuple2<Object, BSONObject> tuple) {
                        Customer m = new Customer();
                        m.setUserName((String) tuple._2().get("userName"));
                        m.setEmail((String) tuple._2().get("email"));

                        return m;
                    }
                }
        );

        SQLContext sqlContext = new org.apache.spark.sql.SQLContext(sc);

        DataFrame messagesSchema = sqlContext.createDataFrame(customers, Customer.class);
        messagesSchema.registerTempTable("customers");

        DataFrame dataFrame = sqlContext.sql(
                "SELECT userName, email FROM customers WHERE email = \"kapil.kataria@scuticommerce.com\"");

        dataFrame.show();

        messagesSchema.printSchema();
    }

    public static void main(final String[] args) {
        new DataframeExample().run();
    }

}

