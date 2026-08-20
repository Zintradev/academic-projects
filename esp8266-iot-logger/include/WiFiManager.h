#ifndef WIFI_MANAGER_H
#define WIFI_MANAGER_H

#include <ESP8266WiFi.h>

/**
 * @brief Class responsible for managing Wi-Fi state and connectivity.
 * 
 * Encapsulates the connection logic and prevents polluting the global namespace.
 */
class WiFiManager {
private:
    const char* _ssid;
    const char* _password;

public:
    /**
     * @brief Construct a new WiFiManager object.
     * @param ssid WiFi Network name.
     * @param password WiFi Password.
     */
    WiFiManager(const char* ssid, const char* password);

    /**
     * @brief Connects to the specified WiFi network with an optional timeout.
     * @param timeoutMs Timeout in milliseconds (default: 15 seconds).
     */
    void connect(unsigned long timeoutMs = 15000);

    /**
     * @brief Checks if WiFi is connected.
     * @return true if connected, false otherwise.
     */
    bool isConnected() const;

    /**
     * @brief Gets the assigned local IP address.
     * @return String IP address.
     */
    String getIPAddress() const;
};

#endif // WIFI_MANAGER_H
