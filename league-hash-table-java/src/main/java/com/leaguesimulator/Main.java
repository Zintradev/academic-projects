package com.leaguesimulator;

import com.leaguesimulator.controller.LeagueController;
import com.leaguesimulator.model.League;
import com.leaguesimulator.view.ConsoleView;

/**
 * Entry point for the League Simulator application.
 */
public class Main {
    public static void main(String[] args) {
        // Instantiate the MVC layers
        League leagueModel = new League();
        ConsoleView consoleView = new ConsoleView();
        LeagueController controller = new LeagueController(leagueModel, consoleView);

        // Run the program
        controller.start();
    }
}
