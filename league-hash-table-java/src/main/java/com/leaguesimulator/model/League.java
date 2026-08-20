package com.leaguesimulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Manages the league operations including registering teams,
 * simulating matches using a round-robin schedule, and computing standings.
 */
public class League {
    private final HashTable teams;
    private final Random random;

    /**
     * Initializes the League with an empty HashTable for teams.
     */
    public League() {
        this.teams = new HashTable();
        this.random = new Random();
    }

    /**
     * Registers a new team into the league using its specified ID and name.
     *
     * @param id   The unique team ID.
     * @param name The name of the team.
     * @return true if registration was successful, false if the ID already exists.
     */
    public boolean addTeam(String id, String name) {
        if (teams.search(id) != null) {
            return false; // Team ID already exists
        }
        Team newTeam = new Team(id, name);
        teams.insert(newTeam);
        return true;
    }

    /**
     * Simulates the entire league season (First Leg and Second Leg)
     * using a round-robin scheduling algorithm (Berger tables / circle method).
     *
     * @return A list of Matchday objects containing all rounds and match results.
     */
    public List<Matchday> simulateLeague() {
        List<Team> activeTeams = getTeamsList();
        int numTeams = activeTeams.size();

        if (numTeams < 2) {
            throw new IllegalStateException("At least two teams are required to simulate a league");
        }

        // If the number of teams is odd, we add a dummy "Bye" team
        boolean hasOddTeams = (numTeams % 2 != 0);
        if (hasOddTeams) {
            activeTeams.add(new Team("BYE_DUMMY", "Bye"));
            numTeams++;
        }

        List<Matchday> seasonSchedule = new ArrayList<>();

        // Phase 1: First Leg (Ida)
        List<Matchday> firstLeg = generateMatchdays(activeTeams, numTeams, true);
        seasonSchedule.addAll(firstLeg);

        // Phase 2: Second Leg (Vuelta)
        // Shuffling the list creates a fresh scheduling order for the return leg
        Collections.shuffle(activeTeams);
        List<Matchday> secondLeg = generateMatchdays(activeTeams, numTeams, false);
        seasonSchedule.addAll(secondLeg);

        // Clean up: safely remove the dummy "Bye" team so it doesn't affect final standings or size
        if (hasOddTeams) {
            activeTeams.removeIf(team -> "BYE_DUMMY".equals(team.getId()));
        }

        return seasonSchedule;
    }

    /**
     * Generates matchdays for a specific phase of the season.
     */
    private List<Matchday> generateMatchdays(List<Team> teamsList, int numTeams, boolean isFirstLeg) {
        List<Matchday> phaseMatchdays = new ArrayList<>();
        List<Team> rotation = new ArrayList<>(teamsList);
        boolean invertHomeAway = false;

        // For N teams, a round-robin tournament has N - 1 rounds
        for (int round = 1; round < numTeams; round++) {
            Matchday matchday = new Matchday(round, isFirstLeg);

            for (int i = 0; i < numTeams / 2; i++) {
                Team home = rotation.get(i);
                Team away = rotation.get(numTeams - 1 - i);

                // Skip matches involving the dummy "Bye" team
                if ("BYE_DUMMY".equals(home.getId()) || "BYE_DUMMY".equals(away.getId())) {
                    continue;
                }

                // Alternate home/away sides to balance schedules
                if (invertHomeAway) {
                    Team temp = home;
                    home = away;
                    away = temp;
                }

                // Simulate score using random goals (0-7)
                int homeGoals = random.nextInt(8);
                int awayGoals = random.nextInt(8);

                Match match = new Match(home, away, homeGoals, awayGoals);
                updateResults(match);
                matchday.addMatch(match);
            }

            phaseMatchdays.add(matchday);

            // Rotate teams (keeping the first team fixed)
            rotateTeams(rotation);
            invertHomeAway = !invertHomeAway;
        }

        return phaseMatchdays;
    }

    /**
     * Rotates elements in the list keeping the first element fixed.
     * Used for round-robin schedule generation.
     */
    private void rotateTeams(List<Team> teamsList) {
        if (teamsList.size() <= 2) {
            return;
        }
        Team fixed = teamsList.get(0);
        Team last = teamsList.remove(teamsList.size() - 1);
        teamsList.add(1, last);
    }

    /**
     * Updates statistical results (points, wins, goals) for both teams in a match.
     */
    private void updateResults(Match match) {
        Team home = match.getHomeTeam();
        Team away = match.getAwayTeam();
        int homeGoals = match.getHomeGoals();
        int awayGoals = match.getAwayGoals();

        home.setGoalsFor(home.getGoalsFor() + homeGoals);
        home.setGoalsAgainst(home.getGoalsAgainst() + awayGoals);

        away.setGoalsFor(away.getGoalsFor() + awayGoals);
        away.setGoalsAgainst(away.getGoalsAgainst() + homeGoals);

        if (homeGoals > awayGoals) {
            home.setPoints(home.getPoints() + 3);
            home.incrementWins();
        } else if (awayGoals > homeGoals) {
            away.setPoints(away.getPoints() + 3);
            away.incrementWins();
        } else {
            home.setPoints(home.getPoints() + 1);
            away.setPoints(away.getPoints() + 1);
        }
    }

    /**
     * Retrives all registered teams from the HashTable as a List.
     */
    public List<Team> getTeamsList() {
        List<Team> list = new ArrayList<>();
        Team[] table = teams.getTeams();
        for (Team team : table) {
            if (team != null) {
                list.add(team);
            }
        }
        return list;
    }

    /**
     * Sorts and returns all registered teams based on league standings:
     * 1. Total Points (Descending)
     * 2. Goal Difference (Descending)
     * 3. Goals Scored (Descending)
     *
     * @return Sorted list of teams.
     */
    public List<Team> getStandings() {
        List<Team> standings = getTeamsList();
        standings.sort((t1, t2) -> {
            if (t1.getPoints() != t2.getPoints()) {
                return Integer.compare(t2.getPoints(), t1.getPoints());
            } else if (t1.getGoalDifference() != t2.getGoalDifference()) {
                return Integer.compare(t2.getGoalDifference(), t1.getGoalDifference());
            } else {
                return Integer.compare(t2.getGoalsFor(), t1.getGoalsFor());
            }
        });
        return standings;
    }

    public HashTable getTeams() {
        return teams;
    }
}
