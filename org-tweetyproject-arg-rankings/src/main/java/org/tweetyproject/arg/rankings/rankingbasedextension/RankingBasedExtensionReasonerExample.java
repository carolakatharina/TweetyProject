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
package org.tweetyproject.arg.rankings.rankingbasedextension;

import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleIdealReasoner;
import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.reasoner.SimpleStableReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


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



    public class RankingBasedExtensionReasonerExample {
        public static void main(String[] args) {
            // Example 1, taken from [Bonzon, Delobelle, Konieczny, Maudet. A Comparative
            // Study of Ranking-Based Semantics for Abstract Argumentation. AAAI 2016]
            DungTheory example1 = new DungTheory();
            Argument a = new Argument("a");
            Argument b = new Argument("b");
            Argument c = new Argument("c");
            Argument d = new Argument("d");
            Argument e = new Argument("e");
            example1.add(a,b,c,d,e);
            example1.add(new Attack(a, e));
            example1.add(new Attack(d, a));
            example1.add(new Attack(e, d));
            example1.add(new Attack(c, e));
            example1.add(new Attack(b, c), new Attack(b, a));

            // Example 2, taken from
            // [Baumeister, Neugebauer, Rothe. Argumentation Meets Computational Social
            // Choice. Tutorial. 2018]
            DungTheory example2 = new DungTheory();
            Argument f = new Argument("f");
            example2.add(a,b,c,d,e,f);
            example2.add(new Attack(a, b));
            example2.add(new Attack(b, c));
            example2.add(new Attack(d, e));
            example2.add(new Attack(c, f));
            example2.add(new Attack(e, d),new Attack(e, c));

            // Example 3, taken from
            // [Cayrol, Lagasquie-Schiex. Graduality in argumentation. 2005]
            DungTheory example3 = new DungTheory();
            Argument a1 = new Argument("A");
            Argument b1 = new Argument("B1");
            Argument b2 = new Argument("B2");
            Argument b3 = new Argument("B3");
            Argument b4 = new Argument("B4");
            Argument c1 = new Argument("C1");
            Argument c2 = new Argument("C2");
            Argument c3 = new Argument("C3");
            Argument c4 = new Argument("C4");
            Argument d1 = new Argument("D1");
            Argument d2 = new Argument("D2");
            Argument d3 = new Argument("D3");
            Argument e1 = new Argument("E1");
            example3.add(a1,b1,b2,b3,b4);
            example3.add(c1,c2,c3,c4);
            example3.add(d1,d2,d3,e1);
            example3.add(new Attack(b1, a1));
            example3.add(new Attack(b2, a1));
            example3.add(new Attack(b3, a1));
            example3.add(new Attack(b4, a1));
            example3.add(new Attack(c1, b1));
            example3.add(new Attack(c2, b1));
            example3.add(new Attack(c3, b2));
            example3.add(new Attack(c4, b3));
            example3.add(new Attack(d1, c1));
            example3.add(new Attack(d2, c2));
            example3.add(new Attack(d3, c3));
            example3.add(new Attack(e1, d1));

            // Example 4a, taken from Figure 2 in
            // [Matt, Toni. A game-theoretic measure of argument strength for abstract argumentation. JELIA 2008]
            DungTheory example4a = new DungTheory();
            Argument g = new Argument("g");
            example4a.add(a,b,c,d,e,f,g);
            example4a.add(new Attack(b, a));
            example4a.add(new Attack(c, a));
            example4a.add(new Attack(d, a));
            example4a.add(new Attack(f, a));
            example4a.add(new Attack(e, d));
            example4a.add(new Attack(g, f));

            // Example 4b, taken from Figure 4 in
            // [Matt, Toni. A game-theoretic measure of argument strength for abstract argumentation. JELIA 2008]
            DungTheory example4b = new DungTheory();
            example4b.add(a,b,c,d,e,f);
            example4b.add(new Attack(a, b));
            example4b.add(new Attack(c, b));
            example4b.add(new Attack(d, e));
            example4b.add(new Attack(e, f),new Attack(e, b));
            example4b.add(new Attack(f, e));

            // Example 4c, taken from Figure 4 in
            // [Matt, Toni. A game-theoretic measure of argument strength for abstract argumentation. JELIA 2008]
            DungTheory example4c = new DungTheory();
            example4c.add(a,b,c,d,e,f);
            example4c.add(new Attack(a, b), new Attack(a, e));
            example4c.add(new Attack(c, b));
            example4c.add(new Attack(d, e));
            example4c.add(new Attack(e, f), new Attack(e, e), new Attack(e, b));
            example4c.add(new Attack(f, e), new Attack(f, b));

            // Example 5, taken from taken from Figure 2.4 in
            // [Delobelle, Jerome. Ranking-based Semantics for Abstract Argumentation. 2017]
            DungTheory example5 = new DungTheory();
            Argument h = new Argument("h");
            Argument i = new Argument("i");
            Argument j = new Argument("j");
            example5.add(a,b,c,d,e,f,g,h,i,j);
            example5.add(new Attack(a,b));
            example5.add(new Attack(b,c), new Attack(b,f));
            example5.add(new Attack(d,g), new Attack(d,f));
            example5.add(new Attack(e,h), new Attack(e,d), new Attack(e,i));
            example5.add(new Attack(h,g));
            example5.add(new Attack(j,i));
            
            var theories = List.of(example1, example2, example3, example4a, example4b, example4c, example5);
            
        
                 for (DungTheory theory : theories) {

                     System.out.println("Beispiel Nr."+theories.indexOf(theory));

                     //STABLE

                     System.out.println("STABLE");

                     // Ranking-Based Extension semantics
                     RankingBasedExtensionReasoner rankingBasedExtensionReasonerStable = new RankingBasedExtensionReasoner(Semantics.STABLE_SEMANTICS,
                             RankingBasedExtensionReasoner.RankingSemantics.CATEGORIZER);
                     System.out.println(rankingBasedExtensionReasonerStable.getClass().getSimpleName());
                     var catRankingBasedStableExtensions = rankingBasedExtensionReasonerStable.getModels(theory);
                     System.out.println("RB-ST-Extensions" + catRankingBasedStableExtensions);
                     var dungStableReasoner = new SimpleStableReasoner();
                     var dungStableExtensions = dungStableReasoner.getModels(theory);
                     System.out.println("DUNG-ST-Extensions" + dungStableExtensions);
                     //assertEquals(dungStableExtensions.size(), catRankingBasedStableExtensions.size());

                     // Strategy-Based Extension semantics
                     RankingBasedExtensionReasoner strategyBasedRankingStableReasoner = new RankingBasedExtensionReasoner(Semantics.STABLE_SEMANTICS,
                             RankingBasedExtensionReasoner.RankingSemantics.STRATEGY);
                     System.out.println(strategyBasedRankingStableReasoner.getClass().getSimpleName());
                     var strategyRankingBasedStableExtensions = strategyBasedRankingStableReasoner.getModels(theory);
                     System.out.println("RB-ST-Extensions" + strategyRankingBasedStableExtensions);
                     System.out.println("DUNG-ST-Extensions" + dungStableExtensions);
                     //assertEquals(dungStableExtensions.size(), strategyRankingBasedStableExtensions.size());

                     // SAF-Ranking Extension semantics
                     RankingBasedExtensionReasoner safRankingStableReasoner = new RankingBasedExtensionReasoner(Semantics.STABLE_SEMANTICS,
                             RankingBasedExtensionReasoner.RankingSemantics.SAF);
                     System.out.println(safRankingStableReasoner.getClass().getSimpleName());
                     var safStableExtensions = safRankingStableReasoner.getModels(theory);
                     System.out.println("RB-ST-Extensions" + safStableExtensions);
                     System.out.println("DUNG-ST-Extensions" + dungStableExtensions);
                     //assertEquals(dungStableExtensions.size(), safStableExtensions.size());

                     // Counting-Ranking Extension semantics
                     RankingBasedExtensionReasoner countingRankingStableReasoner = new RankingBasedExtensionReasoner(Semantics.STABLE_SEMANTICS,
                             RankingBasedExtensionReasoner.RankingSemantics.COUNTING);
                     System.out.println(countingRankingStableReasoner.getClass().getSimpleName());
                     var countingStableExtensions = countingRankingStableReasoner.getModels(theory);
                     System.out.println("RB-ST-Extensions" + countingStableExtensions);
                     System.out.println("DUNG-ST-Extensions" + dungStableExtensions);
                     //assertEquals(dungStableExtensions.size(), countingStableExtensions.size());


                     //GROUNDED

                     System.out.println("GROUNDED");

                     // Ranking-Based Extension semantics
                     RankingBasedExtensionReasoner rankingBasedExtensionGroundedReasoner = new RankingBasedExtensionReasoner(Semantics.GROUNDED_SEMANTICS,
                             RankingBasedExtensionReasoner.RankingSemantics.CATEGORIZER);
                     System.out.println(rankingBasedExtensionGroundedReasoner.getClass().getSimpleName());
                     var catRankingBasedGroundedExtensions = rankingBasedExtensionGroundedReasoner.getModels(theory);
                     System.out.println("RB-GR-Extensions" + catRankingBasedGroundedExtensions);
                     var dungGroundedReasoner = new SimpleGroundedReasoner();
                     var dungGroundedExtensions = dungGroundedReasoner.getModels(theory);
                     System.out.println("DUNG-GR-Extensions" + dungGroundedExtensions);
                     //assertEquals(dungGroundedExtensions.size(), catRankingBasedGroundedExtensions.size());

                     // Strategy-Based Extension semantics
                     RankingBasedExtensionReasoner strategyBasedRankingReasoner = new RankingBasedExtensionReasoner(Semantics.GROUNDED_SEMANTICS,
                             RankingBasedExtensionReasoner.RankingSemantics.STRATEGY);
                     System.out.println(strategyBasedRankingReasoner.getClass().getSimpleName());
                     var strategyRankingBasedGroundedExtensions = strategyBasedRankingReasoner.getModels(theory);
                     System.out.println("RB-GR-Extensions" + strategyRankingBasedGroundedExtensions);
                     System.out.println("DUNG-GR-Extensions" + dungGroundedExtensions);
                     //assertEquals(dungGroundedExtensions.size(), strategyRankingBasedGroundedExtensions.size());

                     // SAF-Ranking Extension semantics
                     RankingBasedExtensionReasoner safRankingReasoner = new RankingBasedExtensionReasoner(Semantics.GROUNDED_SEMANTICS,
                             RankingBasedExtensionReasoner.RankingSemantics.SAF);
                     System.out.println(safRankingReasoner.getClass().getSimpleName());
                     var safGroundedExtensions = safRankingReasoner.getModels(theory);
                     System.out.println("RB-GR-Extensions" + safGroundedExtensions);
                     System.out.println("DUNG-GR-Extensions" + dungGroundedExtensions);
                     //assertEquals(dungGroundedExtensions.size(), safGroundedExtensions.size());

                     // Counting-Ranking Extension semantics
                     RankingBasedExtensionReasoner countingRankingReasoner = new RankingBasedExtensionReasoner(Semantics.GROUNDED_SEMANTICS,
                             RankingBasedExtensionReasoner.RankingSemantics.COUNTING);
                     System.out.println(countingRankingReasoner.getClass().getSimpleName());
                     var countingGroundedExtensions = countingRankingReasoner.getModels(theory);
                     System.out.println("RB-GR-Extensions" + countingGroundedExtensions);
                     System.out.println("DUNG-GR-Extensions" + dungGroundedExtensions);
                     //assertEquals(dungGroundedExtensions.size(), countingGroundedExtensions.size());


                     //PREFERRED
                     System.out.println("PREFERRED");

                     // Ranking-Based Extension semantics
                     RankingBasedExtensionReasoner rankingBasedExtensionPreferredReasoner = new RankingBasedExtensionReasoner(Semantics.PREFERRED_SEMANTICS,
                             RankingBasedExtensionReasoner.RankingSemantics.CATEGORIZER);
                     System.out.println(rankingBasedExtensionPreferredReasoner.getClass().getSimpleName());
                     var catRankingBasedPreferredExtensions = rankingBasedExtensionPreferredReasoner.getModels(theory);
                     System.out.println("RB-IDExtensions" + catRankingBasedPreferredExtensions);
                     var dungPreferredReasoner = new SimplePreferredReasoner();
                     var dungPreferredExtensions = dungPreferredReasoner.getModels(theory);
                     System.out.println("DUNG-PR-Extensions" + dungPreferredExtensions);
                     //assertEquals(dungPreferredExtensions.size(), catRankingBasedPreferredExtensions.size());

                     // Strategy-Based Extension semantics
                     RankingBasedExtensionReasoner strategyBasedRankingReasonerPreferred = new RankingBasedExtensionReasoner(Semantics.PREFERRED_SEMANTICS,
                             RankingBasedExtensionReasoner.RankingSemantics.STRATEGY);
                     System.out.println(strategyBasedRankingReasonerPreferred.getClass().getSimpleName());
                     var strategyRankingBasedPreferredExtensions = strategyBasedRankingReasonerPreferred.getModels(theory);
                     System.out.println("RB-PR-Extensions" + strategyRankingBasedPreferredExtensions);
                     System.out.println("DUNG-PR-Extensions" + dungPreferredExtensions);
                     //assertEquals(dungPreferredExtensions.size(), strategyRankingBasedPreferredExtensions.size());

                     // SAF-Ranking Extension semantics
                     RankingBasedExtensionReasoner safRankingReasonerPreferred = new RankingBasedExtensionReasoner(Semantics.PREFERRED_SEMANTICS,
                             RankingBasedExtensionReasoner.RankingSemantics.SAF);
                     System.out.println(safRankingReasonerPreferred.getClass().getSimpleName());
                     var safPreferredExtensions = safRankingReasonerPreferred.getModels(theory);
                     System.out.println("RB-PR-Extensions" + safPreferredExtensions);
                     System.out.println("DUNG-PR-Extensions" + dungPreferredExtensions);
                     //assertEquals(dungPreferredExtensions.size(), safPreferredExtensions.size());

                     // Counting-Ranking Extension semantics
                     RankingBasedExtensionReasoner countingRankingReasonerPreferred = new RankingBasedExtensionReasoner(Semantics.PREFERRED_SEMANTICS,
                             RankingBasedExtensionReasoner.RankingSemantics.COUNTING);
                     System.out.println(countingRankingReasonerPreferred.getClass().getSimpleName());
                     var countingPreferredExtensions = countingRankingReasonerPreferred.getModels(theory);
                     System.out.println("RB-PR-Extensions" + countingPreferredExtensions);
                     System.out.println("DUNG-PR-Extensions" + dungPreferredExtensions);
                     //assertEquals(dungPreferredExtensions.size(), countingPreferredExtensions.size());

                     //IDEAL
                     System.out.println("IDEAL");

                     // Ranking-Based Extension semantics
                     RankingBasedExtensionReasoner rankingBasedExtensionIdealReasoner = new RankingBasedExtensionReasoner(Semantics.IDEAL_SEMANTICS,
                             RankingBasedExtensionReasoner.RankingSemantics.CATEGORIZER);
                     System.out.println(rankingBasedExtensionIdealReasoner.getClass().getSimpleName());
                     var catRankingBasedIdealExtensions = rankingBasedExtensionIdealReasoner.getModels(theory);
                     System.out.println("RB-ID-Extensions" + catRankingBasedIdealExtensions);
                     var dungIdealReasoner = new SimpleIdealReasoner();
                     var dungIdealExtensions = dungIdealReasoner.getModels(theory);
                     System.out.println("DUNG-ID-Extensions" + dungIdealExtensions);
                     //assertEquals(dungIdealExtensions.size(), catRankingBasedIdealExtensions.size());

                     // Strategy-Based Extension semantics
                     RankingBasedExtensionReasoner strategyBasedRankingReasonerIdeal = new RankingBasedExtensionReasoner(Semantics.IDEAL_SEMANTICS,
                             RankingBasedExtensionReasoner.RankingSemantics.STRATEGY);
                     System.out.println(strategyBasedRankingReasonerIdeal.getClass().getSimpleName());
                     var strategyRankingBasedIdealExtensions = strategyBasedRankingReasonerIdeal.getModels(theory);
                     System.out.println("RB-ID-Extensions" + strategyRankingBasedIdealExtensions);
                     System.out.println("DUNG-ID-Extensions" + dungIdealExtensions);
                     //assertEquals(dungIdealExtensions.size(), strategyRankingBasedIdealExtensions.size());

                     // SAF-Ranking Extension semantics
                     RankingBasedExtensionReasoner safRankingReasonerIdeal = new RankingBasedExtensionReasoner(Semantics.IDEAL_SEMANTICS,
                             RankingBasedExtensionReasoner.RankingSemantics.SAF);
                     System.out.println(safRankingReasonerIdeal.getClass().getSimpleName());
                     var safIdealExtensions = safRankingReasonerIdeal.getModels(theory);
                     System.out.println("RB-ID-Extensions" + safIdealExtensions);
                     System.out.println("DUNG-ID-Extensions" + dungIdealExtensions);
                     //assertEquals(dungIdealExtensions.size(), safIdealExtensions.size());

                     // Counting-Ranking Extension semantics
                     RankingBasedExtensionReasoner countingRankingReasonerIdeal = new RankingBasedExtensionReasoner(Semantics.IDEAL_SEMANTICS,
                             RankingBasedExtensionReasoner.RankingSemantics.COUNTING);
                     System.out.println(countingRankingReasonerIdeal.getClass().getSimpleName());
                     var countingIdealExtensions = countingRankingReasonerIdeal.getModels(theory);
                     System.out.println("RB-ID-Extensions" + countingIdealExtensions);
                     System.out.println("DUNG-ID-Extensions" + dungIdealExtensions);
                     //assertEquals(dungIdealExtensions.size(), countingIdealExtensions.size());

                 }





    }


}
