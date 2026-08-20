package com.leaguesimulator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a matchday (round) of the league, containing a list of matches.
 */
public class Matchday {
    private final int number;
    private final boolean firstLeg; // True if it's the home phase, false if it's the away phase (return match)
    private final List<Match> matches;

    /**
     * Constructs a Matchday.
     *
     * @param number   The round number.
     * @param firstLeg Whether this is the first leg of the season.
     */
    public Matchday(int number, boolean firstLeg) {
        this.number = number;
        this.firstLeg = firstLeg;
        this.matches = new ArrayList<>();
    }

    public int getNumber() {
        return number;
    }

    public boolean isFirstLeg() {
        return firstLeg;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void addMatch(Match match) {
        this.matches.add(match);
    }
}
