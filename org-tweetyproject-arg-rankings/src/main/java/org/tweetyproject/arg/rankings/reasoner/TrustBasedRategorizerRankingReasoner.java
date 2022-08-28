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
 * This class implements the argument ranking approach of [DaCosta. Changing One's Mind: Erase or Rewind, 2011]:.
 * <p>
 * This approach ranks arguments iteratively by considering an argument's basic
 * strength or the strength of its strongest attacker.
 *
 * @author Carola Bauer
 */
public class TrustBasedRategorizerRankingReasoner extends AbstractRankingReasoner<NumericalPartialOrder<Argument, DungTheory>> {



    @Override
    public Collection<NumericalPartialOrder<Argument, DungTheory>> getModels(DungTheory bbase) {
        Collection<NumericalPartialOrder<Argument, DungTheory>> ranks = new HashSet<>();
        ranks.add(this.getModel(bbase));
        return ranks;
    }

    @Override
    public NumericalPartialOrder<Argument, DungTheory> getModel(DungTheory kb) {
        NumericalPartialOrder<Argument, DungTheory> ranking = new NumericalPartialOrder<>();
        ranking.setSortingType(NumericalPartialOrder.SortingType.DESCENDING);

        WeightedDungTheoryWithSelfWeight valuations = new WeightedDungTheoryWithSelfWeight(kb, 0.5); // Stores values of the current iteration
        WeightedDungTheoryWithSelfWeight valuationsOld = new WeightedDungTheoryWithSelfWeight(kb, 0.5); // Stores values of the last iteration
        double distanceOld;
        double distanceNew;
        do {
            distanceOld = getDistance(valuationsOld.getWeights(), valuations.getWeights());



            for (var argument : valuations) {
                var attackers = valuationsOld.getAttackers(argument);


                //find the strongest attacker of argument
                if (attackers.size() == 0) {

                }
                else {
                    Argument max = attackers.iterator().next();
                    for (var att : attackers) {
                        if (valuationsOld.getWeight(att) > valuationsOld.getWeight(max)) {
                            max = att;
                        }
                    }

                    var oldNewWeight = valuations.getWeight(argument);
                    var newNewWeight = getNewWeight(valuationsOld.getWeight(argument), valuationsOld.getWeight(max));
                    valuations.setWeight(argument, newNewWeight);
                    valuationsOld.setWeight(argument, oldNewWeight);
                }
            }
            distanceNew = getDistance(valuationsOld.getWeights(), valuations.getWeights());


        } while(distanceOld!=distanceNew);


        for (Argument arg : (valuations)) {
            ranking.put(arg, valuations.getWeight(arg));
        }

            return ranking;
}


    /**
     * Calculates the new weight.
     *
     * @return new weight of the argument
     */
    private double getNewWeight(Double arg, Double maxAttack) {

        return Math.min(arg, 1 - maxAttack);
    }


    /**
     * natively installed
     */
    @Override
    public boolean isInstalled() {
        return true;
    }

    /**
     * Computes the Euclidean distance between to the given arrays.
     * @param vOld first array
     * @param v second array
     * @return distance between v and vOld
     */
    private double getDistance(Double[] vOld, Double[] v) {
        double sum = 0.0;
        for (int i = 0; i < v.length; i++) {
            sum += Math.pow(v[i]-vOld[i],2.0);
        }
        System.out.println("Distanz"+Math.sqrt(sum));

        return Math.sqrt(sum);
    }





}