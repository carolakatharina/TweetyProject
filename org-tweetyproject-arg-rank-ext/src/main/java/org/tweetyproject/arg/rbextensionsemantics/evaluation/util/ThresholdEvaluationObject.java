package org.tweetyproject.arg.rbextensionsemantics.evaluation.util;

import org.tweetyproject.arg.dung.principles.Principle;

import java.math.BigDecimal;
import java.util.List;

public class ThresholdEvaluationObject {
    private final String name;
    private final List<List<Principle>> prinziplesFulfilled;

    public List<Double> getNumberOfNodes() {
        return percNodesInExtension;
    }

    private final List<Double> percNodesInExtension;

    public List<List<Principle>> getPrinziplesNotFulfilled() {
        return prinziplesNotFulfilled;
    }

    private final List<List<Principle>> prinziplesNotFulfilled;

    private final List<BigDecimal> thresholds;
    public String getName() {
        return name;
    }

    public List<List<Principle>> getPrinziplesFulfilled() {
        return prinziplesFulfilled;
    }

    public List<BigDecimal> getThresholds() {
        return thresholds;
    }




    public ThresholdEvaluationObject(String name, List<List<Principle>>
            prinziplesFulfilled, List<List<Principle>> prinziplesNotFulfilled, List<BigDecimal> thresholds,
                                     List<Double> percOfNodes) {

        this.name = name;
        this.prinziplesFulfilled = prinziplesFulfilled;
        this.prinziplesNotFulfilled = prinziplesNotFulfilled;
        this.thresholds = thresholds;
        this.percNodesInExtension = percOfNodes;
    }


}
