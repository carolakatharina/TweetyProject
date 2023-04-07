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
package org.tweetyproject.arg.aspic.ruleformulagenerator;

import org.tweetyproject.arg.aspic.syntax.DefeasibleInferenceRule;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Implements <code>RuleFormulaGenerator</code> for propositional logic.
 * If a rule has been given a name, it is employed as an identifier.
 *
 * @author Nils Geilen
 */
public class PlFormulaGenerator extends RuleFormulaGenerator<PlFormula> {

	/* (non-Javadoc)
	 * @see ruleformulagenerator.RuleFormulaGenerator#getRuleFormula(org.tweetyproject.arg.aspic.syntax.InferenceRule)
	 */
	@Override
	public PlFormula getRuleFormula(DefeasibleInferenceRule<PlFormula> r) {
		if(r.getName() == null)
			return new Proposition("rule_" + r.hashCode());
		else
			return new Proposition(r.getName());
	}

}
