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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.commons.postulates;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.commons.BeliefSetIterator;
import org.tweetyproject.commons.Formula;

/**
 * Evaluates some approach (reasoner, measure, etc.) wrt. a series
 * of rationality postulates on a given series of knowledge bases.
 * 
 * @author Matthias Thimm
 *
 * @param <T> The type of formulas used in the evaluation.
 * @param <U> The type of belief bases used in the evaluation.
 */
public class PostulateEvaluator<T extends Formula, U extends BeliefSet<T,?>>{
	
	/**
	 * The belief base sampler used to test the rationality postulates
	 */
	private BeliefSetIterator<T,U> iterator;
	
	/**
	 * The approach being evaluated.
	 */
	private PostulateEvaluatable<T> ev;
	
	/**
	 * the list of postulates the approach is evaluated against
	 */
	private List<Postulate<T>> postulates = new LinkedList<Postulate<T>>();
	
	/**
	 * Creates a new evaluator for the given evaluatable and
	 * belief base generator.
	 * @param iterator some belief set iterator
	 * @param ev some evaluatable
	 * @param postulates a set of postulates
	 */
	public PostulateEvaluator(BeliefSetIterator<T,U> iterator, PostulateEvaluatable<T> ev, Collection<Postulate<T>> postulates) {
		this.iterator = iterator;
		this.ev = ev;
		this.postulates.addAll(postulates);
	}
	
	/**
	 * Creates a new evaluator for the given evaluatable and
	 * belief base generator.
	 * @param iterator some belief set iterator
	 * @param ev some evaluatable
	 */
	public PostulateEvaluator(BeliefSetIterator<T,U> iterator, PostulateEvaluatable<T> ev) {
		this.iterator = iterator;
		this.ev = ev;
	}
	
	/**
	 * Adds the given postulate
	 * @param p some postulate
	 */
	public void addPostulate(Postulate<T> p) {
		this.postulates.add(p);
	}
	
	/**
	 * Adds all postulates in the given collection.
	 * @param postulates some postulates
	 */
	public void addAllPostulates(Collection<? extends Postulate<T>> postulates) {
		for (Postulate<T> p : postulates)
			this.addPostulate(p);
	}

	/**
	 * Removes the given postulate
	 * @param p some postulate
	 * @return true if this contained the specified postulate.
	 */
	public boolean removePostulate(Postulate<T> p) {
		return this.postulates.remove(p);
	}
	
	/**
	 * Removes all postulates in the given collection.
	 * @param postulates some postulates
	 */
	public void removeAllPostulates(Collection<? extends Postulate<T>> postulates) {
		for (Postulate<T> p : postulates)
			this.removePostulate(p);
	}

	/**
	 * Evaluates all postulates of this evaluator on the given 
	 * approach on <code>num</code> belief bases generated by
	 * the sampler of this evaluator.
	 * @param num the number of belief bases to be applied.
	 * @param stopWhenFailed if true the evaluation of one postulate
	 * 	will be stopped once a violation has been encountered.
	 * @return a report on the evaluation
	 */
	public PostulateEvaluationReport<T> evaluate(long num, boolean stopWhenFailed) {
		PostulateEvaluationReport<T> rep = new PostulateEvaluationReport<T>(this.ev,this.postulates);
		Collection<Postulate<T>> failedPostulates = new HashSet<Postulate<T>>();
		for(int i = 0; i < num; i++) {
			U instance = this.iterator.next();
			for(Postulate<T> postulate: this.postulates) {
				if(stopWhenFailed && failedPostulates.contains(postulate))
					continue;
				if(!postulate.isApplicable(instance)) 
					rep.addNotApplicableInstance(postulate, instance);
				else if(postulate.isSatisfied(instance, this.ev))
					rep.addPositiveInstance(postulate, instance);
				else {
					rep.addNegativeInstance(postulate, instance);
					failedPostulates.add(postulate);
				}
			}
			//System.out.println(rep);
		}
		return rep;
	}
	
	/**
	 * Evaluates all postulates of this evaluator on the given 
	 * approach on <code>num</code> belief bases generated by
	 * the sampler of this evaluator. The evaluation of any 
	 * one postulate will be stopped once a violation has been
	 * encountered.
	 * @param num the number of belief bases to be applied.
	 * @return a report on the evaluation
	 */
	public PostulateEvaluationReport<T> evaluate(long num) {
		return this.evaluate(num, true);
	}


	public BeliefSetIterator<T, U> getIterator() {
		return iterator;
	}

	public PostulateEvaluatable<T> getEv() {
		return ev;
	}

	public List<Postulate<T>> getPostulates() {
		return postulates;
	}
}
