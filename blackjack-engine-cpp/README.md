# Simple Blackjack

[![C++ Standard](https://img.shields.io/badge/C%2B%2B-17-blue.svg?style=flat-square&logo=c%2B%2B)](https://en.cppreference.com/w/cpp/17)
[![License](https://img.shields.io/badge/License-MIT-green.svg?style=flat-square)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-lightgrey.svg?style=flat-square)](#compilation-and-execution)
[![Design Pattern](https://img.shields.io/badge/Architecture-MVC%20%2F%20Decoupled-orange.svg?style=flat-square)](#separation-of-concerns-mvc-design)

A clean, modern, console-based Blackjack (21) game written in C++17. The project has been fully translated, structured, and refactored from a university codebase into a professional-grade portfolio item demonstrating solid object-oriented design, cross-platform compatibility, separation of concerns, and clean code practices.

---

## Technical Highlights

This project demonstrates several advanced software engineering concepts:

### 🛠️ Separation of Concerns (MVC-like Design)
The codebase separates game logic and data representations from the terminal user interface:
* **Models (`Card`, `Deck`, `Player`, `Dealer`, `GameHistory`):** Maintain pure data and game state, entirely independent of how they are printed.
* **View (`ConsoleView`):** Encapsulates all terminal operations, character reads, and styling.
* **Controller (`Game`):** Orchestrates the rules, scoring, and turn loops by coordinating between the Models and the View.
* *Why it matters:* This decoupled structure makes it trivial to replace the terminal console interface with a graphical GUI (like Qt) or a web-based UI without modifying a single line of game logic.

### 🐧 Cross-Platform Terminal Consolidation
Console games often struggle with portability due to reliance on Windows-specific utilities (like `<conio.h>`'s `_getch()` and `system("cls")`). 
* We isolated and abstracted these OS-specific dependencies inside `ConsoleView`. 
* Preprocessor guards dynamically map inputs: using low-level POSIX terminal configurations (`termios.h`, `tcsetattr`) on Linux/macOS, and native `conio.h` on Windows.
* *Why it matters:* The application compiles and runs natively across Windows, Linux, and macOS while maintaining features like instant keypresses without requiring the user to press Enter.

### 🧬 Clean Object-Oriented Design & Fixed Inheritance
* **Subclassing without Shadowing:** The original codebase had the `Dealer` subclass redeclaring `hand` and `score` private members, causing variable shadowing. This was refactored to use clean constructor delegation, allowing `Dealer` to reuse the inherited `Player` properties correctly.
* **Namespace Discipline:** Removed all `using namespace std;` imports from header files to prevent namespace pollution, utilizing explicit namespaces (`std::string`, `std::vector`) instead.

### 🧠 Robust Algorithmic Hand Evaluation
* Card values are dynamically adjusted. The dealer and players evaluate Aces dynamically: an Ace counts as 11 points unless the hand total exceeds 21, in which case it is demoted to 1 point. This evaluation is performed iteratively using standard library algorithms.

### 🧪 Automated Verification
* A dedicated test runner (`TestBlackjack.cpp`) tests the deck's integrity, shuffled distributions, and dynamic Ace values. It is configured to run automatically under standard testing workflows.

---

## Repository Structure

The project follows standard C++ folder conventions:

```text
SimpleBlackJack/
├── CMakeLists.txt              # Cross-platform CMake configuration
├── BlackJack.sln               # Visual Studio Solution file
├── BlackJack.vcxproj           # Visual Studio Project configuration
├── BlackJack.vcxproj.filters   # Visual Studio Solution Explorer filters
├── LICENSE                     # MIT License
├── README.md                   # Project documentation
├── include/                    # Header files
│   ├── Card.h
│   ├── ConsoleView.h
│   ├── Dealer.h
│   ├── Deck.h
│   ├── Exception.h
│   ├── Game.h
│   ├── GameHistory.h
│   └── Player.h
├── src/                        # Implementation files
│   ├── Card.cpp
│   ├── ConsoleView.cpp
│   ├── Dealer.cpp
│   ├── Deck.cpp
│   ├── Game.cpp
│   ├── GameHistory.cpp
│   └── Main.cpp
└── tests/                      # Unit testing suite
    └── TestBlackjack.cpp
```

---

## Game Rules & Controls

The game supports standard Blackjack rules:
* Players attempt to get as close to 21 as possible without exceeding it.
* Aces count as 11 or 1, Face cards (J, Q, K) count as 10, and numeric cards count as their face value.
* The Dealer must hit if their score is 16 or lower, and must stand if it is 17 or higher.
* Natural Blackjack (21 points on the first two cards) beats other hands and skips player turns.

### Input Controls
No Enter key is required for option menus (uses raw terminal input):
* **Main Menu:**
  * Press `1` to start a new game round.
  * Press `2` to view session history (win/loss records).
  * Press `3` to exit.
* **Gameplay:**
  * Press `1` to **Hit** (draw a new card).
  * Press `2` to **Stand** (keep current score and end turn).

---

## Compilation and Execution

### Option A: Using CMake (Recommended & Cross-Platform)

Ensure you have a C++17 compliant compiler and CMake installed.

1. **Generate Build Files:**
   ```bash
   cmake -B build -S .
   ```
2. **Build the Project:**
   ```bash
   cmake --build build --config Release
   ```
3. **Run the Game:**
   * **Windows:**
     ```bash
     .\build\Release\SimpleBlackjack.exe
     ```
   * **macOS / Linux:**
     ```bash
     ./build/SimpleBlackjack
     ```

---

### Option B: Using Visual Studio (Windows)

1. Open `BlackJack.sln` in Visual Studio 2022.
2. Select your build configuration (e.g., `Debug` or `Release`, `x64` or `x86`).
3. Press `F5` to build and run the game, or build the solution from the Build menu.

---

## Running Unit Tests

To run the automated test suite verifying card valuations, deck shuffling, and Ace adjustments:

### With CMake
```bash
# Run the test binary generated in the build step
# Windows:
.\build\tests\Release\TestBlackjack.exe

# macOS / Linux:
./build/TestBlackjack
```

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
