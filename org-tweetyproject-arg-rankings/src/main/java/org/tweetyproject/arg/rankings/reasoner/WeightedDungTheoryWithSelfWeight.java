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

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a weighted argumentation theory
 * used for learning argumentation theories from labelings with self-weights
 *
 * @author Carola Bauer
 */
public class WeightedDungTheoryWithSelfWeight extends DungTheory {

    /**
     * listing of weights for every argument in the argumentation graph
     */
    public Map<Argument, Double> weights;

    /**
     * initialize a new weighted argumentation theory
     */
    public WeightedDungTheoryWithSelfWeight(DungTheory theory, double selfWeight) {
        super();
        this.add(theory);
        weights = new HashMap<>();
        setInitialWeights(selfWeight);
    }


    private void setInitialWeights(double selfWeight) {
        for (Argument arg: this.getDungTheory().getNodes()) {
            weights.put(arg, selfWeight);
        }
    }

    /**
     * compute Dung Theory only containing attacks with weights greater than zero
     * @return computed Dung Theory
     */
    public DungTheory getDungTheory() {
        DungTheory theory = new DungTheory();
        theory.addAll(this);
        theory.addAllAttacks(this.getAttacks());
        return theory;
    }

    /**
     * Adds all arguments and attacks of the given theory to
     * this theory
     * @param theory some Dung theory
     * @return "true" if this Dung Theory has been modified
     */
    public boolean add(DungTheory theory){
        boolean b1 = this.addAll(theory);
        boolean b2 = this.addAllAttacks(theory.getAttacks());
        return b1 || b2 ;
    }


    /**
     * return weight of the argument
     * @return weight of the argument
     */
    public Double getWeight(Argument arg) {
        return this.weights.get(arg);
    }

    /**
     * return weights all arguments as array
     * @return weights
     */
    public Double[] getWeights() {
        return this.weights.values()
                .stream().toArray(
                        Double[]::new);
    }


    /**
     * sets the weight of the given attack to the given value
     * @param weight new value for the weight
     * @return null or the previously associated value of attack
     */
    public Double setWeight(Argument arg, double weight) {
        return this.weights.put(arg, weight);
    }

}
