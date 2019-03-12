package com.spark.examples.gradle;


import com.spark.examples.gradle.commons.Utils;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

/**
 * Created by fabrice on 3/12/19.
 */
public class SparkInit {
    private static final Logger LOGGER= LoggerFactory.getLogger(SparkInit.class);

    public static void main(String[] args) {

        SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("my app template example");
        String warehouseLocation = new File("spark-warehouse").getAbsolutePath();
        SparkSession sparkSession = SparkSession.builder()
                .config(conf)
                .config("spark.sql.warehouse.dir",warehouseLocation)//define the hive warehouse
                .appName("spark session example")
                .enableHiveSupport() //enable hive support by spark
                .getOrCreate();

        String path = Utils.getPathFromResources("test.csv");
        Properties properties = Utils.loadPropertiesFromResources();
        LOGGER.info("Csv input file :{}",path);
        LOGGER.info("Output json :{}",properties.getProperty("json_output"));

        Dataset<Row> csv_df = sparkSession.read().option("header", "true").option("delimiter", ",").
                csv(path);
        LOGGER.info("record load fom csv :{}" , csv_df.count());
        csv_df.write().mode(SaveMode.Overwrite).json(properties.getProperty("json_output"));

        //DATABASE CASE
        Dataset<Row> jdbc_df = sparkSession.read().format("jdbc")
                .option("url", properties.getProperty("url"))
                .option("dbtable", properties.getProperty("tablename"))
                .option("user", properties.getProperty("username"))
                .option("password", properties.getProperty("password"))
                .load();

        LOGGER.info("record load fom jdbc : {}" , jdbc_df.count());
        //write to csv
        LOGGER.info("output csv fom jdbc : {}" , properties.getProperty("csv_output"));
        jdbc_df.write().mode(SaveMode.Overwrite).csv(properties.getProperty("csv_output"));



        // write to Hive

        jdbc_df.createOrReplaceTempView("tempTable");
        sparkSession.sql("DROP TABLE IF EXISTS mytable");
        sparkSession.sql("CREATE TABLE mytable AS SELECT * FROM tempTable");
        jdbc_df.write().mode(SaveMode.Overwrite).saveAsTable("mytable");
        Dataset<Row> dfHive = sparkSession.sql("SELECT * from mytable");
        LOGGER.info("record number in hive table :{}" , dfHive.count());


    }
}
