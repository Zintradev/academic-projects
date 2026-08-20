cmake_minimum_required(VERSION 3.10)
project(GameCharacterManager C)

# Use standard C99
set(CMAKE_C_STANDARD 99)
set(CMAKE_C_STANDARD_REQUIRED ON)

# Include folder paths
include_directories(include)

# Define source files for the main application
set(APP_SOURCES
    src/main.c
    src/character.c
    src/storage.c
    src/ui.c
)

# Build the main character manager application
add_executable(character_manager ${APP_SOURCES})

# Build the unit tests executable
add_executable(unit_tests
    tests/test_character.c
    src/character.c
)
