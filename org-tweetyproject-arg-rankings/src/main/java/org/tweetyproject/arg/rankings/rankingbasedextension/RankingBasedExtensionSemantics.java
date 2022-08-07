package org.tweetyproject.arg.rankings.rankingbasedextension;

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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

public enum RankingBasedExtensionSemantics {
    /**
     * R_AD
     */
    RB_AD("admissible", "RB_AD"),
    /**
     * R_CO
     */
    RB_CO("complete", "RB_CO"),
    /**
     * R_GR
     */
    RB_GR("grounded", "RB_GR"),
    /**
     * R_PR
     */
    RB_PR("preferred", "RB_PR"),
    /**
     * R_SST
     */
    RB_SST("semi-stable", "RB_SST");


    /**
     * class for ranking-based extension semantics
     */
    public static final RankingBasedExtensionSemantics

    /** ADMISSIBLE */
    ADMISSIBLE_SEMANTICS = RB_AD,
    /**
     * COMPLETE
     */
    COMPLETE_SEMANTICS = RB_CO,

    /** GROUNDED */
    GROUNDED_SEMANTICS = RB_GR,


    /** PREFERRED */
    PREFERRED_SEMANTICS = RB_PR,

    /** SEMI_STABLE */
    SEMI_STABLE_SEMANTICS = RB_SST;



    /** The description of the ranking-based semantics. */
    private String description;
    /** The abbreviation of the ranking-based semantics. */
    private String abbreviation;

    /**
     * Creates a new ranking-based semantics.
     * @param description some description
     * @param abbreviation an abbreviation
     */
    private RankingBasedExtensionSemantics(String description, String abbreviation){
        this.description = description;
        this.abbreviation = abbreviation;
    }

    /**
     * Returns the description of the ranking-based semantics.
     * @return the description of the ranking-based semantics.
     */
    public String description(){
        return this.description;
    }

    /**
     * Returns the abbreviation of the ranking-based semantics.
     * @return the abbreviation of the ranking-based semantics.
     */
    public String abbreviation(){
        return this.abbreviation;
    }
}
