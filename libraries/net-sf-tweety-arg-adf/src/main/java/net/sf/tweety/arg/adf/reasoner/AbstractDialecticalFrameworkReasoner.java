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
package net.sf.tweety.arg.adf.reasoner;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.commons.InferenceMode;

/**
 * Ancestor class for all adf reasoner
 *  
 * @author Mathias Hofer
 */
public abstract class AbstractDialecticalFrameworkReasoner {
	
	private Pipeline<?> computationPipeline;
	
	/**
	 * @param computationPipeline
	 */
	public AbstractDialecticalFrameworkReasoner(Pipeline<?> computationPipeline) {
		this.computationPipeline = computationPipeline;
	}

	public Boolean query(AbstractDialecticalFramework beliefbase, Argument formula) {
		return this.query(beliefbase, formula, InferenceMode.SKEPTICAL);
	}

	/**
	 * 
	 * @param adf
	 * @param argument
	 * @param inferenceMode
	 * @return
	 */
	public Boolean query(AbstractDialecticalFramework adf, Argument argument, InferenceMode inferenceMode) {
		switch (inferenceMode) {
		case CREDULOUS:
			return credulousQuery(adf, argument);
		case SKEPTICAL:
			return skepticalQuery(adf, argument);
		default:
			throw new IllegalArgumentException("InferenceMode not implemented!");
		}
	}
	
	private boolean skepticalQuery(AbstractDialecticalFramework adf, Argument argument) {
		Iterator<Interpretation> iterator = modelIterator(adf);
		while (iterator.hasNext()) {
			Interpretation interpretation = iterator.next();
			if (!interpretation.satisfied(argument)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean credulousQuery(AbstractDialecticalFramework adf, Argument argument) {
		Iterator<Interpretation> iterator = modelIterator(adf);
		while (iterator.hasNext()) {
			Interpretation interpretation = iterator.next();
			if (interpretation.satisfied(argument)) {
				return true;
			}
		}
		return false;
	}

	public Collection<Interpretation> getModels(AbstractDialecticalFramework adf) {
		Collection<Interpretation> models = new LinkedList<Interpretation>();
		Iterator<Interpretation> modelIterator = modelIterator(adf);
		while (modelIterator.hasNext()) {
			models.add(modelIterator.next());
		}
		return models;
	}

	public Interpretation getModel(AbstractDialecticalFramework adf) {
		Iterator<Interpretation> modelIterator = modelIterator(adf);
		if (modelIterator.hasNext()) {
			return modelIterator.next();
		}
		return null;
	}
		
	public Iterator<Interpretation> modelIterator(AbstractDialecticalFramework adf) {
		return computationPipeline.iterator(adf);
	}
}