#include "WiFiManager.h"

WiFiManager::WiFiManager(const char* ssid, const char* password) 
    : _ssid(ssid), _password(password) {}

void WiFiManager::connect(unsigned long timeoutMs) {
    WiFi.mode(WIFI_STA);
    WiFi.begin(_ssid, _password);
    
    Serial.print("Connecting to network: ");
    Serial.println(_ssid);

    unsigned long startMillis = millis();
    while (WiFi.status() != WL_CONNECTED) {
        delay(200);
        Serial.print('.');
        if (millis() - startMillis >= timeoutMs) {
            Serial.println("\nConnection timed out!");
            return;
        }
    }

    Serial.println();
    Serial.print("Successfully connected to: ");
    Serial.println(WiFi.SSID());
    Serial.print("IP Address assigned: ");
    Serial.println(WiFi.localIP());
}

bool WiFiManager::isConnected() const {
    return WiFi.status() == WL_CONNECTED;
}

String WiFiManager::getIPAddress() const {
    return WiFi.localIP().toString();
}
