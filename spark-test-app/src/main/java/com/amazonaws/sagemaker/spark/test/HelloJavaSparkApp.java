/*
 * Decompiled with CFR 0.137.
 * 
 * Could not load the following classes:
 *  org.apache.commons.cli.BasicParser
 *  org.apache.commons.cli.CommandLine
 *  org.apache.commons.cli.HelpFormatter
 *  org.apache.commons.cli.Option
 *  org.apache.commons.cli.Options
 *  org.apache.commons.cli.ParseException
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.spark.sql.DataFrameReader
 *  org.apache.spark.sql.DataFrameWriter
 *  org.apache.spark.sql.Dataset
 *  org.apache.spark.sql.RelationalGroupedDataset
 *  org.apache.spark.sql.SQLContext
 *  org.apache.spark.sql.SparkSession
 *  org.apache.spark.sql.SparkSession$Builder
 *  org.apache.spark.sql.UDFRegistration
 *  org.apache.spark.sql.api.java.UDF1
 *  org.apache.spark.sql.types.DataType
 *  org.apache.spark.sql.types.DataTypes
 */
package com.amazonaws.sagemaker.spark.test;

import java.io.PrintStream;
import java.lang.invoke.SerializedLambda;
import java.util.List;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.sql.DataFrameReader;
import org.apache.spark.sql.DataFrameWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.RelationalGroupedDataset;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.UDFRegistration;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;

public class HelloJavaSparkApp {
    public static void main(String[] args) {
        System.out.println("Hello World, this is Java-Spark!");
        CommandLine parsedArgs = HelloJavaSparkApp.parseArgs(args);
        String inputPath = parsedArgs.getOptionValue("input");
        String outputPath = parsedArgs.getOptionValue("output");
        SparkSession spark = SparkSession.builder().appName("Hello Spark App").getOrCreate();
        System.out.println("Got a Spark session with version: " + spark.version());
        System.out.println("Reading input from: " + inputPath);
        Dataset salesDF = spark.read().json(inputPath);
        salesDF.printSchema();
        salesDF.createOrReplaceTempView("sales");
        Dataset topDF = spark.sql("SELECT date, sale FROM sales WHERE sale > 750 SORT BY sale DESC");
        topDF.show();
        Dataset avgDF = salesDF.groupBy("date", new String[0]).avg(new String[0]).orderBy("date", new String[0]);
        System.out.println("Collected average sales: " + StringUtils.join((Object[])new List[]{avgDF.collectAsList()}));
        spark.sqlContext().udf().register("double", (UDF1 & java.io.Serializable)n -> n + n, DataTypes.LongType);
        Dataset saleDoubleDF = salesDF.selectExpr(new String[]{"date", "sale", "double(sale) as sale_double"}).orderBy("date", new String[]{"sale"});
        saleDoubleDF.show();
        System.out.println("Writing output to: " + outputPath);
        saleDoubleDF.coalesce(1).write().json(outputPath);
        spark.stop();
    }

    private static CommandLine parseArgs(String[] args) {
        Options options = new Options();
        BasicParser parser = new BasicParser();
        Option input = new Option("i", "input", true, "input path");
        input.setRequired(true);
        options.addOption(input);
        Option output = new Option("o", "output", true, "output path");
        output.setRequired(true);
        options.addOption(output);
        try {
            return parser.parse(options, args);
        }
        catch (ParseException e) {
            new HelpFormatter().printHelp("HelloScalaSparkApp --input /opt/ml/input/foo --output /opt/ml/output/bar", options);
            throw new RuntimeException((Throwable)e);
        }
    }
}

