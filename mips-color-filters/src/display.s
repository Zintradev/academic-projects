# ==========================================
# Display & Conversion Module
# ==========================================

.include "../include/mips_constants.inc"

.data
hex_digits:             .asciiz "0123456789ABCDEF"
msg_rgb_prefix:         .asciiz "RGB Levels: "
msg_rgb_float_prefix:   .asciiz "RGB Float: "
msg_cmyk_prefix:        .asciiz "CMYK Color: "
msg_hex_prefix:         .asciiz "Hexadecimal Color: "
msg_open_paren:         .asciiz "("
msg_comma:              .asciiz ", "
msg_close_paren_newline:.asciiz ")\n"

.text
.globl display_color_info
.globl show_rgb
.globl show_rgb_float
.globl convert_to_cmyk
.globl show_hex

# ------------------------------------------
# display_color_info
# Input: $a0 = 24-bit color value
# ------------------------------------------
display_color_info:
    addi $sp, $sp, -8        # Allocate space for $ra and saved $s0
    sw $ra, 4($sp)
    sw $s0, 0($sp)

    move $s0, $a0            # Save active color in $s0 across calls

    # Display RGB Levels
    move $a0, $s0
    jal show_rgb

    # Display RGB Float
    move $a0, $s0
    jal show_rgb_float

    # Display CMYK
    move $a0, $s0
    jal convert_to_cmyk

    # Display Hexadecimal
    move $a0, $s0
    jal show_hex

    lw $s0, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra


# ------------------------------------------
# show_rgb
# Input: $a0 = 24-bit color value
# ------------------------------------------
show_rgb:
    # Extract R, G, B components
    srl $t0, $a0, 16
    andi $t0, $t0, 0xFF      # R component

    srl $t1, $a0, 8
    andi $t1, $t1, 0xFF      # G component

    andi $t2, $a0, 0xFF      # B component

    # Print "RGB Levels: (R, G, B)\n"
    li $v0, SYS_PRINT_STRING
    la $a0, msg_rgb_prefix
    syscall

    li $v0, SYS_PRINT_STRING
    la $a0, msg_open_paren
    syscall

    li $v0, SYS_PRINT_INT
    move $a0, $t0            # Print R
    syscall

    li $v0, SYS_PRINT_STRING
    la $a0, msg_comma
    syscall

    li $v0, SYS_PRINT_INT
    move $a0, $t1            # Print G
    syscall

    li $v0, SYS_PRINT_STRING
    la $a0, msg_comma
    syscall

    li $v0, SYS_PRINT_INT
    move $a0, $t2            # Print B
    syscall

    li $v0, SYS_PRINT_STRING
    la $a0, msg_close_paren_newline
    syscall

    jr $ra


# ------------------------------------------
# show_rgb_float
# Input: $a0 = 24-bit color value
# ------------------------------------------
show_rgb_float:
    # Extract components
    srl $t0, $a0, 16
    andi $t0, $t0, 0xFF      # R

    srl $t1, $a0, 8
    andi $t1, $t1, 0xFF      # G

    andi $t2, $a0, 0xFF      # B

    # Load denominator (255.0) to normalize
    li.s $f1, 255.0

    # Convert Red to float and normalize
    mtc1 $t0, $f0
    cvt.s.w $f0, $f0
    div.s $f0, $f0, $f1      # R_float = R / 255.0

    # Convert Green to float and normalize
    mtc1 $t1, $f2
    cvt.s.w $f2, $f2
    div.s $f2, $f2, $f1      # G_float = G / 255.0

    # Convert Blue to float and normalize
    mtc1 $t2, $f3
    cvt.s.w $f3, $f3
    div.s $f3, $f3, $f1      # B_float = B / 255.0

    # Print "RGB Float: (R, G, B)\n"
    li $v0, SYS_PRINT_STRING
    la $a0, msg_rgb_float_prefix
    syscall

    li $v0, SYS_PRINT_STRING
    la $a0, msg_open_paren
    syscall

    li $v0, SYS_PRINT_FLOAT
    mov.s $f12, $f0          # Print R_float
    syscall

    li $v0, SYS_PRINT_STRING
    la $a0, msg_comma
    syscall

    li $v0, SYS_PRINT_FLOAT
    mov.s $f12, $f2          # Print G_float
    syscall

    li $v0, SYS_PRINT_STRING
    la $a0, msg_comma
    syscall

    li $v0, SYS_PRINT_FLOAT
    mov.s $f12, $f3          # Print B_float
    syscall

    li $v0, SYS_PRINT_STRING
    la $a0, msg_close_paren_newline
    syscall

    jr $ra


# ------------------------------------------
# convert_to_cmyk
# Input: $a0 = 24-bit color value
# Mathematical CMYK formula:
#   R', G', B' = R/255, G/255, B/255
#   K = 1 - max(R', G', B')
#   C = (1 - R' - K) / (1 - K) = (max - R') / max
#   M = (1 - G' - K) / (1 - K) = (max - G') / max
#   Y = (1 - B' - K) / (1 - K) = (max - B') / max
# ------------------------------------------
convert_to_cmyk:
    # Extract components
    srl $t0, $a0, 16
    andi $t0, $t0, 0xFF      # R

    srl $t1, $a0, 8
    andi $t1, $t1, 0xFF      # G

    andi $t2, $a0, 0xFF      # B

    # Load 255.0 for normalization
    li.s $f4, 255.0

    # Convert to float and scale to [0.0, 1.0]
    mtc1 $t0, $f0
    cvt.s.w $f0, $f0
    div.s $f0, $f0, $f4      # R_float ($f0)

    mtc1 $t1, $f1
    cvt.s.w $f1, $f1
    div.s $f1, $f1, $f4      # G_float ($f1)

    mtc1 $t2, $f2
    cvt.s.w $f2, $f2
    div.s $f2, $f2, $f4      # B_float ($f2)

    # Find max(R_float, G_float, B_float)
    mov.s $f3, $f0           # max ($f3) = R_float
    c.lt.s $f3, $f1          # max < G_float?
    bc1f check_max_blue
    mov.s $f3, $f1           # max = G_float

check_max_blue:
    c.lt.s $f3, $f2          # max < B_float?
    bc1f calc_cmyk
    mov.s $f3, $f2           # max = B_float

calc_cmyk:
    # Calculate K: K = 1.0 - max
    li.s $f4, 1.0
    sub.s $f5, $f4, $f3      # K ($f5) = 1.0 - max

    # Prevent division by zero if max is 0.0 (Pure Black)
    li.s $f6, 0.0
    c.eq.s $f3, $f6          # max == 0.0?
    bc1t black_case

    # Calculate C = (max - R_float) / max
    sub.s $f7, $f3, $f0
    div.s $f7, $f7, $f3      # C ($f7)

    # Calculate M = (max - G_float) / max
    sub.s $f8, $f3, $f1
    div.s $f8, $f8, $f3      # M ($f8)

    # Calculate Y = (max - B_float) / max
    sub.s $f9, $f3, $f2
    div.s $f9, $f9, $f3      # Y ($f9)
    j print_cmyk

black_case:
    li.s $f7, 0.0            # C = 0.0
    li.s $f8, 0.0            # M = 0.0
    li.s $f9, 0.0            # Y = 0.0

print_cmyk:
    # Print "CMYK Color: (C, M, Y, K)\n"
    li $v0, SYS_PRINT_STRING
    la $a0, msg_cmyk_prefix
    syscall

    li $v0, SYS_PRINT_STRING
    la $a0, msg_open_paren
    syscall

    li $v0, SYS_PRINT_FLOAT
    mov.s $f12, $f7          # Print C
    syscall

    li $v0, SYS_PRINT_STRING
    la $a0, msg_comma
    syscall

    li $v0, SYS_PRINT_FLOAT
    mov.s $f12, $f8          # Print M
    syscall

    li $v0, SYS_PRINT_STRING
    la $a0, msg_comma
    syscall

    li $v0, SYS_PRINT_FLOAT
    mov.s $f12, $f9          # Print Y
    syscall

    li $v0, SYS_PRINT_STRING
    la $a0, msg_comma
    syscall

    li $v0, SYS_PRINT_FLOAT
    mov.s $f12, $f5          # Print K
    syscall

    li $v0, SYS_PRINT_STRING
    la $a0, msg_close_paren_newline
    syscall

    jr $ra


# ------------------------------------------
# show_hex
# Input: $a0 = 24-bit color value
# Note: Generates hex representation in a stack buffer.
# ------------------------------------------
show_hex:
    # Stack layout:
    # 8($sp) - Saved Return Address ($ra)
    # 0($sp) - Stack buffer for string formatting (8 bytes)
    addi $sp, $sp, -12
    sw $ra, 8($sp)

    move $t0, $a0            # Copy color to $t0
    la $t2, hex_digits       # Load address of digit mapping table

    # Initialize buffer termination at the end of the 8-byte space
    li $t1, '\n'
    sb $t1, 6($sp)           # $sp + 6 = '\n'
    li $t1, 0
    sb $t1, 7($sp)           # $sp + 7 = '\0'

    # Loop 6 times (from index 5 down to 0) to extract hex digits right-to-left
    li $t3, 5
hex_loop:
    andi $t4, $t0, 0xF       # Extract lowest 4 bits
    add $t5, $t2, $t4        # Offset of corresponding hex char
    lb $t6, 0($t5)           # Load character

    add $t7, $sp, $t3        # Offset on stack buffer
    sb $t6, 0($t7)           # Store character in buffer

    srl $t0, $t0, 4          # Shift right by 4 bits
    addi $t3, $t3, -1
    bgez $t3, hex_loop       # Repeat until index < 0

    # Print "Hexadecimal Color: "
    li $v0, SYS_PRINT_STRING
    la $a0, msg_hex_prefix
    syscall

    # Print formatted hex string from the stack buffer
    li $v0, SYS_PRINT_STRING
    move $a0, $sp
    syscall

    lw $ra, 8($sp)
    addi $sp, $sp, 12
    jr $ra
