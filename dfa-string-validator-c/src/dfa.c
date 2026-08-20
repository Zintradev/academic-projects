#include "dfa.h"
#include <ctype.h>
#include <stddef.h>

CharClass classify_char(char c) {
    if (c >= 'A' && c <= 'Z') {
        return CHAR_CLASS_UPPERCASE;
    } else if (c >= 'a' && c <= 'z') {
        return CHAR_CLASS_LOWERCASE;
    } else if (isdigit((unsigned char)c)) {
        return CHAR_CLASS_DIGIT;
    } else if (ispunct((unsigned char)c)) {
        return CHAR_CLASS_PUNCTUATION;
    }
    return CHAR_CLASS_INVALID;
}

// 2D Transition Table representing the DFA state transitions.
// Row indexes: DFAState (0 to 11)
// Column indexes: CharClass (0 to 4)
//
// Pattern represented: [A-Z]? P [a-z0-9] [A-Za-z]? P [a-z]? [A-Za-z] [a-z]? [0-9] P
static const DFAState TRANSITION_TABLE[NUM_STATES][NUM_CLASSES] = {
    // Columns: CHAR_CLASS_INVALID, CHAR_CLASS_UPPERCASE, CHAR_CLASS_LOWERCASE, CHAR_CLASS_DIGIT, CHAR_CLASS_PUNCTUATION
    
    // 0: STATE_START
    { STATE_ERROR, STATE_AFTER_OPTIONAL_UPPERCASE, STATE_ERROR, STATE_ERROR, STATE_AFTER_FIRST_PUNCTUATION },
    
    // 1: STATE_AFTER_OPTIONAL_UPPERCASE
    { STATE_ERROR, STATE_ERROR, STATE_ERROR, STATE_ERROR, STATE_AFTER_FIRST_PUNCTUATION },
    
    // 2: STATE_AFTER_FIRST_PUNCTUATION
    { STATE_ERROR, STATE_ERROR, STATE_AFTER_LOWERCASE_OR_DIGIT, STATE_AFTER_LOWERCASE_OR_DIGIT, STATE_ERROR },
    
    // 3: STATE_AFTER_LOWERCASE_OR_DIGIT
    { STATE_ERROR, STATE_AFTER_OPTIONAL_LETTER, STATE_AFTER_OPTIONAL_LETTER, STATE_ERROR, STATE_AFTER_SECOND_PUNCTUATION },
    
    // 4: STATE_AFTER_OPTIONAL_LETTER
    { STATE_ERROR, STATE_ERROR, STATE_ERROR, STATE_ERROR, STATE_AFTER_SECOND_PUNCTUATION },
    
    // 5: STATE_AFTER_SECOND_PUNCTUATION
    { STATE_ERROR, STATE_AFTER_LETTER, STATE_AFTER_OPTIONAL_LOWERCASE, STATE_ERROR, STATE_ERROR },
    
    // 6: STATE_AFTER_OPTIONAL_LOWERCASE
    { STATE_ERROR, STATE_AFTER_LETTER, STATE_AFTER_LETTER, STATE_ERROR, STATE_ERROR },
    
    // 7: STATE_AFTER_LETTER
    { STATE_ERROR, STATE_ERROR, STATE_AFTER_OPTIONAL_LOWERCASE_2, STATE_AFTER_DIGIT, STATE_ERROR },
    
    // 8: STATE_AFTER_OPTIONAL_LOWERCASE_2
    { STATE_ERROR, STATE_ERROR, STATE_ERROR, STATE_AFTER_DIGIT, STATE_ERROR },
    
    // 9: STATE_AFTER_DIGIT
    { STATE_ERROR, STATE_ERROR, STATE_ERROR, STATE_ERROR, STATE_ACCEPT },
    
    // 10: STATE_ACCEPT
    { STATE_ACCEPT, STATE_ACCEPT, STATE_ACCEPT, STATE_ACCEPT, STATE_ACCEPT },
    
    // 11: STATE_ERROR
    { STATE_ERROR, STATE_ERROR, STATE_ERROR, STATE_ERROR, STATE_ERROR }
};

DFAState transition(DFAState current_state, CharClass char_class) {
    if (current_state < 0 || current_state >= NUM_STATES || char_class < 0 || char_class >= NUM_CLASSES) {
        return STATE_ERROR;
    }
    return TRANSITION_TABLE[current_state][char_class];
}

ValidationResult validate_string(const char *input) {
    ValidationResult result = {
        .is_valid = false,
        .error_position = -1,
        .final_state = STATE_START
    };

    if (input == NULL) {
        result.final_state = STATE_ERROR;
        return result;
    }

    DFAState current_state = STATE_START;
    int position = 1;

    for (int i = 0; input[i] != '\0'; i++) {
        char c = input[i];
        CharClass char_class = classify_char(c);
        DFAState next_state = transition(current_state, char_class);

        if (next_state == STATE_ERROR) {
            result.error_position = position;
            result.final_state = STATE_ERROR;
            return result;
        }

        current_state = next_state;
        position++;

        // Stop processing immediately if we reach the ACCEPT state (prefix validation match)
        if (current_state == STATE_ACCEPT) {
            break;
        }
    }

    result.final_state = current_state;
    result.is_valid = (current_state == STATE_ACCEPT);

    // If parsing ended before reaching the ACCEPT state, record the error position
    if (!result.is_valid && result.error_position == -1) {
        result.error_position = position;
    }

    return result;
}
