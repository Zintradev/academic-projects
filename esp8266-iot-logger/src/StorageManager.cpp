#include "StorageManager.h"

StorageManager::StorageManager(const char* filename) : _filename(filename) {}

bool StorageManager::begin() {
    if (LittleFS.begin()) {
        Serial.println("LittleFS initialized successfully.");
        return true;
    } else {
        Serial.println("LittleFS initialization failed.");
        return false;
    }
}

bool StorageManager::format() {
    Serial.println("Formatting file system...");
    if (LittleFS.format()) {
        Serial.println("File system formatted successfully.");
        return true;
    } else {
        Serial.println("File system formatting error.");
        return false;
    }
}

bool StorageManager::writeRecords(const TimeRecord* records, size_t count) {
    File f = LittleFS.open(_filename, "w");
    if (!f) {
        Serial.println("Failed to open file for writing.");
        return false;
    }

    Serial.println("Writing records to file...");
    for (size_t i = 0; i < count; i++) {
        f.println(records[i].toCSV());
    }
    
    f.close();
    Serial.println("Data written and file closed.");
    return true;
}

bool StorageManager::readAndPrintRecords() {
    File f = LittleFS.open(_filename, "r");
    if (!f) {
        Serial.println("Failed to open file for reading.");
        return false;
    }

    Serial.println("Reading records from file (raw lines):");
    while (f.available()) {
        String line = f.readStringUntil('\n');
        line.trim(); // Remove trailing carriage returns / newlines
        if (line.length() > 0) {
            // Print raw line (CSV)
            Serial.print("  [Raw CSV]: ");
            Serial.println(line);
            
            // Parse and print structured info
            int firstComma = line.indexOf(',');
            int secondComma = line.indexOf(',', firstComma + 1);
            if (firstComma != -1 && secondComma != -1) {
                String dayStr = line.substring(0, firstComma);
                String hourStr = line.substring(firstComma + 1, secondComma);
                String minStr = line.substring(secondComma + 1);
                
                TimeRecord record(dayStr.toInt(), hourStr.toInt(), minStr.toInt());
                Serial.print("  [Parsed] -> ");
                Serial.println(record.toString());
            }
        }
    }
    
    f.close();
    Serial.println("Finished reading file.");
    return true;
}
