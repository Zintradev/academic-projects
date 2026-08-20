# ==========================================
# Input Module (Hexadecimal & Float RGB)
# ==========================================

.include "../include/mips_constants.inc"

.data
prompt_hex:   .asciiz "Enter a hexadecimal number (max 6 digits): "
prompt_red:   .asciiz "Enter red value (float between 0 and 1): "
prompt_green: .asciiz "Enter green value (float between 0 and 1): "
prompt_blue:  .asciiz "Enter blue value (float between 0 and 1): "

.text
.globl read_hex
.globl read_float

# ------------------------------------------
# read_hex
# Input: None
# Output: $v0 = 24-bit color value
# Note: Uses a stack-allocated buffer for safety.
# ------------------------------------------
read_hex:
    # Stack layout:
    # 8($sp) - Saved Return Address ($ra)
    # 0($sp) - Stack buffer for string input (8 bytes)
    addi $sp, $sp, -12
    sw $ra, 8($sp)

    # Print prompt
    li $v0, SYS_PRINT_STRING
    la $a0, prompt_hex
    syscall

    # Read string from console into stack buffer
    li $v0, SYS_READ_STRING
    move $a0, $sp            # Buffer address on stack
    li $a1, 8                # Read up to 7 characters + null
    syscall

    # Initialize parser state
    move $t0, $sp            # $t0 = string pointer
    li $v0, 0                # $v0 = color accumulator

parse_loop:
    lb $t3, 0($t0)           # Load current character
    beqz $t3, done_parsing   # Stop on null terminator
    li $t4, '\n'
    beq $t3, $t4, done_parsing # Stop on newline character

    # Check if '0' <= character <= '9'
    li $t4, '0'
    li $t5, '9'
    blt $t3, $t4, check_upper
    ble $t3, $t5, convert_digit

check_upper:
    # Check if 'A' <= character <= 'F'
    li $t4, 'A'
    li $t5, 'F'
    blt $t3, $t4, check_lower
    ble $t3, $t5, convert_uppercase

check_lower:
    # Check if 'a' <= character <= 'f'
    li $t4, 'a'
    li $t5, 'f'
    blt $t3, $t4, done_parsing # Invalid character, stop parsing
    ble $t3, $t5, convert_lowercase
    j done_parsing           # Invalid character, stop parsing

convert_digit:
    sub $t3, $t3, '0'
    j accumulate

convert_uppercase:
    sub $t3, $t3, 'A'
    addi $t3, $t3, 10
    j accumulate

convert_lowercase:
    sub $t3, $t3, 'a'
    addi $t3, $t3, 10
    j accumulate

accumulate:
    sll $v0, $v0, 4          # Shift accumulator left by 4 bits
    or $v0, $v0, $t3         # Add current nibble
    addi $t0, $t0, 1         # Move to next character
    j parse_loop

done_parsing:
    # Restore $ra and restore stack pointer
    lw $ra, 8($sp)
    addi $sp, $sp, 12
    jr $ra


# ------------------------------------------
# read_float
# Input: None
# Output: $v0 = 24-bit color value
# ------------------------------------------
read_float:
    addi $sp, $sp, -4
    sw $ra, 0($sp)

    # Read Red float component
    li $v0, SYS_PRINT_STRING
    la $a0, prompt_red
    syscall
    li $v0, SYS_READ_FLOAT
    syscall
    mov.s $f1, $f0           # Store Red in $f1

    # Read Green float component
    li $v0, SYS_PRINT_STRING
    la $a0, prompt_green
    syscall
    li $v0, SYS_READ_FLOAT
    syscall
    mov.s $f2, $f0           # Store Green in $f2

    # Read Blue float component
    li $v0, SYS_PRINT_STRING
    la $a0, prompt_blue
    syscall
    li $v0, SYS_READ_FLOAT
    syscall
    mov.s $f3, $f0           # Store Blue in $f3

    # Scale components from [0.0, 1.0] to [0.0, 255.0]
    li.s $f4, 255.0
    mul.s $f1, $f1, $f4      # R * 255.0
    mul.s $f2, $f2, $f4      # G * 255.0
    mul.s $f3, $f3, $f4      # B * 255.0

    # Truncate fractional parts to integer values
    trunc.w.s $f1, $f1
    trunc.w.s $f2, $f2
    trunc.w.s $f3, $f3

    # Move float registers back to GPRs
    mfc1 $t0, $f1            # R
    mfc1 $t1, $f2            # G
    mfc1 $t2, $f3            # B

    # Construct the final 24-bit color: (R << 16) | (G << 8) | B
    sll $t0, $t0, 16
    sll $t1, $t1, 8
    or  $v0, $t0, $t1
    or  $v0, $v0, $t2

    lw $ra, 0($sp)
    addi $sp, $sp, 4
    jr $ra
