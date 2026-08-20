#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "storage.h"

int storage_save_to_file(const CharacterManager *manager, const char *filename) {
    if (manager == NULL || filename == NULL) {
        return 0;
    }

    FILE *file = fopen(filename, "w");
    if (file == NULL) {
        return 0;
    }

    for (int i = 0; i < manager->count; i++) {
        // Save using pipe '|' delimiter to support character names with spaces
        fprintf(file, "%s|%d|%d\n",
                manager->characters[i].character_name,
                manager->characters[i].release_year,
                manager->characters[i].genre);
    }

    fclose(file);
    return 1;
}

int storage_load_from_file(CharacterManager *manager, const char *filename) {
    if (manager == NULL || filename == NULL) {
        return -1;
    }

    FILE *file = fopen(filename, "r");
    if (file == NULL) {
        return -1; // File does not exist or can't be read
    }

    char line[256];
    int loaded_count = 0;

    while (fgets(line, sizeof(line), file) != NULL) {
        // Remove trailing newline character(s)
        line[strcspn(line, "\r\n")] = '\0';

        // Parse fields using pipe '|' delimiter
        char *name_token = strtok(line, "|");
        char *year_token = strtok(NULL, "|");
        char *genre_token = strtok(NULL, "|");

        if (name_token != NULL && year_token != NULL && genre_token != NULL) {
            int year = atoi(year_token);
            int genre_val = atoi(genre_token);

            if (character_manager_add(manager, name_token, year, (GameGenre)genre_val)) {
                loaded_count++;
            }
        }
    }

    fclose(file);
    return loaded_count;
}
