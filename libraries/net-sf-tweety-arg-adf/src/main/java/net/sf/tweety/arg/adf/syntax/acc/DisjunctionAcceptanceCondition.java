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
package net.sf.tweety.arg.adf.syntax.acc;

import java.util.Collection;
import java.util.Set;

public final class DisjunctionAcceptanceCondition extends AbstractAcceptanceCondition {
	
	/**
	 * 
	 * @param left
	 * @param right
	 */
	public DisjunctionAcceptanceCondition(AcceptanceCondition left, AcceptanceCondition right) {
		super(Set.of(left, right));
	}

	/**
	 * @param children
	 */
	public DisjunctionAcceptanceCondition(Collection<AcceptanceCondition> children) {
		super(children);
		if (children.size() < 2) {
			throw new IllegalArgumentException("At least 2 children expected!");
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition#accept(net.sf.tweety.arg.adf.syntax.acc.Visitor, java.lang.Object)
	 */
	@Override
	public <U, D> U accept(Visitor<U, D> visitor, D topDownData) {
		return visitor.visit(this, topDownData);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition#getName()
	 */
	@Override
	public String getName() {
		return "or";
	}

}
