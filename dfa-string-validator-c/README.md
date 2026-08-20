# Deterministic Finite Automaton (DFA) String Validator

[![Language: C](https://img.shields.io/badge/Language-C-blue.svg?style=flat-square)](https://en.wikipedia.org/wiki/C_(programming_language))
[![Standard: C99](https://img.shields.io/badge/Standard-C99-orange.svg?style=flat-square)](https://en.wikipedia.org/wiki/C99)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg?style=flat-square)](LICENSE)

A highly optimized, modular, and thread-safe implementation of a **Deterministic Finite Automaton (DFA)** for string pattern validation in C. 

The automaton is designed to validate strings against a complex pattern featuring optional (epsilon-transition equivalent) and mandatory lexical tokens, representing a typical low-level validator (e.g., for security codes, serial keys, or structured password patterns).

---

## Technical Highlights

This project demonstrates core competencies in **Systems Programming**, **Theory of Computation**, and **Compiler Design**:

- **Theory of Computation & Automata**:
  - Implementation of a formal Deterministic Finite Automaton (DFA) \( M = (Q, \Sigma, \delta, q_0, F) \).
  - Clean representation of epsilon transitions (\(\epsilon\)) resolved at design-time into a deterministic state-transition matrix.
  - Linear-time regular language recognition without the overhead of backtracking or non-deterministic engines.
- **Low-Level Lexical Analysis**:
  - O(1) state transition resolution via a static 2D transition table lookup.
  - Custom classification mapping characters (\(\text{ASCII}\)) into an abstract alphabet (\(\Sigma\)).
- **Algorithmic Complexity**:
  - **Time Complexity**: \(\mathcal{O}(n)\) where \(n\) is the string length. Each input character undergoes character class lookup and matrix indexing in constant time \(\mathcal{O}(1)\).
  - **Space Complexity**: \(\mathcal{O}(1)\) auxiliary space. The automaton is memory-static, using zero dynamic allocations (`malloc`), eliminating memory leaks, stack overflows, and heap fragmentation risks.
- **Defensive & Secure C Practices**:
  - Elimination of global states to ensure the library is **thread-safe** and **reentrant**.
  - Mitigation of buffer overflows by enforcing string limits (`%99s`) on CLI inputs.
  - Strict classification verification checking boundary conditions on transition lookups.

---

## The Automaton Definition

The validator checks if an input string matches the following formal Regular Expression:

$$\mathbf{L} = \mathcal{L}\left( [A-Z]? \cdot P \cdot [a-z0-9] \cdot [A-Za-z]? \cdot P \cdot [a-z]? \cdot [A-Za-z] \cdot [a-z]? \cdot [0-9] \cdot P \right)$$

Where **$P$** represents any standard punctuation character (e.g. `@`, `#`, `$`, `!`).

### State Transition Table

The DFA's state machine \(\delta(q, \sigma)\) is implemented using a lookup matrix:

| State ($Q$) | Class: `[A-Z]` (Upper) | Class: `[a-z]` (Lower) | Class: `[0-9]` (Digit) | Class: `P` (Punctuation) | Invalid / Non-ASCII | Description |
| :--- | :---: | :---: | :---: | :---: | :---: | :--- |
| **0 (START)** | 1 | ERROR | ERROR | 2 | ERROR | Initial state, optional uppercase or first punctuation |
| **1 (OPT_UPPER)** | ERROR | ERROR | ERROR | 2 | ERROR | Optional uppercase read, expects first punctuation |
| **2 (P1)** | ERROR | 3 | 3 | ERROR | ERROR | First punctuation read, expects letter or digit |
| **3 (L_OR_D)** | 4 | 4 | ERROR | 5 | ERROR | Digit/letter read, expects optional letter or second punctuation |
| **4 (OPT_LETTER)** | ERROR | ERROR | ERROR | 5 | ERROR | Optional letter read, expects second punctuation |
| **5 (P2)** | 7 | 6 | ERROR | ERROR | ERROR | Second punctuation read, expects optional lowercase |
| **6 (OPT_LOWER)** | 7 | 7 | ERROR | ERROR | ERROR | Optional lowercase read, expects letter |
| **7 (LETTER)** | ERROR | 8 | 9 | ERROR | ERROR | Letter read, expects optional lowercase |
| **8 (OPT_LOWER_2)**| ERROR | ERROR | 9 | ERROR | ERROR | Optional lowercase 2 read, expects digit |
| **9 (DIGIT)** | ERROR | ERROR | ERROR | 10 | ERROR | Digit read, expects final punctuation |
| **10 (ACCEPT)** | 10 | 10 | 10 | 10 | 10 | Final acceptance state, validation succeeds |
| **11 (ERROR)** | 11 | 11 | 11 | 11 | 11 | Sink/Trap state representing validation failure |

---

## Directory Structure

```text
.
├── include/
│   └── dfa.h          # Public API and state definition headers
├── src/
│   ├── dfa.c          # Core DFA validation & transition table logic
│   └── main.c         # Interactive CLI entrypoint (View/Controller)
├── tests/
│   └── test_dfa.c     # Automated unit test suite
├── Makefile           # Multi-platform compilation script
├── .gitignore         # Strict build and configuration ignore patterns
└── README.md          # Project documentation
```

---

## Quick Start

### Prerequisites
A C-compiler (`gcc` or `clang`) and `make` (optional).

### Compile and Run Main App

**Using Make:**
```bash
make
./dfa_validator
```

**Using GCC directly:**
```bash
gcc -Iinclude src/dfa.c src/main.c -o dfa_validator
./dfa_validator
```

### Compile and Run Tests

**Using Make:**
```bash
make test
```

**Using GCC directly:**
```bash
gcc -Iinclude src/dfa.c tests/test_dfa.c -o test_runner
./test_runner
```

---

## Usage Example

```bash
$ ./dfa_validator
Please write your string (+ENTER): Z@5$A7#

String IS VALID according to the expression.

$ ./dfa_validator
Please write your string (+ENTER): Z5$A7#
Posicion de error: 2
String IS NOT VALID according to the expression.
```
