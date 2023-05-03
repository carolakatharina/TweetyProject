package org.tweetyproject.arg.rankings.rankingbasedextension.evaluation;

import org.tweetyproject.arg.rankings.rankingbasedextension.exactreasoner.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ThresholdValuesForRBSemantics {
    public static List<BigDecimal> getThresholdValues(ExactGeneralRankingBasedExtensionReasoner.RankingSemantics rankingSemantics) {
        var startValue = getRangeForSemantics(rankingSemantics)[0];
        var endValue = getRangeForSemantics(rankingSemantics)[1];
        var values = new ArrayList<BigDecimal>();
        BigDecimal lastValue = startValue;
        values.add(startValue);
        do {
            var newValue = lastValue.add(BigDecimal.valueOf(0.01));
            values.add(newValue);
            lastValue = newValue;
        } while (lastValue.compareTo(endValue)<0);
        return values;
    }

    private static BigDecimal[] getRangeForSemantics(ExactGeneralRankingBasedExtensionReasoner.RankingSemantics semantics) {
        return switch (semantics) {
            case CATEGORIZER -> new BigDecimal[]{ExactCategorizerRankingReasoner.getMinimalValue(), ExactCategorizerRankingReasoner.getMaximalValue()};
            case COUNTING -> new BigDecimal[]{ExactCountingRankingReasoner.getMinimalValue(), ExactCountingRankingReasoner.getMaximalValue()};
            case MAX -> new BigDecimal[]{ExactMaxBasedRankingReasoner.getMinimalValue(), ExactMaxBasedRankingReasoner.getMaximalValue()};
            case TRUST -> new BigDecimal[]{ExactTrustBasedRankingReasoner.getMinimalValue(), ExactTrustBasedRankingReasoner.getMaximalValue()};
            case NSA -> new BigDecimal[]{ExactNsaReasoner.getMinimalValue(), ExactNsaReasoner.getMaximalValue()};
            case ALPHABBS_0 -> null;
            case ALPHABBS_1 -> null;
            case ALPHABBS_2 -> null;
            case EULER -> new BigDecimal[]{ExactEulerMaxBasedRankingReasoner.getMinimalValue(), ExactEulerMaxBasedRankingReasoner.getMaximalValue()};
            case ITS -> new BigDecimal[]{ExactIterativeSchemaRankingReasoner.getMinimalValue(), ExactIterativeSchemaRankingReasoner.getMaximalValue()};
            case MATT_TONI -> new BigDecimal[]{ExactStrategyBasedRankingReasoner.getMinimalValue(), ExactStrategyBasedRankingReasoner.getMaximalValue()};
        };
    }
}
