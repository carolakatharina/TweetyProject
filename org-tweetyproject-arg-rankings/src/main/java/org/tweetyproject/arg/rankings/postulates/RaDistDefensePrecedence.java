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

import java.util.*;
import java.util.stream.Collectors;

import org.tweetyproject.arg.rankings.reasoner.AbstractRankingReasoner;
import org.tweetyproject.comparator.GeneralComparator;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * The "distributed-defense precedence" postulate for ranking semantics as
 * proposed in [Amgoud, Ben-Naim. Ranking-based semantics for argumentation
 * frameworks. 2013]: The best defense is when each defender attacks a distinct
 * attacker.
 * 
 * @author Anna Gessler
 *
 */
public class RaDistDefensePrecedence extends RankingPostulate {

	@Override
	public String getName() {
		return "Distributed-Defense Precedence";
	}

	@Override
	public boolean isApplicable(Collection<Argument> kb) {
		if (!(kb instanceof DungTheory))
			return false;
		return(kb.size() >= 2);
	}

	@Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractRankingReasoner<GeneralComparator<Argument, DungTheory>> ev) {
		if (!this.isApplicable(kb))
			return true;
		if (ev.getModel((DungTheory) kb) == null)
			return true;
		
		DungTheory dt = (DungTheory) kb;


		var satisfied=true;
		for (var a: dt) {

			List<Argument> potentialBs = dt.stream().filter(arg ->
							dt.getAttackers(arg).size()==dt.getAttackers(a).size() && !arg.equals(a))
					.collect(Collectors.toList());
			for (var b: potentialBs) {
				Set<Argument> defendersA = new HashSet<Argument>();
				Set<Argument> defendersB = new HashSet<Argument>();
				for (Argument at : dt.getAttackers(a))
					defendersA.addAll(dt.getAttackers(at));
				for (Argument bt : dt.getAttackers(b))
					defendersB.addAll(dt.getAttackers(bt));
				if (defendersA.size() != defendersB.size())
					break;
				if (defenseIsDistributed(a, dt) && defenseIsSimple(a, dt) &&
						!defenseIsDistributed(b, dt) && defenseIsSimple(b, dt)) {
					GeneralComparator<Argument, DungTheory> ranking = ev.getModel((DungTheory) dt);
					if(!ranking.isStrictlyMoreAcceptableThan(a, b)) {
						return false;
					}
				}
			}


		}
		return satisfied;
	}


	/*The defense of an argument x is simple iff each defender of x
	attacks exactly one attacker of x*/
	private boolean defenseIsSimple(Argument x, DungTheory kb) {
		var defenders= kb.getAttackers(kb.getAttackers(x));
		return defenders.stream().allMatch(def ->
				kb.getAttackers(x).stream().filter(att -> kb.isAttackedBy(att, def)).collect(Collectors.toList()).size()==1);
	}

	/*The defense of an argument x is distributed iff every attacker of x is attacked by at least
	one argument */
	private boolean defenseIsDistributed(Argument x, DungTheory kb) {
		var attackers= kb.getAttackers(x);
		return attackers.stream().allMatch(att -> kb.getAttackers(att).size()>0);
	}

}
