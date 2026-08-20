# ==========================================
# Automated Unit Tests for Color Filters
# ==========================================

.include "../include/mips_constants.inc"

.text
.globl main

main:
    # ------------------------------------------
    # Test 1: apply_red_filter
    # Input: 0x123456 (R=12, G=34, B=56)
    # Expected: 0x120000
    # ------------------------------------------
    li $a0, 0x123456
    jal apply_red_filter
    li $t0, 0x120000
    bne $v0, $t0, test_fail

    # ------------------------------------------
    # Test 2: apply_green_filter
    # Input: 0x123456
    # Expected: 0x003400
    # ------------------------------------------
    li $a0, 0x123456
    jal apply_green_filter
    li $t0, 0x003400
    bne $v0, $t0, test_fail

    # ------------------------------------------
    # Test 3: apply_blue_filter
    # Input: 0x123456
    # Expected: 0x000056
    # ------------------------------------------
    li $a0, 0x123456
    jal apply_blue_filter
    li $t0, 0x000056
    bne $v0, $t0, test_fail

    # ------------------------------------------
    # Test 4: apply_yellow_filter (R + G)
    # Input: 0x123456
    # Expected: 0x123400
    # ------------------------------------------
    li $a0, 0x123456
    jal apply_yellow_filter
    li $t0, 0x123400
    bne $v0, $t0, test_fail

    # ------------------------------------------
    # Test 5: apply_cyan_filter (G + B)
    # Input: 0x123456
    # Expected: 0x003456
    # ------------------------------------------
    li $a0, 0x123456
    jal apply_cyan_filter
    li $t0, 0x003456
    bne $v0, $t0, test_fail

    # ------------------------------------------
    # Test 6: apply_magenta_filter (R + B)
    # Input: 0x123456
    # Expected: 0x120056
    # ------------------------------------------
    li $a0, 0x123456
    jal apply_magenta_filter
    li $t0, 0x120056
    bne $v0, $t0, test_fail

    # All tests passed successfully!
    li $v0, SYS_PRINT_STRING
    la $a0, msg_all_passed
    syscall
    
    # Exit test suite with success
    li $v0, SYS_EXIT
    syscall

test_fail:
    # Print failure message
    li $v0, SYS_PRINT_STRING
    la $a0, msg_test_failed
    syscall
    
    # Exit test suite
    li $v0, SYS_EXIT
    syscall

.data
msg_all_passed:   .asciiz "\n[SUCCESS] All Unit Tests Passed!\n"
msg_test_failed:  .asciiz "\n[FAILURE] Unit Tests Failed!\n"

# ==========================================
# Include Target under Test
# ==========================================
.include "../src/filters.s"
