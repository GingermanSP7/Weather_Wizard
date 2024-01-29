package com.sp.sparkstreamingmodule.mapping;

import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

public class Schema {
    public static StructType schema = new StructType()
            .add("temp", DataTypes.DoubleType)
            .add("feltTemp", DataTypes.DoubleType)
            .add("maxTemp", DataTypes.DoubleType)
            .add("minTemp", DataTypes.DoubleType)
            .add("city", DataTypes.StringType)
            .add("latitude", DataTypes.StringType)
            .add("longitude", DataTypes.StringType)
            .add("location", DataTypes.StringType)
            .add("timestamp", DataTypes.StringType)
            .add("humidity", DataTypes.DoubleType)
            .add("windSpeed", DataTypes.DoubleType)
            .add("pressure", DataTypes.DoubleType)
            .add("sunrise", DataTypes.StringType)
            .add("sunset", DataTypes.StringType)
            .add("rainIntensity", DataTypes.DoubleType)
            .add("rainProb", DataTypes.DoubleType)
            .add("co", DataTypes.DoubleType)
            .add("no2", DataTypes.DoubleType)
            .add("o3", DataTypes.DoubleType)
            .add("so2", DataTypes.DoubleType)
            .add("pm2_5", DataTypes.DoubleType)
            .add("pm10", DataTypes.DoubleType)
            .add("usEpaIndex", DataTypes.IntegerType)
            .add("gbDefraIndex", DataTypes.IntegerType);
}
