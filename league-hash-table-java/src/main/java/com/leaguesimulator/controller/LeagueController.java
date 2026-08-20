package com.leaguesimulator.controller;

import com.leaguesimulator.model.League;
import com.leaguesimulator.model.Matchday;
import com.leaguesimulator.model.Team;
import com.leaguesimulator.view.ConsoleView;

import java.util.List;

/**
 * Coordinates user actions, business logic, and UI updates.
 * Acts as the Controller in the MVC architecture.
 */
public class LeagueController {
    private final League league;
    private final ConsoleView view;

    /**
     * Constructs the LeagueController and populates initial sample data.
     *
     * @param league The League model.
     * @param view   The ConsoleView.
     */
    public LeagueController(League league, ConsoleView view) {
        this.league = league;
        this.view = view;
        seedSampleTeams();
    }

    /**
     * Seeds the league with initial teams for easier testing.
     */
    private void seedSampleTeams() {
        league.addTeam("E1", "Real Madrid");
        league.addTeam("E2", "FC Barcelona");
        league.addTeam("E3", "Atlético Madrid");
        league.addTeam("E4", "Sevilla FC");
        league.addTeam("E5", "Valencia CF");
        league.addTeam("E6", "Real Betis");
        league.addTeam("E7", "Real Murcia");
        league.addTeam("E8", "Málaga CF");
    }

    /**
     * Runs the main interactive menu loop.
     */
    public void start() {
        boolean exit = false;
        while (!exit) {
            view.printMenu();
            int choice = view.promptInt("");

            switch (choice) {
                case 1:
                    insertTeam();
                    break;
                case 2:
                    runSimulation();
                    exit = true; // Simulation completes the program loop in original requirement
                    break;
                default:
                    view.printMessage("Invalid option. Please try again.");
            }
        }
    }

    /**
     * Prompts for and inserts a new team into the league.
     */
    private void insertTeam() {
        String id = view.promptString("Enter Team ID (e.g., E9): ");
        if (id.isEmpty()) {
            view.printError("Team ID cannot be empty.");
            return;
        }

        String name = view.promptString("Enter Team Name: ");
        if (name.isEmpty()) {
            view.printError("Team Name cannot be empty.");
            return;
        }

        boolean success = league.addTeam(id, name);
        if (success) {
            view.printMessage("Team '" + name + "' registered successfully.");
            
            // Check custom hash table load factor warning (Clean Code design)
            if (league.getTeams().isLoadFactorHigh()) {
                view.printWarning("The Hash Table load factor is currently "
                        + String.format("%.2f", league.getTeams().getLoadFactor())
                        + " (exceeds 80%). Consider increasing table capacity!");
            }
        } else {
            view.printError("A team with ID '" + id + "' is already registered.");
        }
    }

    /**
     * Runs the season simulation and displays all schedules and standings.
     */
    private void runSimulation() {
        int teamCount = league.getTeamsList().size();
        if (teamCount < 2) {
            view.printError("At least two teams are required to start the simulation.");
            return;
        }

        view.printSectionHeader("STARTING LEAGUE SIMULATION");
        
        try {
            List<Matchday> schedule = league.simulateLeague();

            // Display match details grouped by phases
            view.printSectionHeader("FIRST LEG PHASE");
            schedule.stream()
                    .filter(Matchday::isFirstLeg)
                    .forEach(view::printMatchday);

            view.printSectionHeader("SECOND LEG PHASE (RETURN matches)");
            schedule.stream()
                    .filter(matchday -> !matchday.isFirstLeg())
                    .forEach(view::printMatchday);

            // Display final league standing table
            List<Team> standings = league.getStandings();
            view.printStandings(standings);

        } catch (Exception e) {
            view.printError("An error occurred during league simulation: " + e.getMessage());
        }
    }
}
