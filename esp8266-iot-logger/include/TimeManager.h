#ifndef TIME_MANAGER_H
#define TIME_MANAGER_H

#include <Arduino.h>
#include <time.h>

/**
 * @brief Class handling NTP time synchronization and local POSIX-based time retrieval.
 * 
 * Provides an interface to standard time functions in an OOP wrapper.
 */
class TimeManager {
private:
    const char* _ntpServer;
    long _gmtOffsetSec;
    int _daylightOffsetSec;

public:
    /**
     * @brief Construct a new TimeManager object.
     * @param ntpServer NTP server domain (e.g. "pool.ntp.org").
     * @param gmtOffsetSec GMT offset in seconds.
     * @param daylightOffsetSec DST offset in seconds.
     */
    TimeManager(const char* ntpServer, long gmtOffsetSec, int daylightOffsetSec);

    /**
     * @brief Initializes NTP configuration.
     */
    void begin();

    /**
     * @brief Prints the current local time to the Serial console.
     */
    void printLocalTime() const;

    /**
     * @brief Gets a formatted timestamp.
     * @return String formatted time (e.g. "2026-08-20 17:49:00").
     */
    String getFormattedTime() const;
};

#endif // TIME_MANAGER_H
