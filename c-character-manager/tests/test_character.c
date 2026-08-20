#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include "../include/character.h"

void test_initialization(void) {
    CharacterManager manager;
    character_manager_init(&manager);
    
    assert(manager.characters == NULL);
    assert(manager.count == 0);
    
    printf("\t[PASS] test_initialization\n");
}

void test_add_character(void) {
    CharacterManager manager;
    character_manager_init(&manager);

    int success = character_manager_add(&manager, "Geralt of Rivia", 2015, GENRE_RPG);
    assert(success == 1);
    assert(manager.count == 1);
    assert(manager.characters != NULL);
    assert(strcmp(manager.characters[0].character_name, "Geralt of Rivia") == 0);
    assert(manager.characters[0].release_year == 2015);
    assert(manager.characters[0].genre == GENRE_RPG);

    character_manager_free(&manager);
    assert(manager.characters == NULL);
    assert(manager.count == 0);
    
    printf("\t[PASS] test_add_character\n");
}

void test_delete_character(void) {
    CharacterManager manager;
    character_manager_init(&manager);

    character_manager_add(&manager, "Char 1", 2000, GENRE_RPG);
    character_manager_add(&manager, "Char 2", 2001, GENRE_GACHA);
    character_manager_add(&manager, "Char 3", 2002, GENRE_SHOOTER);

    assert(manager.count == 3);

    // Delete middle element (Char 2)
    int success = character_manager_delete_at(&manager, 1);
    assert(success == 1);
    assert(manager.count == 2);
    assert(strcmp(manager.characters[0].character_name, "Char 1") == 0);
    assert(strcmp(manager.characters[1].character_name, "Char 3") == 0);

    // Delete remaining items
    character_manager_delete_at(&manager, 0);
    character_manager_delete_at(&manager, 0);
    assert(manager.count == 0);
    assert(manager.characters == NULL);

    character_manager_free(&manager);
    printf("\t[PASS] test_delete_character\n");
}

int main(void) {
    printf("\n=== Running Unit Tests ===\n");
    test_initialization();
    test_add_character();
    test_delete_character();
    printf("=== All tests passed successfully! ===\n\n");
    return 0;
}
