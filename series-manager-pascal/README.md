# TV Series Manager 🎬

A clean, robust TV series management desktop application developed in **Object Pascal** using the **Lazarus Integrated Development Environment (IDE)** and the **Free Pascal Compiler (FPC)**.

[![Language](https://img.shields.io/badge/Language-Object%20Pascal-blue.svg)](https://www.lazarus-ide.org/)
[![Compiler](https://img.shields.io/badge/Compiler-Free%20Pascal-orange.svg)](https://www.freepascal.org/)
[![IDE](https://img.shields.io/badge/IDE-Lazarus-green.svg)](https://www.lazarus-ide.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Tests](https://img.shields.io/badge/Tests-Passed-brightgreen.svg)](tests/TestSeries.lpr)

This repository serves as a professional showcase of Object Pascal development. It demonstrates clean coding principles, custom binary database persistence, modern state encapsulation, defensive design patterns, and decoupled test automation.

---

## 📂 Repository Structure

The project follows standard repository organization guidelines:

```bash
BasicSeriesManager-Pascal/
├── BasicSeriesManager.lpi      # Lazarus Project Information file (Project config)
├── .gitignore                  # Strict compiler/session exclusions for FPC/Lazarus
├── LICENSE                     # MIT License details
├── README.md                   # Recruiter-facing documentation
├── images/                     # Local asset storage directory
│   └── blanco.jpg              # Default cover art placeholder
├── src/                        # Main application source code
│   ├── BasicSeriesManager.lpr  # Entry point of the GUI application
│   ├── USeries.pas             # Domain Model & Binary Persistence logic
│   ├── UMainMenu.pas / .lfm    # Main Menu form controller & visual layout
│   ├── UAddSeries.pas / .lfm   # Series registration form controller & visual layout
│   └── UListSeries.pas / .lfm  # CRUD manager form controller & visual layout
└── tests/                      # Automated test suite
    └── TestSeries.lpr          # Non-GUI console integration test runner
```

---

## ⚡ Technical Highlights

This application is built to showcase advanced programming and architectural concepts, elevating it beyond basic university assignments:

### 1. Structured Binary Persistence (Stack vs. Heap Memory)
Unlike standard Object Pascal `class` structures which are allocated on the heap and represented by 32/64-bit pointers, this project utilizes Turbo Pascal-style `object` records (`TSeries`). 
- **The Challenge**: Serializing heap-allocated objects directly to disk is impossible since pointer addresses change between runs.
- **The Solution**: Static `object` types are allocated sequentially on the stack or inline. This ensures contiguous memory alignment of fixed-size strings (`string[30]`) and integers, allowing direct block writes to disk via a typed binary file structure (`file of TSeries`).

### 2. State Encapsulation (Elimination of Globals)
Earlier versions of this project relied on unsafe global variables inside implementation sections to track the current active record, index ranges, and local images.
- **Refactoring**: Encapsulated all state variables directly as private attributes of the form classes (e.g., `FCurrentIndex`, `FLastIndex`, `FCurrentSeries`, and `FPhotoName`). 
- **Impact**: This ensures proper object-oriented scope, prevents cross-unit side effects, and avoids memory or state corruption when multiple instances of forms are active.

### 3. Decoupled Architecture & UI Separation
The domain logic (in [USeries.pas](file:///c:/Users/zintr/Documents/GitHub_Repo/BasicSeriesManager-Pascal/src/USeries.pas)) is fully decoupled from the graphical user interface.
- Dead console display methods (`mostrar`) were removed to prevent UI side effects from leaking into the model.
- Standard file-handling functions (such as `GetRecordCount` and `TSeries.Delete`) operate completely on binary file data, allowing them to be run without loading the Lazarus Component Library (LCL) or GUI forms.

### 4. Non-GUI Integration Testing
By separating domain logic from LCL, this project includes an automated test runner ([TestSeries.lpr](file:///c:/Users/zintr/Documents/GitHub_Repo/BasicSeriesManager-Pascal/tests/TestSeries.lpr)).
- Runs directly from the console to test the lifecycle of `TSeries` objects.
- Tests validation, binary file generation, sequential record offset reads, multi-record indexing, and record-shifting deletions.

---

## 🎮 Controls & Interface Usage

| Screen | Control | Action | Rationale |
| :--- | :--- | :--- | :--- |
| **Main Menu** | `Add New Series` | Opens the creation module | Hides menu and instantiates Add Form |
| | `Manage Series` | Opens the CRUD listing module | Hides menu and instantiates List Form |
| **Add Series** | `Select Cover` | Launches picture selector | Copies selected image file into `images/` directory |
| | `Save` | Saves entries to binary DB | Validates data and commits to `series.dat` |
| | `Back` | Returns to Main Menu | Restores Main Menu form |
| **Manage Series**| `Previous` / `Next` | Browses records sequentially | Decrements or increments active record index |
| | `Series List` (ListBox)| Jump-selects a series | Click-to-select maps index to disk offset directly |
| | `Change Cover` | Modifies active cover art | Imports and saves new graphic asset to `images/` |
| | `Save Changes` | Modifies active database entry | Triggers `TryStrToInt` verification and rewrites entry |
| | `Delete` | Deletes selected series | Shifts file records sequentially to delete, then refreshes list |

---

## 🛠️ Build and Compilation Guide

### Prerequisites
- **Lazarus IDE**: [Download here](https://www.lazarus-ide.org/index.php?page=downloads) (v2.0 or newer).
- **Free Pascal Compiler (FPC)**: Included automatically with Lazarus IDE installation.

### Option A: Standard Build via Lazarus IDE
1. Open Lazarus IDE.
2. Go to the top menu and select **Project** ➔ **Open Project...**
3. Select `BasicSeriesManager.lpi` in the root of this folder.
4. Press **F9** (or select **Run** ➔ **Run**) to compile and launch the application.

### Option B: Command Line Build (Recruiter/CI Verification)
You can compile the binary without opening the GUI IDE.

**1. Compile the Main Application:**
```bash
# Using lazbuild
lazbuild BasicSeriesManager.lpi
```

**2. Compile and Run the Automated Integration Tests:**
```bash
# Compile the test runner
fpc -Mobjfpc -Fusrc -FEtests tests/TestSeries.lpr

# Run tests
./tests/TestSeries
```

---

## 📄 License
This project is open-source and licensed under the **MIT License**. Check out the [LICENSE](LICENSE) file for more information.

---
Developed with clean code principles by [Zintradev](https://github.com/Zintradev) 💻.
