#ifndef UI_H
#define UI_H

#include "character.h"

/**
 * @brief Displays all characters currently in the manager in a clean, formatted table.
 * @param manager Pointer to the CharacterManager struct.
 */
void ui_display_characters(const CharacterManager *manager);

/**
 * @brief Prompts the user for details to add new characters.
 * @param manager Pointer to the CharacterManager struct.
 */
void ui_add_character_prompt(CharacterManager *manager);

/**
 * @brief Prompts the user to select and delete a character.
 * @param manager Pointer to the CharacterManager struct.
 */
void ui_delete_character_prompt(CharacterManager *manager);

/**
 * @brief Prints the main menu options to the console.
 */
void ui_show_menu(void);

/**
 * @brief Clears the stdin input buffer to prevent scanning errors.
 */
void ui_clear_input_buffer(void);

/**
 * @brief Reads a string safely from the console, removing trailing newlines.
 * @param buffer Pointer to the destination buffer.
 * @param size Maximum size of the buffer.
 */
void ui_read_string(char *buffer, int size);

#endif // UI_H
