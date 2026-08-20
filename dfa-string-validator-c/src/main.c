#include <stdio.h>
#include "dfa.h"

int main(void) {
    char input[100];

    printf("Please write your string (+ENTER): ");
    
    // Safely read up to 99 characters to prevent buffer overflow
    if (scanf("%99s", input) != 1) {
        printf("Error reading input.\n");
        return 1;
    }

    ValidationResult result = validate_string(input);

    if (result.is_valid) {
        printf("\nString IS VALID according to the expression.\n");
    } else {
        printf("Posicion de error: %i", result.error_position);
        printf("\nString IS NOT VALID according to the expression.\n");
    }

    return 0;
}
