# ==========================================
# Filters Module (Color Masking Operations)
# ==========================================

.include "../include/mips_constants.inc"

.data
msg_filter_applied: .asciiz "\nFilter applied successfully.\n"

.text
.globl apply_red_filter
.globl apply_green_filter
.globl apply_blue_filter
.globl apply_yellow_filter
.globl apply_cyan_filter
.globl apply_magenta_filter

# ------------------------------------------
# print_applied_msg
# Internal helper to print verification message.
# ------------------------------------------
print_applied_msg:
    li $v0, SYS_PRINT_STRING
    la $a0, msg_filter_applied
    syscall
    jr $ra

# ------------------------------------------
# apply_red_filter
# Input: $a0 = 24-bit input color
# Output: $v0 = 24-bit filtered color (R component only)
# ------------------------------------------
apply_red_filter:
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $s0, 0($sp)

    andi $s0, $a0, 0xFF0000  # Keep only Red component

    jal print_applied_msg

    move $v0, $s0
    lw $s0, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

# ------------------------------------------
# apply_green_filter
# Input: $a0 = 24-bit input color
# Output: $v0 = 24-bit filtered color (G component only)
# ------------------------------------------
apply_green_filter:
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $s0, 0($sp)

    andi $s0, $a0, 0x00FF00  # Keep only Green component

    jal print_applied_msg

    move $v0, $s0
    lw $s0, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

# ------------------------------------------
# apply_blue_filter
# Input: $a0 = 24-bit input color
# Output: $v0 = 24-bit filtered color (B component only)
# ------------------------------------------
apply_blue_filter:
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $s0, 0($sp)

    andi $s0, $a0, 0x0000FF  # Keep only Blue component

    jal print_applied_msg

    move $v0, $s0
    lw $s0, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

# ------------------------------------------
# apply_yellow_filter
# Input: $a0 = 24-bit input color
# Output: $v0 = 24-bit filtered color (R + G components)
# ------------------------------------------
apply_yellow_filter:
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $s0, 0($sp)

    andi $s0, $a0, 0xFFFF00  # Keep Red and Green components

    jal print_applied_msg

    move $v0, $s0
    lw $s0, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

# ------------------------------------------
# apply_cyan_filter
# Input: $a0 = 24-bit input color
# Output: $v0 = 24-bit filtered color (G + B components)
# ------------------------------------------
apply_cyan_filter:
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $s0, 0($sp)

    andi $s0, $a0, 0x00FFFF  # Keep Green and Blue components

    jal print_applied_msg

    move $v0, $s0
    lw $s0, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra

# ------------------------------------------
# apply_magenta_filter
# Input: $a0 = 24-bit input color
# Output: $v0 = 24-bit filtered color (R + B components)
# ------------------------------------------
apply_magenta_filter:
    addi $sp, $sp, -8
    sw $ra, 4($sp)
    sw $s0, 0($sp)

    andi $s0, $a0, 0xFF00FF  # Keep Red and Blue components

    jal print_applied_msg

    move $v0, $s0
    lw $s0, 0($sp)
    lw $ra, 4($sp)
    addi $sp, $sp, 8
    jr $ra
