#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <locale.h>
#include <string.h>

#include "character.h"
#include "storage.h"
#include "ui.h"

int main(int argc, char *argv[]) {
    // Set seed for random initialization
    srand((unsigned int)time(NULL));

    // Configure locale for UTF-8 support (to support symbols and accents nicely)
    setlocale(LC_ALL, "");

    CharacterManager my_characters;
    character_manager_init(&my_characters);

    const char *db_file = "characters.txt";

    // Attempt to load characters from the default persistence file
    int loaded = storage_load_from_file(&my_characters, db_file);
    if (loaded >= 0) {
        printf("\n\t[INFO] Loaded %d characters from '%s'.\n", loaded, db_file);
    } else {
        printf("\n\t[INFO] No save file '%s' found. Seeding 3 random characters to start.\n", db_file);
        character_manager_init_random(&my_characters, 3);
    }

    int choice = 0;
    do {
        ui_show_menu();
        if (scanf("%d", &choice) != 1) {
            printf("\n\tInvalid input. Please enter a number.\n");
            ui_clear_input_buffer();
            continue;
        }
        ui_clear_input_buffer();

        switch (choice) {
            case 1:
                ui_delete_character_prompt(&my_characters);
                break;
            case 2:
                ui_add_character_prompt(&my_characters);
                break;
            case 3:
                ui_display_characters(&my_characters);
                break;
            case 4: {
                char confirmation[10];
                printf("\n\tAre you sure you want to delete ALL characters? (y/N): ");
                ui_read_string(confirmation, sizeof(confirmation));
                if (strcmp(confirmation, "y") == 0 || strcmp(confirmation, "Y") == 0) {
                    character_manager_free(&my_characters);
                    printf("\tAll characters deleted locally. Remember to save changes (Option 5) to persist.\n");
                } else {
                    printf("\tClear operation cancelled.\n");
                }
                break;
            }
            case 5:
                if (storage_save_to_file(&my_characters, db_file)) {
                    printf("\tDatabase successfully saved to '%s'.\n", db_file);
                } else {
                    printf("\tError: Could not write database to '%s'.\n", db_file);
                }
                break;
            case 6:
                printf("\tExiting the character manager.\n");
                break;
            default:
                printf("\tInvalid selection. Please choose an option from 1 to 6.\n");
                break;
        }
    } while (choice != 6);

    // Free all allocated dynamic memory before termination to avoid leaks
    character_manager_free(&my_characters);
    printf("\tCleanup finished. Goodbye!\n");

    return 0;
}
