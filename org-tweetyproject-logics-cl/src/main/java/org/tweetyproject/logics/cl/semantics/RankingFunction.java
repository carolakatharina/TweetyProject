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
package org.tweetyproject.logics.cl.semantics;

import java.util.*;

import org.tweetyproject.commons.*;
import org.tweetyproject.logics.cl.syntax.*;
import org.tweetyproject.logics.pl.semantics.*;
import org.tweetyproject.logics.pl.syntax.*;


/**
 * A ranking function (or ordinal conditional function, OCF) that maps possible worlds
 * of a propositional language to integers. 
 * 
 * <br><br>See W. Spohn. Ordinal conditional functions: a dynamic theory of epistemic states.
 * In W.L. Harper and B. Skyrms, editors, Causation in Decision, Belief Change, and Statistics, II,
 * pages 105-134. Kluwer Academic Publishers, 1988.
 * 
 * @author Matthias Thimm
 *
 */
public class RankingFunction extends AbstractInterpretation<ClBeliefSet,Conditional> {
	
	/**
	 * Integer used to define infinity.
	 */
	public static final Integer INFINITY = Integer.MAX_VALUE;
	
	/**
	 * The ranks of the possible worlds.
	 */
	private Map<PossibleWorld,Integer> ranks;
	
	/**
	 * The signature of the language this ranking function
	 * is defined on.
	 */
	private PlSignature signature;
	
	/**
	 * Creates a new ranking function mapping each
	 * given interpretation to zero.
	 * @param signature the signature of the language this ranking function
	 * is defined on.
	 */
	public RankingFunction(PlSignature signature){
		this.signature = signature;		
		this.ranks = new HashMap<PossibleWorld,Integer>();
		for(PossibleWorld w: PossibleWorld.getAllPossibleWorlds(signature.toCollection()))
			this.ranks.put(w, 0);			
	}
	
	/**
	 * Gets the rank of the given possible world.
	 * @param w an possible world.
	 * @return the rank of the given possible world.
	 * @throws IllegalArgumentException if the given possible world has no
	 *   rank in this ranking function.
	 */
	public Integer rank(PossibleWorld w) throws IllegalArgumentException{
		if(!this.ranks.containsKey(w))
			throw new IllegalArgumentException("No rank defined for the possible world " + w);
		return this.ranks.get(w);
	}
	
	/**
	 * Sets the rank for the given possible world.
	 * @param w an possible world.
	 * @param value the rank for the possible world.
	 */
	public void setRank(PossibleWorld w, Integer value){		
		if(value < 0)
			throw new IllegalArgumentException("Illegal rank value " + value + ". Ranks must be greater or equal zero.");
		this.ranks.put(w, value);
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logic.Interpretation#satisfies(org.tweetyproject.logic.Formula)
	 */
	@Override
	public boolean satisfies(Conditional formula) throws IllegalArgumentException{
		Conditional c = (Conditional) formula;
		Integer rankPremiseAndConclusion = this.rank(c.getConclusion().combineWithAnd(c.getPremise().iterator().next()));
		Integer rankPremiseAndNotConclusion = this.rank((PlFormula)c.getConclusion().complement().combineWithAnd(c.getPremise().iterator().next()));
		return rankPremiseAndConclusion < rankPremiseAndNotConclusion;		
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.kr.Interpretation#satisfies(org.tweetyproject.logic.KnowledgeBase)
	 */
	@Override
	public boolean satisfies(ClBeliefSet beliefBase){
		for(Formula f: beliefBase)
			if(!(f instanceof Conditional))
				throw new IllegalArgumentException();
			else if(!this.satisfies((Conditional)f))
				return false;
		return true;
	}
	
	/**
	 * Sets the rank of every interpretation i that does not satisfy
	 * the given set of formulas to RankingFunction.INFINITY.
	 * @param formulas a set of first-order formulas.
	 */
	public void forceStrictness(Set<PlFormula> formulas){
		for(PossibleWorld w: this.ranks.keySet())
			if(!w.satisfies(formulas))
				this.setRank(w, RankingFunction.INFINITY);
	}
	
	/**
	 * Gets the rank of the given formula. Throws an IllegalArgumentException when
	 * the language of the formula does not correspond to the language of the
	 * interpretations this ranking function is defined on. Otherwise the rank of a formula
	 * is defined as the minimal rank of its satisfying interpretations.
	 * @param formula a formula.
	 * @return the rank of the given formula.
	 * @throws IllegalArgumentException if the languages of the formula does not correspond to the language of the
	 * 		interpretations this ranking function is defined on.
	 */
	public Integer rank(PlFormula formula) throws IllegalArgumentException{
		Integer rank = RankingFunction.INFINITY;
		for(PossibleWorld i: this.ranks.keySet())
			if(i.satisfies(formula))
				if(this.ranks.get(i).compareTo(rank)<0)
					rank = this.ranks.get(i); 
		return rank;
	}
	
	/**
	 * Returns the minimal rank of this OCF.
	 * @return the minimal rank of this OCF.
	 */
	private Integer minimalRank(){
		Integer min = RankingFunction.INFINITY;
		for(Integer i: this.ranks.values())
			if(i < min)
				min = i;
		return min;
	}
	
	/**
	 * Normalizes this OCF, i.e. appropriately shifts the ranks
	 * such that the minimal rank equals zero. 
	 */
	public void normalize(){
		Integer minimalRank = this.minimalRank();
		for(PossibleWorld w: this.ranks.keySet()){
			if(this.rank(w) != RankingFunction.INFINITY)
				this.ranks.put(w, this.rank(w)-minimalRank);
		}
			
	}
	
	/**
	 * Checkes whether this OCF is normalized, i.e. whether its
	 * minimal rank value is zero.
	 * @return "true" if this OCF is normalized
	 */
	public boolean isNormalized(){
		return this.minimalRank() == 0;
	}
	
	/**
	 * Returns all interpretations that are mapped to a rank
	 * unequal to INFINITY.
	 * @return all interpretations that are mapped to a rank
	 * unequal to INFINITY.
	 */
	public Set<PossibleWorld> getPossibleWorlds(){
		Set<PossibleWorld> worlds = new HashSet<PossibleWorld>();
		for(PossibleWorld w: this.ranks.keySet())
			if(this.ranks.get(w) < RankingFunction.INFINITY)
				worlds.add(w);
		return worlds;
	}
	
	/**
	 * Returns all interpretations that are mapped to 0
	 * @return all interpretations that are mapped to 0
	 */
	public Set<PossibleWorld> getPlausibleWorlds(){
		Set<PossibleWorld> worlds = new HashSet<PossibleWorld>();
		for(PossibleWorld w: this.ranks.keySet())
			if(this.ranks.get(w) == 0)
				worlds.add(w);
		return worlds;
	}
	
	/**
	 * Returns the signature of the first-order language this ranking function
	 * is defined on.
	 * @return the signature of the first-order language this ranking function
	 * is defined on.
	 */
	public PlSignature getSignature(){
		return this.signature;
	}
	
	/**
	 * Checks whether the given possible world w verifies the given 
	 * conditional (B|A), i.e. whether w satisfies A and B
	 * @param w a possible world
	 * @param c a conditional.
	 * @return "true" if the given possible world verifies the given conditional. 
	 */
	public static boolean verifies(PossibleWorld w, Conditional c){
		PlFormula formula = c.getPremise().iterator().next().combineWithAnd(c.getConclusion());
		return w.satisfies(formula);
	}
	
	/**
	 * Checks whether the given possible world w falsifies the given 
	 * conditional (B|A), i.e. whether w satisfies A and not B
	 * @param w a possible world
	 * @param c a conditional.
	 * @return "true" if the given possible world falsifies the given conditional. 
	 */
	public static boolean falsifies(PossibleWorld w, Conditional c){
		PlFormula formula = c.getPremise().iterator().next().combineWithAnd(c.getConclusion().complement());
		return w.satisfies(formula);
	}
	
	/**
	 * Checks whether the given possible world w satisfies the given 
	 * conditional (B|A), i.e. whether w does not falsify c.
	 * @param w a possible world
	 * @param c a conditional.
	 * @return "true" if the given possible world satisfies the given conditional. 
	 */
	public static boolean satisfies(PossibleWorld w, Conditional c){
		return !RankingFunction.falsifies(w, c);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		String s = "[\n";
		Iterator<PossibleWorld> it = this.ranks.keySet().iterator();
		while(it.hasNext()){
			PossibleWorld w = it.next();
			s += "  " + w + " => ";
			if(this.rank(w).equals(RankingFunction.INFINITY))
				s += "INFINITY";
			else s += this.rank(w);
			s += "\n";
		}
		s += "]";
		return s;
	}
	
	@Override
	public boolean equals(Object other) {
		if(! (other instanceof RankingFunction))
			return false;
		
		RankingFunction oc = (RankingFunction)other;
		return ranks.equals(oc.ranks);
	}
}
