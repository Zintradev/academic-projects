package com.leaguesimulator.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LeagueTest {
    private League league;

    @BeforeEach
    public void setUp() {
        league = new League();
    }

    @Test
    public void testAddTeamSuccess() {
        assertTrue(league.addTeam("T1", "Chelsea"));
        assertTrue(league.addTeam("T2", "Arsenal"));
        assertEquals(2, league.getTeamsList().size());
    }

    @Test
    public void testAddDuplicateTeamFails() {
        assertTrue(league.addTeam("T1", "Chelsea"));
        assertFalse(league.addTeam("T1", "Arsenal")); // Same ID
        assertEquals(1, league.getTeamsList().size());
    }

    @Test
    public void testSimulateLeagueEvenTeams() {
        league.addTeam("T1", "Chelsea");
        league.addTeam("T2", "Arsenal");
        league.addTeam("T3", "Liverpool");
        league.addTeam("T4", "Man City");

        List<Matchday> schedule = league.simulateLeague();

        // 4 teams -> (4-1) * 2 = 6 matchdays total
        assertEquals(6, schedule.size());

        // Each matchday should have N / 2 = 2 matches
        for (Matchday md : schedule) {
            assertEquals(2, md.getMatches().size());
        }

        // Check that final standings contains exactly the 4 real teams
        List<Team> standings = league.getStandings();
        assertEquals(4, standings.size());
        
        // Ensure no dummy team leaked
        for (Team team : standings) {
            assertNotEquals("BYE_DUMMY", team.getId());
            assertNotEquals("Bye", team.getName());
        }
    }

    @Test
    public void testSimulateLeagueOddTeams() {
        league.addTeam("T1", "Chelsea");
        league.addTeam("T2", "Arsenal");
        league.addTeam("T3", "Liverpool");
        league.addTeam("T4", "Man City");
        league.addTeam("T5", "Man United");

        List<Matchday> schedule = league.simulateLeague();

        // 5 teams -> internally becomes 6 teams (including Bye)
        // 6 teams -> (6-1) * 2 = 10 matchdays total
        assertEquals(10, schedule.size());

        // In each round, one team rests (the one playing the dummy Bye team).
        // So 6 / 2 = 3 matches, minus the Bye match = 2 active matches.
        for (Matchday md : schedule) {
            assertEquals(2, md.getMatches().size());
        }

        // Standings should list exactly 5 real teams (the dummy Bye team must be deleted)
        List<Team> standings = league.getStandings();
        assertEquals(5, standings.size());

        for (Team team : standings) {
            assertNotEquals("BYE_DUMMY", team.getId());
            assertNotEquals("Bye", team.getName());
        }
    }

    @Test
    public void testStandingsSorting() {
        league.addTeam("T1", "Team 1");
        league.addTeam("T2", "Team 2");
        league.addTeam("T3", "Team 3");

        Team t1 = league.getTeams().search("T1");
        Team t2 = league.getTeams().search("T2");
        Team t3 = league.getTeams().search("T3");

        // Set points
        t1.setPoints(10);
        t2.setPoints(12); // Highest points
        t3.setPoints(10); // Tied with t1 on points

        // Tie-breaker: Goal Difference (GD)
        t1.setGoalsFor(10);
        t1.setGoalsAgainst(5); // GD = +5
        
        t3.setGoalsFor(8);
        t3.setGoalsAgainst(6); // GD = +2

        List<Team> standings = league.getStandings();

        // Expected Order: Team 2 (12pts), Team 1 (10pts, GD +5), Team 3 (10pts, GD +2)
        assertEquals(t2, standings.get(0));
        assertEquals(t1, standings.get(1));
        assertEquals(t3, standings.get(2));
    }
}
