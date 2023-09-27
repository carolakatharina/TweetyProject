package org.tweetyproject.arg.rbextensionsemantics.evaluation;

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

import org.tweetyproject.arg.rbextensionsemantics.exactreasoner.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for determining thresholds above which an argument is accepted.
 * @author Carola Bauer
 */
public class ThresholdValuesForRBSemantics {
    public static List<BigDecimal> getThresholdValues(ExactGeneralRankingBasedExtensionReasoner.RankingSemantics rankingSemantics) {
        var startValue = getRangeForSemantics(rankingSemantics)[0];
        var endValue = getRangeForSemantics(rankingSemantics)[1];
        var values = new ArrayList<BigDecimal>();
        BigDecimal lastValue = startValue;
        values.add(startValue);
        do {
            var newValue = lastValue.add(BigDecimal.valueOf(0.0001));
            values.add(newValue);
            lastValue = newValue;
        } while (lastValue.compareTo(endValue) < 0);
        return values;
    }

    private static BigDecimal[] getRangeForSemantics(ExactGeneralRankingBasedExtensionReasoner.RankingSemantics semantics) {
        return switch (semantics) {
            case CATEGORIZER -> new BigDecimal[]{ExactCategorizerRankingReasoner.getMinimalValue(), ExactCategorizerRankingReasoner.getMaximalValue()};
            case COUNTING -> new BigDecimal[]{ExactCountingRankingReasoner.getMinimalValue(), ExactCountingRankingReasoner.getMaximalValue()};
            case MAX -> new BigDecimal[]{ExactMaxBasedRankingReasoner.getMinimalValue(), ExactMaxBasedRankingReasoner.getMaximalValue()};
            case TRUST -> new BigDecimal[]{ExactTrustBasedRankingReasoner.getMinimalValue(), ExactTrustBasedRankingReasoner.getMaximalValue()};
            case NSA -> new BigDecimal[]{ExactNsaReasoner.getMinimalValue(), ExactNsaReasoner.getMaximalValue()};
            case EULER -> new BigDecimal[]{ExactEulerMaxBasedRankingReasoner.getMinimalValue(), ExactEulerMaxBasedRankingReasoner.getMaximalValue()};
            case ITS -> new BigDecimal[]{ExactIterativeSchemaRankingReasoner.getMinimalValue(), ExactIterativeSchemaRankingReasoner.getMaximalValue()};
            case MATT_TONI -> new BigDecimal[]{ExactStrategyBasedRankingReasoner.getMinimalValue(),
                    ExactStrategyBasedRankingReasoner.getMaximalValue()};
        };
    }

    private static BigDecimal[] getAbsArgThresholdForSemantics(ExactGeneralRankingBasedExtensionReasoner.RankingSemantics semantics) {
        return switch (semantics) {
            case CATEGORIZER ->
                    new BigDecimal[]{
                            BigDecimal.valueOf(0.9134),

            };
            case COUNTING ->
                    new BigDecimal[]{BigDecimal.valueOf(0.9939)
            };

            case MAX ->
                    new BigDecimal[]{
                            BigDecimal.valueOf(0.6181)};

            case TRUST, ITS ->  new BigDecimal[]{
                    BigDecimal.valueOf(0.5)};
            case NSA ->   new BigDecimal[]{

                    BigDecimal.valueOf(0.687)
            };

            case EULER ->  new BigDecimal[]{
                    BigDecimal.valueOf(0.5672)};
            case MATT_TONI ->
                    new BigDecimal[]{BigDecimal.valueOf(0.5556) };
        };
    }


    public static BigDecimal[] getThresholdForSemantics(ExactGeneralRankingBasedExtensionReasoner.RankingSemantics semantics,
                                                        ExactGeneralRankingBasedExtensionReasoner.AcceptanceCondition acceptanceCondition) {
        return switch (acceptanceCondition) {
            case RB_ARG_ABS_STRENGTH -> getAbsArgThresholdForSemantics(semantics);
            case RB_ATT_ABS_STRENGTH -> getAbsAttThresholdForSemantics(semantics);
            default -> new BigDecimal[]{BigDecimal.valueOf(0.0)};
        };
    }



    private static BigDecimal[] getAbsAttThresholdForSemantics(ExactGeneralRankingBasedExtensionReasoner.RankingSemantics semantics) {
        return switch (semantics) {
            case CATEGORIZER ->
                    new BigDecimal[]{BigDecimal.valueOf(0.089),
                            BigDecimal.valueOf(0.094)
                    };
            case COUNTING ->
                    new BigDecimal[]{BigDecimal.valueOf(0.229),
                            BigDecimal.valueOf(0.285)
                    };

            case MAX ->
                    new BigDecimal[]{
                            BigDecimal.valueOf(0.618)};
            case TRUST, ITS ->  new BigDecimal[]{
                    BigDecimal.valueOf(0.49999)};
            case NSA ->  new BigDecimal[]{BigDecimal.valueOf(0.726)
            };
            case EULER ->  new BigDecimal[]{
                    BigDecimal.valueOf(0.567)};

            case MATT_TONI ->
                    new BigDecimal[]{BigDecimal.valueOf(0.251) };
        };
    }

}
