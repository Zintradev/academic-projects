package com.leaguesimulator.view;

import com.leaguesimulator.model.Match;
import com.leaguesimulator.model.Matchday;
import com.leaguesimulator.model.Team;

import java.util.List;
import java.util.Scanner;

/**
 * Handles all console interactions, formatting, and input collection.
 * Serves as the View layer in the MVC architecture.
 */
public class ConsoleView {
    private final Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prints the primary user option menu.
     */
    public void printMenu() {
        System.out.println("\n==================================");
        System.out.println("      LEAGUE SIMULATOR MENU       ");
        System.out.println("==================================");
        System.out.println("1. Insert Team");
        System.out.println("2. Run League Simulation");
        System.out.print("Select an option: ");
    }

    /**
     * Prompts the user for a text string.
     */
    public String promptString(String promptText) {
        System.out.print(promptText);
        return scanner.nextLine().trim();
    }

    /**
     * Prompts the user for an integer option.
     */
    public int promptInt(String promptText) {
        System.out.print(promptText);
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a valid number: ");
            scanner.next(); // Clear invalid input
        }
        int option = scanner.nextInt();
        scanner.nextLine(); // Clear the newline character from buffer
        return option;
    }

    /**
     * Prints a general status message.
     */
    public void printMessage(String message) {
        System.out.println(message);
    }

    /**
     * Prints an error message.
     */
    public void printError(String error) {
        System.err.println("Error: " + error);
    }

    /**
     * Prints a warning message.
     */
    public void printWarning(String warning) {
        System.out.println("\n#### WARNING: " + warning);
    }

    /**
     * Prints the matches of a specific matchday.
     *
     * @param matchday The Matchday to display.
     */
    public void printMatchday(Matchday matchday) {
        String legType = matchday.isFirstLeg() ? "First Leg (Ida)" : "Second Leg (Vuelta)";
        System.out.println("\n--- Round " + matchday.getNumber() + " (" + legType + ") ---");
        for (Match match : matchday.getMatches()) {
            System.out.printf("  %-18s %d - %d %s\n",
                    match.getHomeTeam().getName(),
                    match.getHomeGoals(),
                    match.getAwayGoals(),
                    match.getAwayTeam().getName());
        }
    }

    /**
     * Prints the final standings table.
     *
     * @param standings List of Teams sorted by standings.
     */
    public void printStandings(List<Team> standings) {
        System.out.println("\n==========================================================================");
        System.out.println("                             FINAL STANDINGS                              ");
        System.out.println("==========================================================================");
        System.out.printf("%-4s %-20s %-8s %-6s %-12s %-15s %-15s\n",
                "Pos", "Team Name", "Points", "Wins", "Goals For", "Goals Against", "Goal Difference");
        System.out.println("--------------------------------------------------------------------------");

        int pos = 1;
        for (Team team : standings) {
            System.out.printf("%-4d %-20s %-8d %-6d %-12d %-15d %+15d\n",
                    pos++,
                    team.getName(),
                    team.getPoints(),
                    team.getWins(),
                    team.getGoalsFor(),
                    team.getGoalsAgainst(),
                    team.getGoalDifference());
        }
        System.out.println("==========================================================================\n");
    }

    public void printSectionHeader(String header) {
        System.out.println("\n>>> " + header + " <<<");
    }
}
