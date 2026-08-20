package com.leaguesimulator.model;

/**
 * Represents a match between two teams with goals scored by each team.
 */
public class Match {
    private final Team homeTeam;
    private final Team awayTeam;
    private final int homeGoals;
    private final int awayGoals;

    /**
     * Constructs a Match result.
     *
     * @param homeTeam  The home team.
     * @param awayTeam  The away team.
     * @param homeGoals Goals scored by the home team.
     * @param awayGoals Goals scored by the away team.
     */
    public Match(Team homeTeam, Team awayTeam, int homeGoals, int awayGoals) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public int getAwayGoals() {
        return awayGoals;
    }

    public boolean isDraw() {
        return homeGoals == awayGoals;
    }

    public Team getWinner() {
        if (homeGoals > awayGoals) {
            return homeTeam;
        } else if (awayGoals > homeGoals) {
            return awayTeam;
        }
        return null; // Draw
    }

    public Team getLoser() {
        if (homeGoals > awayGoals) {
            return awayTeam;
        } else if (awayGoals > homeGoals) {
            return homeTeam;
        }
        return null; // Draw
    }

    @Override
    public String toString() {
        return homeTeam.getName() + " " + homeGoals + " - " + awayGoals + " " + awayTeam.getName();
    }
}
