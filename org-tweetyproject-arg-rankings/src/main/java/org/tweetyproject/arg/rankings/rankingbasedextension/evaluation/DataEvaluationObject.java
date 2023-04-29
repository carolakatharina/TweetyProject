package org.tweetyproject.arg.rankings.rankingbasedextension.evaluation;

public class DataEvaluationObject {

    final String graphname;
    final int numberOfSelfLoops;

    final boolean hasCycles;

    final boolean hasOddCycles;

    final int numberOfArguments;

    final int numberOfAttacks;

    final int numberSCCs;

    public DataEvaluationObject(String graphname, int numberOfSelfLoops, boolean hasCycles, boolean hasOddCycles, int numberOfArguments, int numberOfAttacks, int numberSCCs) {
        this.graphname = graphname;
        this.numberOfSelfLoops = numberOfSelfLoops;
        this.hasCycles = hasCycles;
        this.hasOddCycles = hasOddCycles;
        this.numberOfArguments = numberOfArguments;
        this.numberSCCs = numberSCCs;
        this.numberOfAttacks = numberOfAttacks;
    }

    public int getNumberOfSelfLoops() {
        return numberOfSelfLoops;
    }

    public boolean isHasCycles() {
        return hasCycles;
    }


    public int getNumberOfArguments() {
        return numberOfArguments;
    }

    public int getNumberOfAttacks() {
        return numberOfAttacks;
    }

    public boolean isHasOddCycles() {
        return hasOddCycles;
    }

    public int getNumberSCCs() {
        return numberSCCs;
    }
}
