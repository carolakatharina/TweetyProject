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
package org.tweetyproject.arg.rankings.rankingbasedextension;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.rankingbasedextension.exactreasoner.ExactGeneralRankingBasedExtensionReasoner;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.postulates.PostulateEvaluationReport;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Summarises the results of a postulate evaluation.
 * 
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas
 */
public class DetailedEvaluationReport<S extends Formula> extends PostulateEvaluationReport {

	List<Collection<S>> allAfs = new ArrayList<Collection<S>>();
	List<Collection<S>> allExtensions = new ArrayList<Collection<S>>();



	public List<Map<Argument, BigDecimal>> getRankings() {
		return rankings;
	}

	List<Map<Argument, BigDecimal>> rankings = new ArrayList<>();


	public void setAllExtensions(List<Collection<S>> allExtensions) {
		this.allExtensions = allExtensions;
	}

	public void setAllAfs(List<Collection<S>> allAfs) {
		this.allAfs = allAfs;
	}

	public void addExtension(Collection<S> ext) {
		allExtensions.add(ext);
	}

	public void addRanking(Map<Argument, BigDecimal> entry) {
		rankings.add(entry);
	}

	public void addAf(Collection<S> af) {
		allAfs.add(af);
	}


	public List<Collection<S>> getAllExtensions() {
		return allExtensions;
	}

	public Collection<Collection<S>> getAllAfs() {
		return allAfs;
	}






	/**
	 * Creates a new evaluation report for the given approach and set of postulates
	 *
	 * @param ev   some approach
	 * @param list a set of postulates
	 */
	public DetailedEvaluationReport(ExactGeneralRankingBasedExtensionReasoner ev, List list) {
		super(ev, list);
	}



	/**
	 * @return an easy-to-read string representation of the report in which
	 * the results are ordered alphabetically by postulate name.
	 */
	public void printForSimple(ExactGeneralRankingBasedExtensionReasoner.RankingSemantics semantics) throws IOException {

		var headers = "AF,"+"Ext,"+"Ranking,"+"grounded,"+"\n";



		createCsv(headers, semantics );
	}
	public void createCsv(String headers, ExactGeneralRankingBasedExtensionReasoner.RankingSemantics semantics) throws IOException {
		BufferedWriter writer = Files.newBufferedWriter(Paths.get(".\\org-tweetyproject-arg-rankings\\src\\main\\java\\org\\tweetyproject\\arg\\rankings\\rankingbasedextension\\evaluation\\data\\detailsemantics_evaluation2" +
				Math.random() + semantics+".csv"));

		CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
				.setHeader(headers)
				.build();

		var reasoner = new SimpleGroundedReasoner();

		try (final CSVPrinter printer = new CSVPrinter(writer, csvFormat)) {
			try {
				for (int i = 0; i < this.allAfs.size(); i++) {
					var af = (DungTheory) this.allAfs.get(i);
					var ranking = this.rankings.get(i);
					var grounded = reasoner.getModel(af);
					var ext = (Extension) this.getAllExtensions().get(i);
					printer.printRecord(af, ext,ranking, grounded);
				}

			} catch (IOException e) {
				throw new RuntimeException(e);

			}

			printer.flush();
		}
	}



}
