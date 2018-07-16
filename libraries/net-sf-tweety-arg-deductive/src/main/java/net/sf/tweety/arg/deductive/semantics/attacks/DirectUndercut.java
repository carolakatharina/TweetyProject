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
package net.sf.tweety.arg.deductive.semantics.attacks;

import net.sf.tweety.arg.deductive.semantics.DeductiveArgument;
import net.sf.tweety.logics.pl.NaiveReasoner;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * This attack notion models the direct undercut relation; A is defeated by B iff there is c in support(A) with claim(B) == \neg c.
 * @author Matthias Thimm
 */
public class DirectUndercut implements Attack{

	/** Singleton instance. */
	private static DirectUndercut instance = new DirectUndercut();
	
	/** Private constructor. */
	private DirectUndercut(){};
	
	/**
	 * Returns the singleton instance of this class.
	 * @return the singleton instance of this class.
	 */
	public static DirectUndercut getInstance(){
		return DirectUndercut.instance;
	}	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.deductive.semantics.attacks.Attack#isAttackedBy(net.sf.tweety.argumentation.deductive.semantics.DeductiveArgument, net.sf.tweety.argumentation.deductive.semantics.DeductiveArgument)
	 */
	@Override
	public boolean isAttackedBy(DeductiveArgument a, DeductiveArgument b) {
		NaiveReasoner reasoner = new NaiveReasoner();
		for(PropositionalFormula f: a.getSupport())
			if(reasoner.isEquivalent(b.getClaim(), new Negation(f)))
				return true;
		return false;
	}

}