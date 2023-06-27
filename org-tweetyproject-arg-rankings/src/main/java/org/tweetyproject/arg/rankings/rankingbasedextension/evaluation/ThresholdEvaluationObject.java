package org.tweetyproject.arg.rankings.rankingbasedextension.evaluation;

import org.tweetyproject.arg.dung.principles.Principle;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ThresholdEvaluationObject {
    private final String bezeichnung;
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
    public String getBezeichnung() {
        return bezeichnung;
    }

    public List<List<Principle>> getPrinziplesFulfilled() {
        return prinziplesFulfilled;
    }

    public List<BigDecimal> getThresholds() {
        return thresholds;
    }



    public ThresholdEvaluationObject(String bezeichnung, List<List<Principle>>
            prinziplesFulfilled, List<List<Principle>> prinziplesNotFulfilled, List<BigDecimal> thresholds) {

        this.bezeichnung = bezeichnung;
        this.prinziplesFulfilled = prinziplesFulfilled;
        this.prinziplesNotFulfilled = prinziplesNotFulfilled;
        this.thresholds = thresholds;
        this.percNodesInExtension = new ArrayList<>();
    }

    public ThresholdEvaluationObject(String bezeichnung, List<List<Principle>>
            prinziplesFulfilled, List<List<Principle>> prinziplesNotFulfilled, List<BigDecimal> thresholds,
                                    List<Double> percOfNodes) {

        this.bezeichnung = bezeichnung;
        this.prinziplesFulfilled = prinziplesFulfilled;
        this.prinziplesNotFulfilled = prinziplesNotFulfilled;
        this.thresholds = thresholds;
        this.percNodesInExtension = percOfNodes;
    }


}
