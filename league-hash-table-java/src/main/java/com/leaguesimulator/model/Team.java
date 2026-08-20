package com.leaguesimulator.model;

/**
 * Represents a sports team participating in the league.
 * Maintains team statistics such as points, goals scored, goals conceded, and wins.
 */
public class Team {
    private final String id;
    private final String name;
    private int points;
    private int goalsFor;
    private int goalsAgainst;
    private int wins;

    /**
     * Constructs a Team with a generated unique ID.
     *
     * @param name Name of the team.
     */
    public Team(String name) {
        this.id = generateUniqueId();
        this.name = name;
        this.points = 0;
        this.goalsFor = 0;
        this.goalsAgainst = 0;
        this.wins = 0;
    }

    /**
     * Constructs a Team with a specified ID and name.
     *
     * @param id   Unique identifier of the team.
     * @param name Name of the team.
     */
    public Team(String id, String name) {
        this.id = id;
        this.name = name;
        this.points = 0;
        this.goalsFor = 0;
        this.goalsAgainst = 0;
        this.wins = 0;
    }

    /**
     * Generates a unique identifier based on current system nanosecond time.
     *
     * @return A unique ID string.
     */
    private String generateUniqueId() {
        return "ID-" + System.nanoTime();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public void setGoalsFor(int goalsFor) {
        this.goalsFor = goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public int getWins() {
        return wins;
    }

    public void incrementWins() {
        this.wins++;
    }

    /**
     * Calculates the goal difference (goals scored minus goals conceded).
     *
     * @return The net goal difference.
     */
    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    @Override
    public String toString() {
        return name + ", Points: " + points + ", GD: " + getGoalDifference();
    }
}
