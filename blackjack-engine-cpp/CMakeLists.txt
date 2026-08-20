cmake_minimum_required(VERSION 3.10)
project(SimpleBlackjack LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Include directories
include_directories(include)

# Source files (shared between executable and tests)
set(GAME_SOURCES
    src/Card.cpp
    src/Deck.cpp
    src/Player.cpp
    src/Dealer.cpp
    src/GameHistory.cpp
    src/ConsoleView.cpp
    src/Game.cpp
)

# Application executable
add_executable(SimpleBlackjack src/Main.cpp ${GAME_SOURCES})

# Test executable
add_executable(TestBlackjack tests/TestBlackjack.cpp ${GAME_SOURCES})
