#ifndef STORAGE_H
#define STORAGE_H

#include "character.h"

/**
 * @brief Saves all characters in the manager to a file.
 * @param manager Pointer to the CharacterManager struct.
 * @param filename Path to the file where data will be stored.
 * @return int 1 on success, 0 on failure.
 */
int storage_save_to_file(const CharacterManager *manager, const char *filename);

/**
 * @brief Loads characters from a file and populates the manager.
 * @param manager Pointer to the CharacterManager struct.
 * @param filename Path to the file from which data will be read.
 * @return int Number of characters successfully loaded, or -1 on file open failure.
 */
int storage_load_from_file(CharacterManager *manager, const char *filename);

#endif // STORAGE_H
