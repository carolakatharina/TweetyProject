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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.rankings.reasoner;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.NumericalPartialOrder;

import java.util.Collection;
import java.util.HashSet;

/**
 * This class implements the argument ranking approach of [Besnard, Hunter. A logic-based theory of deductive arguments. 2001]:.
 * <p>
 * This approach ranks arguments iteratively by considering an argument's basic
 * strength as well as the strength of all its attackers.
 *
 * @author Carola Bauer
 */
public class WeightedCategorizerRankingReasoner extends AbstractRankingReasoner<NumericalPartialOrder<Argument, DungTheory>> {


    @Override
    public Collection<NumericalPartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
        Collection<NumericalPartialOrder<Argument, DungTheory>> ranks = new HashSet<NumericalPartialOrder<Argument, DungTheory>>();
        ranks.add(this.getModel(bbase));
        return ranks;
    }

    @Override
    public NumericalPartialOrder<Argument, DungTheory> getModel(DungTheory kb) {
        NumericalPartialOrder<Argument, DungTheory> ranking = new NumericalPartialOrder<Argument, DungTheory>();
        ranking.setSortingType(NumericalPartialOrder.SortingType.DESCENDING);

        WeightedDungTheoryWithSelfWeight valuations = new WeightedDungTheoryWithSelfWeight(kb, 1.0); // Stores values of the current iteration
        WeightedDungTheoryWithSelfWeight valuationsOld = new WeightedDungTheoryWithSelfWeight(kb, 1.0); // Stores values of the last iteration

        for (int step = 0; step < 100000; step++) {


            for (var argument : valuations) {
                var attackers = valuationsOld.getAttackers(argument);
                if (attackers.size() == 0) {
                    //donothing

                } else {
                    double sumAttacks = 0;
                    for (var att : attackers) {
                        sumAttacks= sumAttacks+ valuationsOld.getWeight(att);
                    }
                    var oldNewMax = valuations.getWeight(argument);
                    var newNewMax = getNewWeight(valuationsOld.getWeight(argument), sumAttacks);
                    valuations.setWeight(argument, newNewMax);
                    valuationsOld.setWeight(argument, oldNewMax );

                }
            }

            for (Argument arg : (valuations))
                ranking.put(arg, valuations.getWeight(arg));
            }
            return ranking;
        }


    /**
     * Calculates the new weight.
     *
     * @return new weight of the argument
     */
    private double getNewWeight(Double arg, Double sumAttacks) {

        return arg / (1 + sumAttacks);
    }


    /**
     * natively installed
     */
    @Override
    public boolean isInstalled() {
        return true;
    }


}