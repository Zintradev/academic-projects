#include <stdio.h>
#include <stdbool.h>
#include "dfa.h"

typedef struct {
    const char *description;
    const char *input;
    bool expected_valid;
    int expected_error_pos;
} TestCase;

int main(void) {
    TestCase tests[] = {
        // Valid scenarios
        {
            "Minimal valid string (no optionals)",
            "@5$A7#",
            true,
            -1
        },
        {
            "Valid string with optional uppercase start",
            "Z@5$A7#",
            true,
            -1
        },
        {
            "Valid string with optional middle letter (lowercase)",
            "@5a$A7#",
            true,
            -1
        },
        {
            "Valid string with optional middle letter (uppercase)",
            "@5A$A7#",
            true,
            -1
        },
        {
            "Valid string with optional lowercase in suffix",
            "@5$bA7#",
            true,
            -1
        },
        {
            "Valid string with extra lowercase in suffix",
            "@5$bca7#",
            true,
            -1
        },
        {
            "Valid string with all optional characters",
            "Z#5y@xWv3!",
            true,
            -1
        },
        
        // Invalid scenarios
        {
            "Empty string",
            "",
            false,
            1
        },
        {
            "Starts with invalid character (digit)",
            "0@5$A7#",
            false,
            1
        },
        {
            "Missing first punctuation mark",
            "Z5$A7#",
            false,
            2
        },
        {
            "Two consecutive punctuation marks at the start",
            "@@5$A7#",
            false,
            2
        },
        {
            "Ends prematurely after second punctuation",
            "@5$",
            false,
            4
        },
        {
            "Invalid character after second punctuation (digit)",
            "@5$7A7#",
            false,
            4
        },
        {
            "Invalid character in suffix sequence (punctuation instead of digit)",
            "@5$A#",
            false,
            5
        },
        {
            "Valid pattern prefix but incomplete (missing final punctuation)",
            "@5$A7",
            false,
            6
        }
    };

    int num_tests = sizeof(tests) / sizeof(tests[0]);
    int passed = 0;

    printf("===========================================\n");
    printf("        DFA VALIDATOR UNIT TESTS           \n");
    printf("===========================================\n");

    for (int i = 0; i < num_tests; i++) {
        ValidationResult result = validate_string(tests[i].input);
        bool status_ok = (result.is_valid == tests[i].expected_valid);
        bool pos_ok = tests[i].expected_valid || (result.error_position == tests[i].expected_error_pos);

        if (status_ok && pos_ok) {
            printf("  [PASS] Test %2d: %s\n", i + 1, tests[i].description);
            passed++;
        } else {
            printf("  [FAIL] Test %2d: %s\n", i + 1, tests[i].description);
            printf("         Input   : \"%s\"\n", tests[i].input);
            printf("         Expected: valid=%s, error_pos=%d\n", 
                   tests[i].expected_valid ? "true" : "false", tests[i].expected_error_pos);
            printf("         Got     : valid=%s, error_pos=%d (final_state=%d)\n", 
                   result.is_valid ? "true" : "false", result.error_position, result.final_state);
        }
    }

    printf("===========================================\n");
    printf("Summary: %d / %d tests passed.\n", passed, num_tests);
    printf("===========================================\n");

    return (passed == num_tests) ? 0 : 1;
}
