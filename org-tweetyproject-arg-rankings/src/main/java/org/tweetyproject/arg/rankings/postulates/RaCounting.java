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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.rankings.postulates;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.rankingbasedextension.exactreasoner.ExactGeneralRankingBasedExtensionReasoner;
import org.tweetyproject.arg.rankings.reasoner.AbstractRankingReasoner;
import org.tweetyproject.comparator.GeneralComparator;

import java.util.Collection;
import java.util.Iterator;

/**
 * The "Counting" postulate for gradual semantics as proposed in
 * [Amgoud, Ben-Naim. Axiomatic Foundations of Acceptability Semantics. 2016]
 * considers the quantity of non-rejected attackers.
 *
 * @author Carola Bauer
 */
public class RaCounting extends RankingPostulate {

    @Override
    public String getName() {
        return "Counting";
    }

    @Override
    public boolean isApplicable(Collection<Argument> kb) {
        return ((kb instanceof DungTheory) && (kb.size() >= 2));
    }

    @Override
    public boolean isSatisfied(Collection<Argument> kb, AbstractRankingReasoner<GeneralComparator<Argument, DungTheory>> ev) {
        if (!this.isApplicable(kb))
            return true;
        if (ev.getModel((DungTheory) kb) == null)
            return true;


        DungTheory dt = new DungTheory((DungTheory) kb);
        GeneralComparator<Argument, DungTheory> ranking = ev.getModel((DungTheory) dt);
        var satisfied =true;
        for (var a: dt) {
            for (var b : dt) {

                var attackersA = dt.getAttackers(a);
                var attackersB = dt.getAttackers(b);


                if (attackersB.size() > (attackersA.size() )
                        && !dt.isAttackedBy(a,a)
                        && !dt.isAttackedBy(b,b)
                        && (attackersA.stream().anyMatch(
                        arg1 -> !dt.isAttackedBy(arg1, arg1)))
                        && attackersB.containsAll(attackersA)
                        && (attackersB.stream().filter(arg2 -> !attackersA.contains(arg2))
                        .anyMatch(arg3 -> !dt.isAttackedBy(arg3, arg3)))) {
                    satisfied= ranking.isStrictlyMoreAcceptableThan(a, b);

                }
                if (satisfied== false) {
                    System.out.println(dt);
                    return satisfied;
                }
            }
        }
        return satisfied;
    }

}
