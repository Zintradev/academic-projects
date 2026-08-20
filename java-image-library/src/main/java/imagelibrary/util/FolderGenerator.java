package imagelibrary.util;

import java.io.File;
import java.util.Random;

/**
 * Utility class for generating random folder structures.
 * Creates hierarchical directory structures with configurable depth and branching.
 */
public class FolderGenerator {
    private static final String[] FOLDER_NAMES = {"Photos", "Trips", "Events", "Family", "Work"};
    
    /**
     * Generates a random folder structure within the specified base directory. 
     * @param baseFolder The root directory where the structure will be created
     * @param maxLevels Maximum depth of the folder hierarchy
     * @param maxFoldersPerLevel Maximum number of folders per level
     * @param rand Random number generator for structure randomization
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public static void generateStructure(File baseFolder, int maxLevels, int maxFoldersPerLevel, Random rand) {
        if (baseFolder == null || maxLevels <= 0 || maxFoldersPerLevel <= 0 || rand == null) {
            throw new IllegalArgumentException("Invalid parameters");
        }
        
        if (!baseFolder.exists()) {
            baseFolder.mkdirs();
        }
        
        generateRecursive(baseFolder, maxLevels, maxFoldersPerLevel, rand, 0);
    }

    /**
     * Recursively generates folders and subfolders.
     * @param currentFolder The parent folder for this recursion level
     * @param remainingLevels How many more levels deep to generate
     * @param maxFolders Maximum number of folders to create at this level
     * @param rand Random number generator for structure randomization
     * @param currentLevel The current depth level (0-based)
     */
    private static void generateRecursive(File currentFolder, int remainingLevels, int maxFolders, Random rand, int currentLevel) {
        if (remainingLevels <= 0) return;

        int folderCount = rand.nextInt(maxFolders) + 1;
        for (int i = 0; i < folderCount; i++) {
            String name = FOLDER_NAMES[rand.nextInt(FOLDER_NAMES.length)] 
                         + "_" + System.currentTimeMillis() + rand.nextInt(1000);
            File newFolder = new File(currentFolder, name);
            newFolder.mkdir();
            
            if (rand.nextDouble() < 0.7) {
                generateRecursive(newFolder, remainingLevels - 1, maxFolders, rand, currentLevel + 1);
            }
        }
    }
}