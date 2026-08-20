package com.leaguesimulator.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HashTableTest {
    private HashTable hashTable;

    @BeforeEach
    public void setUp() {
        hashTable = new HashTable();
    }

    @Test
    public void testInsertAndSearch() {
        Team t1 = new Team("E1", "Real Madrid");
        Team t2 = new Team("E2", "FC Barcelona");

        hashTable.insert(t1);
        hashTable.insert(t2);

        assertEquals(2, hashTable.size());
        assertEquals(t1, hashTable.search("E1"));
        assertEquals(t2, hashTable.search("E2"));
    }

    @Test
    public void testSearchNonExistent() {
        assertNull(hashTable.search("E99"));
    }

    @Test
    public void testDelete() {
        Team t1 = new Team("E1", "Real Madrid");
        hashTable.insert(t1);

        assertEquals(1, hashTable.size());
        assertEquals(t1, hashTable.search("E1"));

        hashTable.delete("E1");

        assertEquals(0, hashTable.size());
        assertNull(hashTable.search("E1"));
    }

    @Test
    public void testCollisionResolution() {
        // Find two IDs that generate a collision under table size 29 and transforms
        // Let's insert multiple items to trigger collisions and ensure quadratic probing resolves them.
        Team[] teams = new Team[15];
        for (int i = 0; i < 15; i++) {
            teams[i] = new Team("E" + i, "Team " + i);
            hashTable.insert(teams[i]);
        }

        assertEquals(15, hashTable.size());
        
        // Ensure all teams can be searched and retrieved correctly despite collisions
        for (int i = 0; i < 15; i++) {
            Team retrieved = hashTable.search("E" + i);
            assertNotNull(retrieved);
            assertEquals("Team " + i, retrieved.getName());
        }
    }

    @Test
    public void testLoadFactorWarning() {
        assertFalse(hashTable.isLoadFactorHigh());

        // Insert 24 teams (24/29 = 0.827) to trigger load factor > 80%
        for (int i = 0; i < 24; i++) {
            hashTable.insert(new Team("E" + i, "Team " + i));
        }

        assertTrue(hashTable.isLoadFactorHigh());
        assertTrue(hashTable.getLoadFactor() > 0.8);
    }
}
