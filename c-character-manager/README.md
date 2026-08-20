# Game Character Manager

[![Language: C](https://img.shields.io/badge/Language-C-00599C.svg?style=flat&logo=c)](https://en.wikipedia.org/wiki/C_(programming_language))
[![Standard: C99](https://img.shields.io/badge/Standard-C99-orange.svg)](https://en.wikipedia.org/wiki/C99)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Build: Cross-Platform](https://img.shields.io/badge/Build-CMake%20%7C%20Make%20%7C%20PowerShell-blue.svg)](#compilation--execution)

A modular, clean, and memory-safe console application written in C to manage game character profiles with local database file persistence.

This project is structured according to professional C engineering standards, demonstrating clean architecture, strict memory management, and solid testing principles.

---

## 🚀 Features

- **Unified CLI Application**: Manage your game characters in a centralized menu loop.
- **Dynamic Collection Management**: Create, view, and delete character records with automated resizing arrays.
- **File Persistence**: Load and save character profiles to a local file (`characters.txt`).
- **Smart Startup Initialization**: Automatically boots with your saved file or defaults to seeding 3 randomly generated characters if no database exists.
- **Robust Input Control**: Prevent buffer overflows and command-line crashes.

---

## 🛠️ Technical Highlights

This project demonstrates advanced concepts in C programming, making it a strong demonstration of foundational software engineering:

- **Modular Architecture (separation of concerns)**:
  - **Models & Logic** (`include/character.h`, `src/character.c`): Represents the domain definitions (`Character`, `CharacterManager`) and encapsulated mutations.
  - **Persistence Layer** (`include/storage.h`, `src/storage.c`): Handles high-level file operations.
  - **View / Presentation Layer** (`include/ui.h`, `src/ui.c`): Decoupled CLI input-output formatting.
- **Strict Dynamic Memory Management**:
  - Employs dynamic reallocation (`realloc`) to grow and shrink the character database arrays on demand, ensuring zero wasted memory footprint.
  - Custom memory deallocation routines (`character_manager_free`) that traverse nested pointers (like the dynamically allocated character name strings) to prevent memory leaks.
- **Security & Buffer Safety**:
  - Replaced the legacy, highly vulnerable `gets()` input function with standard `fgets()` and string sanitization, defending the application against stack buffer overflows.
- **Robust Persistence Serialization**:
  - Implements a custom parser using `strtok` with pipe delimiters (`|`), allowing character names to safely contain spaces (unlike standard space-separated `fscanf` which breaks on multi-word strings).
- **Unit Testing**:
  - Includes a standalone unit-test suite (`tests/test_character.c`) utilizing standard `assert` headers to verify allocation, deletion, index shifting, and structural lifecycle invariants.

---

## 🎮 Controls & Usage

Upon execution, the terminal presents a menu to coordinate character manager commands:

| Choice | Option | Description |
| :---: | :--- | :--- |
| **`1`** | **Delete a character** | Lists all characters, prompts for a specific numeric ID, shifts remaining database array elements, and resizes memory. |
| **`2`** | **Add new character(s)** | Interactively prompts for metadata (name, release year, genre) and appends characters dynamically. |
| **`3`** | **List characters** | Renders all loaded characters in a beautifully aligned CLI grid. |
| **`4`** | **Delete all characters** | Purges all character structures from heap memory locally (requires prompt confirmation). |
| **`5`** | **Save characters to file** | Serializes the active database state back to `characters.txt`. |
| **`6`** | **Exit** | Cleans up all resources, frees allocated blocks, and terminates safely. |

---

## ⚙️ Compilation & Execution

This project is build-tool agnostic and compiles seamlessly on Windows, Linux, and macOS.

### Requirements
- A standard C compiler (`gcc`, `clang`, or MSVC `cl.exe`).
- *(Optional)* CMake (version 3.10+).

---

### Option A: CMake (Recommended Cross-Platform)
From the root directory, generate build directories and compile:

```bash
# Configure CMake build files
cmake -B build

# Build target binaries (both main app and unit tests)
cmake --build build
```

- Run the application:
  ```bash
  # Linux / macOS
  ./build/character_manager
  
  # Windows
  .\build\Debug\character_manager.exe
  ```
- Run the unit tests:
  ```bash
  # Linux / macOS
  ./build/unit_tests
  
  # Windows
  .\build\Debug\unit_tests.exe
  ```

---

### Option B: Makefile (Linux / macOS / Git Bash)
A standard Makefile is provided for environments equipped with GCC and GNU Make:

```bash
# Compile the character manager
make

# Run the application
./character_manager

# Compile and run unit tests
make test

# Clear compilation artifacts
make clean
```

---

### Option C: PowerShell Script (Windows Native)
If you are developing on Windows and have any C compiler (GCC, Clang, or MSVC) installed:

```powershell
# Run the automated build script (creates executables in bin/)
.\build.ps1

# Run the application
.\bin\character_manager.exe

# Run the unit tests
.\bin\unit_tests.exe
```
