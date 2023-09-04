package org.tweetyproject.arg.rbextensionsemantics.evaluation.util;
/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

import org.tweetyproject.arg.dung.principles.Principle;

import java.math.BigDecimal;
import java.util.List;


/**
 * Helper class that stores the results of a threshold evaluation.
 *
 * @author Carola Bauer
 */
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


    /**
     * Creates a new threshold evaluation object for a gradual semantics
     * @param name the name for the report
     * @param prinziplesFulfilled list of the number of principles fulfilled for each threshold
     * @param prinziplesNotFulfilled list of the number of principles not fulfilled for each threshold
     * @param thresholds the thresholds used
     * @param percOfNodes the percentage of the average nodes in an extension
     */
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
