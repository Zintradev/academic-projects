#ifndef CHARACTER_H
#define CHARACTER_H

typedef enum {
    GENRE_RPG = 1,
    GENRE_GACHA,
    GENRE_SHOOTER,
    GENRE_HORROR
} GameGenre;

typedef struct {
    char *character_name;
    int release_year;
    GameGenre genre;
} Character;

typedef struct {
    Character *characters;
    int count;
} CharacterManager;

/**
 * @brief Initializes the character manager.
 * @param manager Pointer to the CharacterManager struct.
 */
void character_manager_init(CharacterManager *manager);

/**
 * @brief Adds a character with dynamic memory allocation for the name.
 * @param manager Pointer to the CharacterManager struct.
 * @param name The character's name.
 * @param release_year The game's release year.
 * @param genre The game's genre.
 * @return int 1 on success, 0 on memory allocation failure.
 */
int character_manager_add(CharacterManager *manager, const char *name, int release_year, GameGenre genre);

/**
 * @brief Deletes a character at a specific 0-based index.
 * @param manager Pointer to the CharacterManager struct.
 * @param index Index of the character to delete.
 * @return int 1 on success, 0 on invalid index or memory reallocation failure.
 */
int character_manager_delete_at(CharacterManager *manager, int index);

/**
 * @brief Frees all allocated memory in the character manager.
 * @param manager Pointer to the CharacterManager struct.
 */
void character_manager_free(CharacterManager *manager);

/**
 * @brief Populates the character manager with a set of default random characters.
 * @param manager Pointer to the CharacterManager struct.
 * @param count The number of random characters to generate.
 */
void character_manager_init_random(CharacterManager *manager, int count);

/**
 * @brief Returns a string representation of a GameGenre.
 * @param genre The game genre.
 * @return const char* String representation of the genre.
 */
const char *genre_to_string(GameGenre genre);

#endif // CHARACTER_H
