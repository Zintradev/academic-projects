package com.leaguesimulator.model;

/**
 * A custom Hash Table implementation that stores Teams.
 * Uses quadratic probing for collision resolution.
 */
public class HashTable {
    private static final int TABLE_SIZE = 29; // Chosen prime number for hash table capacity
    private static final double LOAD_FACTOR_THRESHOLD = 0.8;

    private int size;
    private double loadFactor;
    private final Team[] table;

    /**
     * Initializes the hash table with the default table size.
     */
    public HashTable() {
        this.table = new Team[TABLE_SIZE];
        this.size = 0;
        this.loadFactor = 0.0;
    }

    /**
     * Computes the hash code index for a given string key using quadratic probing.
     *
     * @param key The key to hash (typically the Team ID).
     * @return The computed table index.
     */
    public int hash(String key) {
        int i = 0;
        long hashValue = stringToLongHash(key);
        int index = (int) (hashValue % TABLE_SIZE);

        // Quadratic probing loop to resolve collisions
        while (table[index] != null && !table[index].getId().equals(key)) {
            i++;
            index = (index + i * i) % TABLE_SIZE; // Treats array as circular
        }
        return index;
    }

    /**
     * Transforms a string key into a long hash value.
     *
     * @param key The string key.
     * @return A long value representation of the string.
     */
    private long stringToLongHash(String key) {
        long hashValue = 0;
        int length = Math.min(10, key.length());
        for (int j = 0; j < length; j++) {
            hashValue = hashValue * 29 + (int) key.charAt(j);
        }

        if (hashValue < 0) {
            hashValue = -hashValue;
        }
        return hashValue;
    }

    /**
     * Inserts a Team into the hash table.
     *
     * @param team The team to insert.
     */
    public void insert(Team team) {
        if (team == null || team.getId() == null) {
            throw new IllegalArgumentException("Team and Team ID cannot be null");
        }
        int index = hash(team.getId());
        if (table[index] == null) {
            size++;
        }
        table[index] = team;
        loadFactor = (double) size / TABLE_SIZE;
    }

    /**
     * Searches for a Team by its unique ID.
     *
     * @param key The team ID.
     * @return The Team object if found, null otherwise.
     */
    public Team search(String key) {
        if (key == null) {
            return null;
        }
        int index = hash(key);
        return table[index];
    }

    /**
     * Removes a Team from the hash table by its unique ID.
     *
     * @param key The team ID.
     */
    public void delete(String key) {
        if (key == null) {
            return;
        }
        int index = hash(key);
        if (table[index] != null) {
            table[index] = null;
            size--;
            loadFactor = (double) size / TABLE_SIZE;
        }
    }

    /**
     * Exposes the backing table array.
     *
     * @return Array of Teams.
     */
    public Team[] getTeams() {
        return table;
    }

    public int size() {
        return size;
    }

    public double getLoadFactor() {
        return loadFactor;
    }

    /**
     * Checks if the load factor exceeds the threshold.
     *
     * @return true if the load factor is greater than 80%, false otherwise.
     */
    public boolean isLoadFactorHigh() {
        return loadFactor > LOAD_FACTOR_THRESHOLD;
    }

    public static int getTableSize() {
        return TABLE_SIZE;
    }
}
