#include <Arduino.h>
#include "config.h"
#include "WiFiManager.h"
#include "TimeManager.h"
#include "StorageManager.h"
#include "TimeRecord.h"

// Instantiate control classes
WiFiManager wifi(WIFI_SSID, WIFI_PASS);
TimeManager timeKeeper(NTP_SERVER, GMT_OFFSET_SEC, DAYLIGHT_OFFSET_SEC);
StorageManager storage(LOG_FILE_PATH);

void setup() {
    Serial.begin(115200);
    delay(1000);
    Serial.println("\n=== ESP8266 Time Logger Initializing ===");

    // 1. Establish Wi-Fi Connection
    wifi.connect();

    // 2. Initialize NTP Sync
    timeKeeper.begin();
    delay(1000); // Allow time for network socket initialization
    timeKeeper.printLocalTime();

    // 3. Mount and format Filesystem, and write sample records
    if (storage.begin()) {
        // Format filesystem on startup (matching original behavior)
        storage.format();

        // Sample records to write (equivalent to original values: 15/3/15, 3/6/45, 95/22/0, etc.)
        TimeRecord sampleRecords[] = {
            TimeRecord(15, 3, 15),
            TimeRecord(3, 6, 45),
            TimeRecord(95, 22, 0),
            TimeRecord(7, 12, 20),
            TimeRecord(127, 18, 30)
        };
        size_t count = sizeof(sampleRecords) / sizeof(sampleRecords[0]);

        storage.writeRecords(sampleRecords, count);
    }
    
    Serial.println("=== Initialization Completed ===\n");
}

void loop() {
    // 1-second delay matching original cadence
    delay(1000);
    
    // Print current synchronized local time
    timeKeeper.printLocalTime();
    
    // Open, read, parse and display stored records
    storage.readAndPrintRecords();
    
    // 5-second sleep between readings, matching original cadence
    delay(5000);
}
