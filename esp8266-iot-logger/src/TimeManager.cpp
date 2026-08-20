#include "TimeManager.h"

TimeManager::TimeManager(const char* ntpServer, long gmtOffsetSec, int daylightOffsetSec)
    : _ntpServer(ntpServer), _gmtOffsetSec(gmtOffsetSec), _daylightOffsetSec(daylightOffsetSec) {}

void TimeManager::begin() {
    configTime(_gmtOffsetSec, _daylightOffsetSec, _ntpServer);
}

void TimeManager::printLocalTime() const {
    time_t rawTime;
    struct tm* timeInfo;
    time(&rawTime);
    timeInfo = localtime(&rawTime);
    
    if (timeInfo->tm_year < 70) {
        Serial.println("Local Time: (Waiting for NTP sync...)");
    } else {
        Serial.print("Local Time: ");
        Serial.print(asctime(timeInfo));
    }
}

String TimeManager::getFormattedTime() const {
    time_t rawTime;
    struct tm* timeInfo;
    time(&rawTime);
    timeInfo = localtime(&rawTime);
    
    if (timeInfo->tm_year < 70) {
        return "N/A (Not Synced)";
    }
    
    char buffer[30];
    strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", timeInfo);
    return String(buffer);
}
