#ifndef STORAGE_MANAGER_H
#define STORAGE_MANAGER_H

#include <LittleFS.h>
#include "TimeRecord.h"

/**
 * @brief Handles file system mounting, formatting, and file I/O operations.
 * 
 * Replaces the deprecated SPIFFS with LittleFS, which is faster and supports wear leveling.
 */
class StorageManager {
private:
    const char* _filename;

public:
    /**
     * @brief Construct a new StorageManager object.
     * @param filename Path of the file to manage (e.g. "/time_log.csv").
     */
    StorageManager(const char* filename);

    /**
     * @brief Mounts the LittleFS filesystem.
     * @return true if successful, false otherwise.
     */
    bool begin();

    /**
     * @brief Formats the LittleFS filesystem.
     * @return true if successful, false otherwise.
     */
    bool format();

    /**
     * @brief Overwrites the file with the provided array of records.
     * @param records Pointer to the array of records.
     * @param count Number of elements in the array.
     * @return true if successful, false otherwise.
     */
    bool writeRecords(const TimeRecord* records, size_t count);

    /**
     * @brief Opens the file in read-only mode, parses and displays the contents.
     * @return true if successful, false otherwise.
     */
    bool readAndPrintRecords();
};

#endif // STORAGE_MANAGER_H
