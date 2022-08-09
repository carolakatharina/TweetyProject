package org.tweetyproject.arg.rankings.rankingbasedextension;


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

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.reasoner.*;

import java.util.*;

public class RankingBasedExtensionReasoner extends AbstractExtensionReasoner {
    RankingSemantics rankingSemantics;
    Semantics extensionSemantics;


    public enum RankingSemantics {

        CATEGORIZER,
        STRATEGY,
        SAF,

        COUNTING
    }

    public RankingBasedExtensionReasoner(Semantics extensionSemantics,
                                         RankingSemantics semantics) {
        this.rankingSemantics = semantics;
        this.extensionSemantics = extensionSemantics;
    }

    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {

        return null;
    }


    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        Map<Argument, Double> ranking;
        ranking = new HashMap<>(switch (this.rankingSemantics) {
            case CATEGORIZER -> new CategorizerRankingReasoner().getModel(bbase);
            case STRATEGY -> new StrategyBasedRankingReasoner().getModel(bbase);
            case SAF -> new SAFRankingReasoner().getModel(bbase);
            case COUNTING -> new CountingRankingReasoner().getModel(bbase);
        });
        Collection<Extension<DungTheory>> allExtensions = new HashSet<>();

        for (int k = ranking.size(); k > 0; k--) {
            Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(ranking.size(), k);


            while (iterator.hasNext()) {
                final int[] combination = iterator.next();
                Collection<Argument> arguments = new HashSet<>();
                double sum = 0;
                for (int index : combination) {

                    var argument = (Argument) (ranking.keySet().toArray()[index]);
                    arguments.add(argument);

                    System.out.println(argument.getName() + ": " + ranking.get(argument));

                    sum = sum + ranking.get(argument);
                }

                //System.out.println(sum);

                if (sum > getThreshold()) {

                    allExtensions.add(new Extension<>(arguments));
                }


            }


        }
        return allExtensions;
    }

    private double getThreshold() {
        return 1.5;

    }
}

