CC = gcc
CFLAGS = -Wall -Wextra -std=c99 -Iinclude

SRC_DIR = src
INC_DIR = include
BUILD_DIR = build
TEST_DIR = tests

# Target names
TARGET = dfa_validator
TEST_TARGET = test_runner

# Sources
SRCS = $(SRC_DIR)/main.c $(SRC_DIR)/dfa.c
TEST_SRCS = $(TEST_DIR)/test_dfa.c $(SRC_DIR)/dfa.c

# Objects
OBJS = $(BUILD_DIR)/main.o $(BUILD_DIR)/dfa.o
TEST_OBJS = $(BUILD_DIR)/test_dfa.o $(BUILD_DIR)/dfa.o

.PHONY: all clean test run

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) -o $@ $^

$(TEST_TARGET): $(TEST_OBJS)
	$(CC) $(CFLAGS) -o $@ $^

$(BUILD_DIR)/%.o: $(SRC_DIR)/%.c | $(BUILD_DIR)
	$(CC) $(CFLAGS) -c $< -o $@

$(BUILD_DIR)/test_dfa.o: $(TEST_DIR)/test_dfa.c | $(BUILD_DIR)
	$(CC) $(CFLAGS) -c $< -o $@

$(BUILD_DIR):
	mkdir -p $(BUILD_DIR)

test: $(TEST_TARGET)
	./$(TEST_TARGET)

run: $(TARGET)
	./$(TARGET)

clean:
	rm -rf $(BUILD_DIR) $(TARGET) $(TEST_TARGET)
