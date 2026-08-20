#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "ui.h"

void ui_clear_input_buffer(void) {
    int c;
    while ((c = getchar()) != '\n' && c != EOF);
}

void ui_read_string(char *buffer, int size) {
    if (fgets(buffer, size, stdin) != NULL) {
        // Strip trailing newline character
        buffer[strcspn(buffer, "\r\n")] = '\0';
    }
}

void ui_display_characters(const CharacterManager *manager) {
    if (manager == NULL || manager->count <= 0) {
        printf("\n\tNo characters available.\n");
        return;
    }

    printf("\n\t===============================================================\n");
    printf("\t %-4s | %-25s | %-12s | %-10s\n", "ID", "Character Name", "Release Year", "Genre");
    printf("\t---------------------------------------------------------------\n");
    for (int i = 0; i < manager->count; i++) {
        printf("\t %-4d | %-25s | %-12d | %-10s\n",
               i + 1,
               manager->characters[i].character_name,
               manager->characters[i].release_year,
               genre_to_string(manager->characters[i].genre));
    }
    printf("\t===============================================================\n");
}

void ui_add_character_prompt(CharacterManager *manager) {
    if (manager == NULL) {
        return;
    }

    int quantity = 0;
    printf("\n\tHow many new characters do you want to add? -> ");
    if (scanf("%d", &quantity) != 1 || quantity <= 0) {
        printf("\tInvalid input quantity.\n");
        ui_clear_input_buffer();
        return;
    }
    ui_clear_input_buffer(); // Consume newline left by scanf

    for (int i = 0; i < quantity; i++) {
        int year = 0;
        int genre_num = 0;
        char name[100];

        printf("\n\t[New Character %d of %d]\n", i + 1, quantity);
        printf("\tEnter the release year: ");
        if (scanf("%d", &year) != 1) {
            printf("\tInvalid input. Defaulting release year to 2000.\n");
            year = 2000;
        }
        ui_clear_input_buffer();

        printf("\tEnter genre (1: RPG, 2: Gacha, 3: Shooter, 4: Horror): ");
        if (scanf("%d", &genre_num) != 1 || genre_num < 1 || genre_num > 4) {
            printf("\tInvalid selection. Defaulting to RPG.\n");
            genre_num = 1;
        }
        ui_clear_input_buffer();

        printf("\tEnter character name: ");
        ui_read_string(name, sizeof(name));

        if (character_manager_add(manager, name, year, (GameGenre)genre_num)) {
            printf("\tCharacter '%s' successfully added locally.\n", name);
        } else {
            printf("\tFailed to add character (Memory allocation error).\n");
        }
    }
    printf("\n\tRemember to save changes to the file (Option 5).\n");
}

void ui_delete_character_prompt(CharacterManager *manager) {
    if (manager == NULL || manager->count <= 0) {
        printf("\n\tNo characters available to delete.\n");
        return;
    }

    ui_display_characters(manager);

    int id = 0;
    printf("\n\tEnter the ID of the character to delete: ");
    if (scanf("%d", &id) != 1) {
        printf("\tInvalid ID selection.\n");
        ui_clear_input_buffer();
        return;
    }
    ui_clear_input_buffer();

    if (id < 1 || id > manager->count) {
        printf("\tCharacter ID %d does not exist.\n", id);
        return;
    }

    int index = id - 1;
    const char *name = manager->characters[index].character_name;
    printf("\tDeleting character '%s'...\n", name ? name : "Unknown");

    if (character_manager_delete_at(manager, index)) {
        printf("\tCharacter deleted successfully.\n");
        printf("\tRemember to save changes to the file (Option 5).\n");
    } else {
        printf("\tFailed to delete character.\n");
    }
}

void ui_show_menu(void) {
    printf("\n\t=========================================\n");
    printf("\t          CHARACTER MANAGER MENU         \n");
    printf("\t=========================================\n");
    printf("\t  1. Delete a character\n");
    printf("\t  2. Add new character(s)\n");
    printf("\t  3. List characters\n");
    printf("\t  4. Delete all characters\n");
    printf("\t  5. Save characters to file\n");
    printf("\t  6. Exit\n");
    printf("\t=========================================\n");
    printf("\t  Choice -> ");
}
