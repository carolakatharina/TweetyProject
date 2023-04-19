package org.tweetyproject.arg.rankings.rankingbasedextension.evaluation;

import org.tweetyproject.arg.rankings.rankingbasedextension.RankingBasedExtensionReasoner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ThresholdValuesForRBSemantics {
    public static List<BigDecimal> getThresholdValues(RankingBasedExtensionReasoner.RankingSemantics rankingSemantics) {
        var startValue = getRangeForSemantics(rankingSemantics)[0];
        var endValue = getRangeForSemantics(rankingSemantics)[1];
        var values = new ArrayList<BigDecimal>();
        values.add(startValue);
        BigDecimal lastValue = startValue;
        do {
            var newValue = lastValue.add(BigDecimal.valueOf(0.001));
            values.add(newValue);
            lastValue = newValue;
        } while (lastValue.doubleValue()<endValue.doubleValue());
        return values;
    }

    private static BigDecimal[] getRangeForSemantics(RankingBasedExtensionReasoner.RankingSemantics semantics) {
        return switch (semantics) {
            case MAX -> new BigDecimal[]{BigDecimal.valueOf(0.5), BigDecimal.valueOf(1.0)};
            default -> new BigDecimal[]{BigDecimal.valueOf(0.), BigDecimal.valueOf(1.0)};
        };
    }
}
