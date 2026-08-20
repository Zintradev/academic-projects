# ==========================================
# Main Module (Application Driver & Menu)
# ==========================================

.include "../include/mips_constants.inc"

.data
msg_menu: .asciiz "\n==========================================\n             COLOR PROGRAM\n==========================================\nPress the initial key to select an operation:\n  <H> Read color in hexadecimal format (e.g., FF00FF)\n  <N> Read color in R-G-B float levels (e.g., 0.5 1.0 0.0)\n  <I> Query current color\n  <R> Apply red filter\n  <V> Apply green filter\n  <A> Apply blue filter\n  <Y> Apply yellow filter\n  <C> Apply cyan filter\n  <M> Apply magenta filter\n  <S> Exit\n------------------------------------------\nEnter your choice (lowercase): "

msg_invalid_option: .asciiz "\nInvalid option. Please select a valid option.\n"
msg_newline:        .asciiz "\n"

.text
.globl main

main:
    # Initialize active color state to 0x000000 (Black)
    # Using a saved register ($s0) to persist state safely across subroutine calls
    li $s0, 0x000000

menu_loop:
    # Print the menu selection prompt
    li $v0, SYS_PRINT_STRING
    la $a0, msg_menu
    syscall

    # Read selected option character
    li $v0, SYS_READ_CHAR
    syscall
    move $t0, $v0            # Save selection character in $t0

    # Consume the newline character ('\n') from the input buffer
    li $v0, SYS_READ_CHAR
    syscall

    # Evaluate the selected option
    beq $t0, 'h', handle_read_hex
    beq $t0, 'n', handle_read_float
    beq $t0, 'i', handle_query
    beq $t0, 'r', handle_filter_red
    beq $t0, 'v', handle_filter_green
    beq $t0, 'a', handle_filter_blue
    beq $t0, 'y', handle_filter_yellow
    beq $t0, 'c', handle_filter_cyan
    beq $t0, 'm', handle_filter_magenta
    beq $t0, 's', handle_exit

    # If option is invalid, notify the user and loop again
    li $v0, SYS_PRINT_STRING
    la $a0, msg_invalid_option
    syscall
    j menu_loop

handle_read_hex:
    jal read_hex
    move $s0, $v0            # Save the returned color value to active state
    j menu_loop

handle_read_float:
    jal read_float
    move $s0, $v0            # Save the returned color value to active state
    j menu_loop

handle_query:
    move $a0, $s0            # Pass the active color as argument
    jal display_color_info
    j menu_loop

handle_filter_red:
    move $a0, $s0            # Pass active color
    jal apply_red_filter
    move $s0, $v0            # Update active color with result
    j menu_loop

handle_filter_green:
    move $a0, $s0
    jal apply_green_filter
    move $s0, $v0
    j menu_loop

handle_filter_blue:
    move $a0, $s0
    jal apply_blue_filter
    move $s0, $v0
    j menu_loop

handle_filter_yellow:
    move $a0, $s0
    jal apply_yellow_filter
    move $s0, $v0
    j menu_loop

handle_filter_cyan:
    move $a0, $s0
    jal apply_cyan_filter
    move $s0, $v0
    j menu_loop

handle_filter_magenta:
    move $a0, $s0
    jal apply_magenta_filter
    move $s0, $v0
    j menu_loop

handle_exit:
    li $v0, SYS_EXIT
    syscall

# ==========================================
# Include Dependent Modules
# ==========================================
.include "input.s"
.include "filters.s"
.include "display.s"
