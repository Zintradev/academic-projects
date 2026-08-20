#ifndef TIME_RECORD_H
#define TIME_RECORD_H

#include <Arduino.h>

/**
 * @brief Represents a logged time event with day, hour, and minute.
 * 
 * Demonstrates basic encapsulation and data modeling.
 */
struct TimeRecord {
    uint8_t day;
    uint8_t hour;
    uint8_t minute;

    // Constructors
    TimeRecord() : day(0), hour(0), minute(0) {}
    TimeRecord(uint8_t d, uint8_t h, uint8_t m) : day(d), hour(h), minute(m) {}

    /**
     * @brief Formats the record as a CSV line.
     * @return Comma-separated representation (e.g. "15,3,15").
     */
    String toCSV() const {
        return String(day) + "," + String(hour) + "," + String(minute);
    }

    /**
     * @brief Formats the record as a human-readable string.
     * @return Formatted string representation.
     */
    String toString() const {
        char buffer[32];
        snprintf(buffer, sizeof(buffer), "Day: %02d | Time: %02d:%02d", day, hour, minute);
        return String(buffer);
    }
};

#endif // TIME_RECORD_H
