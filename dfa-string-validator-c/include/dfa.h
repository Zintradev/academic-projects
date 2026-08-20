#ifndef DFA_H
#define DFA_H

#include <stdbool.h>

/**
 * @brief Enumeration representing the states of the Deterministic Finite Automaton (DFA).
 */
typedef enum {
    STATE_START = 0,                     ///< Initial state
    STATE_AFTER_OPTIONAL_UPPERCASE,      ///< Read optional uppercase letter [A-Z]
    STATE_AFTER_FIRST_PUNCTUATION,       ///< Read first mandatory punctuation
    STATE_AFTER_LOWERCASE_OR_DIGIT,      ///< Read mandatory lowercase or digit
    STATE_AFTER_OPTIONAL_LETTER,         ///< Read optional letter [A-Za-z]
    STATE_AFTER_SECOND_PUNCTUATION,      ///< Read second mandatory punctuation
    STATE_AFTER_OPTIONAL_LOWERCASE,      ///< Read optional lowercase letter [a-z]
    STATE_AFTER_LETTER,                  ///< Read mandatory letter [A-Za-z]
    STATE_AFTER_OPTIONAL_LOWERCASE_2,    ///< Read optional lowercase letter [a-z]
    STATE_AFTER_DIGIT,                   ///< Read mandatory digit [0-9]
    STATE_ACCEPT,                        ///< Final/acceptance state (mandatory punctuation)
    STATE_ERROR                          ///< Trap state for invalid inputs
} DFAState;

/**
 * @brief Enumeration representing the character classes (alphabet of the DFA).
 */
typedef enum {
    CHAR_CLASS_INVALID = 0,              ///< Invalid character class
    CHAR_CLASS_UPPERCASE,                ///< Uppercase letters [A-Z]
    CHAR_CLASS_LOWERCASE,                ///< Lowercase letters [a-z]
    CHAR_CLASS_DIGIT,                    ///< Decimal digits [0-9]
    CHAR_CLASS_PUNCTUATION               ///< Punctuation/special characters (ispunct)
} CharClass;

/**
 * @brief Structure representing the results of a validation check.
 */
typedef struct {
    bool is_valid;                       ///< True if string matches the DFA pattern
    int error_position;                  ///< 1-based index where validation failed, or -1 if valid
    DFAState final_state;                ///< State of the DFA after processing
} ValidationResult;

// Number of states and character classes in the DFA
#define NUM_STATES 12
#define NUM_CLASSES 5

/**
 * @brief Classifies a character into its corresponding CharClass.
 * 
 * @param c The character to classify.
 * @return CharClass The classified character category.
 */
CharClass classify_char(char c);

/**
 * @brief Transitions from the current state to the next state based on the input character class.
 * 
 * This uses a 2D lookup transition table for O(1) transitions.
 * 
 * @param current_state The current state of the DFA.
 * @param char_class The character class of the input character.
 * @return DFAState The next state of the DFA.
 */
DFAState transition(DFAState current_state, CharClass char_class);

/**
 * @brief Validates an input string against the DFA.
 * 
 * @param input The null-terminated string to validate.
 * @return ValidationResult The detailed result of the validation.
 */
ValidationResult validate_string(const char *input);

#endif // DFA_H
