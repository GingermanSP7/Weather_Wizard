package com.sp.sparkstreamingmodule.manager;

import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.regression.LinearRegression;
import org.springframework.context.annotation.Bean;


public class PipelineManager {

    @Bean
    private static PipelineStage[] getPipelineStage(LinearRegression lr){
        return new PipelineStage[]{lr};
    }

    public static Pipeline getPipeline(LinearRegression lr) {
        return new Pipeline()
                .setStages(getPipelineStage(lr));
    }
}
