package com.sp.sparkstreamingmodule.manager;

import org.apache.spark.ml.feature.VectorAssembler;

import java.util.Objects;

public class VectorAssemblerManager {
    private VectorAssemblerManager(){}

    public static VectorAssembler getVector(){
        return new VectorAssembler()
                .setInputCols(new String[]{"humidity", "windSpeed"});
    }

}
