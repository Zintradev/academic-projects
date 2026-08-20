#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include "character.h"

void character_manager_init(CharacterManager *manager) {
    if (manager != NULL) {
        manager->characters = NULL;
        manager->count = 0;
    }
}

int character_manager_add(CharacterManager *manager, const char *name, int release_year, GameGenre genre) {
    if (manager == NULL || name == NULL) {
        return 0;
    }

    // Allocate / Reallocate memory for characters array
    int new_count = manager->count + 1;
    Character *temp = (Character *)realloc(manager->characters, new_count * sizeof(Character));
    if (temp == NULL) {
        // realloc failed; original pointer remains valid
        return 0;
    }
    manager->characters = temp;

    // Allocate memory for the character name
    char *name_copy = (char *)malloc((strlen(name) + 1) * sizeof(char));
    if (name_copy == NULL) {
        return 0;
    }
    strcpy(name_copy, name);

    // Set the fields
    manager->characters[manager->count].character_name = name_copy;
    manager->characters[manager->count].release_year = release_year;
    manager->characters[manager->count].genre = genre;
    manager->count = new_count;

    return 1;
}

int character_manager_delete_at(CharacterManager *manager, int index) {
    if (manager == NULL || index < 0 || index >= manager->count) {
        return 0;
    }

    // Free the dynamic memory of the character name at that index
    if (manager->characters[index].character_name != NULL) {
        free(manager->characters[index].character_name);
        manager->characters[index].character_name = NULL;
    }

    // Shift remaining characters to close the gap
    for (int i = index; i < manager->count - 1; i++) {
        manager->characters[i] = manager->characters[i + 1];
    }

    int new_count = manager->count - 1;
    if (new_count == 0) {
        free(manager->characters);
        manager->characters = NULL;
    } else {
        Character *temp = (Character *)realloc(manager->characters, new_count * sizeof(Character));
        if (temp != NULL) {
            manager->characters = temp;
        }
        // If realloc fails when shrinking, we keep the original block which is still valid
    }

    manager->count = new_count;
    return 1;
}

void character_manager_free(CharacterManager *manager) {
    if (manager == NULL) {
        return;
    }

    if (manager->characters != NULL) {
        for (int i = 0; i < manager->count; i++) {
            if (manager->characters[i].character_name != NULL) {
                free(manager->characters[i].character_name);
                manager->characters[i].character_name = NULL;
            }
        }
        free(manager->characters);
        manager->characters = NULL;
    }
    manager->count = 0;
}

void character_manager_init_random(CharacterManager *manager, int count) {
    if (manager == NULL || count <= 0) {
        return;
    }

    for (int i = 0; i < count; i++) {
        // Generate random year between 1980 and 2024
        int year = (rand() % (2024 - 1980 + 1)) + 1980;
        // Generate random genre from GENRE_RPG (1) to GENRE_HORROR (4)
        GameGenre genre = (GameGenre)((rand() % 4) + 1);

        char temp_name[50];
        sprintf(temp_name, "Character %d", manager->count + 1);

        character_manager_add(manager, temp_name, year, genre);
    }
}

const char *genre_to_string(GameGenre genre) {
    switch (genre) {
        case GENRE_RPG:
            return "RPG";
        case GENRE_GACHA:
            return "Gacha";
        case GENRE_SHOOTER:
            return "Shooter";
        case GENRE_HORROR:
            return "Horror";
        default:
            return "Unknown";
    }
}
