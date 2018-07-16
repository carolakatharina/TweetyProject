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
package net.sf.tweety.logics.cl;

import net.sf.tweety.commons.*;

/**
 * Implements the approach from<br>
 * 
 * James P. Delgrande. Relevance, Conditionals, and Defeasible Reasoning. In preparation.
 * 
 * @author Matthias Thimm
 *
 */
public class RelevanceReasoner implements BeliefBaseReasoner<ClBeliefSet> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Reasoner#query(net.sf.tweety.kr.Formula)
	 */
	@Override
	public Answer query(ClBeliefSet beliefset, Formula query) {
		// TODO
		return null;
	}

	/**
	 * Computes the extended knowledge base this reasoner bases on.
	 * @param beliefset a beliefset
	 * @return the extended knowledge base this reasoner bases on.
	 */
	public ClBeliefSet getExtension(ClBeliefSet beliefset){
		//TODO
		return null;
	}
	
}