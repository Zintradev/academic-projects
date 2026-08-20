# Low-Level Color Processing Engine (MIPS Assembly)

[![MIPS Assembly](https://img.shields.io/badge/Language-MIPS%20Assembly-blue.svg)](https://en.wikipedia.org/wiki/MIPS_architecture)
[![Simulator](https://img.shields.io/badge/Simulator-MARS%20%2F%20SPIM-orange.svg)](https://courses.missouristate.edu/kenvollmar/mars/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A high-performance, modular MIPS assembly program demonstrating low-level color parsing, format conversion, and bitwise color-filtering operations. This project features robust, stack-safe routines, strict adherence to MIPS calling conventions, and automated unit testing.

---

## Technical Highlights

This project demonstrates advanced assembly programming paradigms, showing mastery over processor-level resources:

### 1. Stack Memory Allocation (Local Buffers)
To comply with Clean Code principles and avoid the safety hazards of global mutable variables in `.data`, temporary buffers (such as the 8-byte buffer in the Hexadecimal parser and Hex output formatter) are allocated **dynamically on the stack frame** of the function:
```assembly
# Stack allocation for a local buffer
addi $sp, $sp, -12      # Allocate 12 bytes: 4 for $ra, 8 for buffer
sw $ra, 8($sp)          # Save return address
move $a0, $sp           # Pass stack address as buffer pointer to syscall
...
lw $ra, 8($sp)          # Restore return address
addi $sp, $sp, 12       # Deallocate stack frame
```
This isolates the function's internal memory state and makes it re-entrant.

### 2. Standard MIPS Calling Conventions
The codebase has been refactored from using a global shared register state (which was vulnerable to clobbering) to strict parameter passing:
* **Arguments** are passed in `$a0` (e.g., active color).
* **Return Values** are returned in `$v0` (e.g., parsed or filtered color).
* **Saved Registers** like `$s0` are utilized in `main` to preserve application state across subroutine calls, with functions correctly saving/restoring them if modified.
* **Call Stack Integrity** is maintained by using `jal` and returning with `jr $ra` throughout the program, eliminating direct jumps to `main` which previously led to stack growth and potential overflow.

### 3. CMYK Mathematical Correctness & Zero-Division Safety
Calculates CMYK values on the MIPS Coprocessor 1 (floating point unit) utilizing normalized RGB floats. It features a critical safety check to prevent division by zero when processing pure black (`#000000`):
$$\begin{aligned}
K &= 1.0 - \max(R', G', B') \\
C &= \frac{\max(R', G', B') - R'}{\max(R', G', B')} \quad (\text{for } \max > 0)
\end{aligned}$$
If $\max = 0.0$, the engine automatically sets $C=0, M=0, Y=0, K=1$ to protect processor execution state from hardware exceptions.

### 4. Hexadecimal String Parsing & Character Handling
Features a robust string-to-integer converter that parses 24-bit RGB values from a hex string. It supports numeric characters (`'0'-'9'`), uppercase letters (`'A'-'F'`), and lowercase letters (`'a'-'f'`) through character-range branching and bitwise shifts (`sll $v0, $v0, 4`).

---

## Repository Structure

```text
├── .gitignore                  # Exclusion file for simulator cache and OS files
├── LICENSE                     # MIT License
├── README.md                   # Recruiter-facing documentation
├── include/
│   └── mips_constants.inc      # System Call Code definitions (e.g., SYS_PRINT_STRING)
├── src/
│   ├── main.s                  # Driver code containing the menu loop and state
│   ├── input.s                 # Input parsers (Hex string and Float RGB)
│   ├── filters.s               # Bitwise filter operations (Red, Green, Blue, etc.)
│   └── display.s               # Subroutines for RGB, CMYK, and Hex conversions
└── tests/
    └── test_filters.s          # Automated test runner asserting filter correctness
```

---

## Menu Controls & Operations

The program features an interactive CLI menu:

| Option | Operation | Details |
| :---: | :--- | :--- |
| **`h`** | Read Hexadecimal | Inputs color from a 6-digit hex string (e.g., `FF00FF` or `3a6b2c`) |
| **`n`** | Read RGB Floats | Inputs color components as floats between `0.0` and `1.0` (e.g., `0.5`, `1.0`, `0.0`) |
| **`i`** | Query Color | Outputs color in: RGB Levels (int), RGB Float, CMYK Float, and Hex format |
| **`r`** | Apply Red Filter | Filters the active color keeping only Red: `color & 0xFF0000` |
| **`v`** | Apply Green Filter | Filters the active color keeping only Green: `color & 0x00FF00` |
| **`a`** | Apply Blue Filter | Filters the active color keeping only Blue: `color & 0x0000FF` |
| **`y`** | Apply Yellow Filter| Filters the active color keeping Red & Green: `color & 0xFFFF00` |
| **`c`** | Apply Cyan Filter | Filters the active color keeping Green & Blue: `color & 0x00FFFF` |
| **`m`** | Apply Magenta Filter| Filters the active color keeping Red & Blue: `color & 0xFF00FF` |
| **`s`** | Exit | Safely terminates the program execution |

---

## Compilation & Execution

### 1. Graphical Interface (MARS GUI)
1. Download and run the [MARS MIPS Simulator](https://courses.missouristate.edu/kenvollmar/mars/).
2. Open `src/main.s` in MARS.
3. Select **Assemble** (F3 or the wrench icon).
4. Run the code (F5 or the green play icon).
5. Interact with the program using the **Run I/O** panel at the bottom of the screen.

### 2. Command Line Interface (MARS CLI)
To run the main program directly from your terminal:
```bash
java -jar Mars.jar src/main.s
```

To run the automated unit test suite:
```bash
java -jar Mars.jar tests/test_filters.s
```
*(The suite will execute assertions on all color filters and output `[SUCCESS] All Unit Tests Passed!`)*
