# ESP8266 NTP Time Logger

[![PlatformIO](https://img.shields.io/badge/PlatformIO-Compatible-blue.svg)](https://platformio.org)
[![Language](https://img.shields.io/badge/Language-C%2B%2B11-orange.svg)](https://en.cppreference.com/)
[![Framework](https://img.shields.io/badge/Framework-Arduino-red.svg)](https://www.arduino.cc/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

An elegant, object-oriented Internet of Things (IoT) application for ESP8266 microcontrollers that synchronizes real-world time via Network Time Protocol (NTP) and logs structured chronological event records to local flash storage.

---

## Technical Highlights

This project demonstrates advanced embedded software engineering principles and patterns adapted for resource-constrained microcontrollers:

### 1. Object-Oriented Design (OOD) in C++
Unlike traditional single-file Arduino sketch anti-patterns that rely heavily on global variables and spaghetti code, this firmware uses a decoupled class architecture:
* **[`WiFiManager`](include/WiFiManager.h):** Abstracts connection routines, reconnection loops, and connection timeouts.
* **[`TimeManager`](include/TimeManager.h):** Interfaces with standard system NTP servers, encapsulating offsets and formatted POSIX timestamp parsing.
* **[`StorageManager`](include/StorageManager.h):** Manages local file system input/output (I/O) streams.
* **[`TimeRecord`](include/TimeRecord.h):** An immutable-style data model representing the logged events.

### 2. Embedded Memory Optimization (Heap Safety)
With only ~80KB of instruction-ready RAM on the ESP8266, heap fragmentation is a common cause of sudden system crashes.
* **Stack Allocation:** We avoid dynamic allocations (`new`/`delete` or `malloc`/`free`) during runtime logging.
* **Array-based Storage:** Records are stored and processed as stack-allocated static structures (`TimeRecord[]`), ensuring a predictable memory footprint and preventing memory leaks.
* **Pass-by-Reference:** Code uses `const` references to avoid copying heavy objects.

### 3. File System Modernization: SPIFFS to LittleFS
Upgraded from the deprecated `SPIFFS` to the modern **LittleFS** filesystem, introducing several performance improvements:
* **Wear Leveling:** Extends the lifecycle of the ESP8266's physical flash chips by distributing write cycles across blocks.
* **Power-Loss Resilience:** Implements journaling-like structures that prevent filesystem corruption if power is disconnected during a write operation.
* **Speed & Directory Support:** Offers faster read/write lookups and handles subdirectories efficiently.

### 4. POSIX Time Integration
Instead of importing heavy external time libraries, this application interfaces directly with the native ESP8266 SDK built-in NTP daemon using standard POSIX `time.h` utilities (`configTime`, `localtime`, and `strftime`). This reduces the compiled binary footprint and utilizes highly optimized system calls.

---

## Repository Structure

```text
ESP8266-Time-Logger/
├── platformio.ini               # PlatformIO Project Configuration
├── .gitignore                   # Ignores build artifacts and secret credentials
├── README.md                    # This documentation
├── include/
│   ├── config.h.example         # Template for private credentials
│   ├── WiFiManager.h            # Wi-Fi Controller Header
│   ├── TimeManager.h            # NTP Synchronization Header
│   ├── StorageManager.h         # LittleFS Operations Header
│   └── TimeRecord.h             # Data Model representation
├── src/
│   ├── main.cpp                 # Main loop/entry point
│   ├── WiFiManager.cpp          # WiFi handshaking implementation
│   ├── TimeManager.cpp          # NTP sync & POSIX time implementation
│   └── StorageManager.cpp       # Filesystem read/write & CSV parser
└── tests/
    └── README.md                # Automated testing strategy documentation
```

---

## Execution Flow & Console Outputs

Upon startup, the firmware executes a deterministic sequence of events outputting logs to the `Serial` console at `115200` baud:

| Phase | Action | Serial Log Example |
|---|---|---|
| **Boot** | Board initializes, setups Serial communication | `=== ESP8266 Time Logger Initializing ===` |
| **Wi-Fi** | Connects to router with timeout control | `Connecting to network: Router_SSID` <br> `....`<br>`Successfully connected to: Router_SSID`<br>`IP Address assigned: 192.168.1.50` |
| **NTP** | Synchronizes system clock with NTP server pool | `Local Time: Thu Aug 20 17:50:00 2026` |
| **Storage** | Mounts and formats LittleFS, writes 5 static records | `LittleFS initialized successfully.` <br>`Formatting file system...`<br>`Writing records to file...`<br>`Data written and file closed.` |
| **Loop** | Periodically reads CSV records, parses and logs them | `[Raw CSV]: 15,3,15`<br>`[Parsed] -> Day: 15 \| Time: 03:15` |

---

## Getting Started

### Hardware Requirements
* **Microcontroller:** ESP8266 Board (e.g., NodeMCU V2/V3, Wemos D1 Mini).
* **Interface:** Micro-USB Cable.

### Compilation & Installation

#### Option A: Using PlatformIO (Recommended)
1. Install [VS Code](https://code.visualstudio.com/) and the [PlatformIO IDE Extension](https://platformio.org/).
2. Clone this repository:
   ```bash
   git clone https://github.com/Zintradev/ESP8266-Time-Logger.git
   ```
3. Open the cloned folder in VS Code.
4. Copy the credential template to create your local config file:
   ```bash
   cp include/config.h.example include/config.h
   ```
5. Open `include/config.h` and replace the credentials with your Wi-Fi details:
   ```cpp
   #define WIFI_SSID "YOUR_WIFI_NAME"
   #define WIFI_PASS "YOUR_WIFI_PASSWORD"
   ```
6. Click the **Upload** button (arrow icon at the bottom tray) or run:
   ```bash
   pio run --target upload
   ```
7. Open the Serial Monitor at `115200` baud to view logs:
   ```bash
   pio device monitor
   ```

#### Option B: Using the Arduino IDE
1. Open the Arduino IDE.
2. In the Board Manager, install the `esp8266` core by ESP8266 Community (version 3.0.0 or higher).
3. Create a folder named `ESP8266-Time-Logger` and move the contents of `src/` and `include/` into it, renaming `main.cpp` to `ESP8266-Time-Logger.ino`.
4. Create `config.h` from `config.h.example` in the same directory.
5. In the Arduino IDE settings, select your NodeMCU board.
6. Click **Upload** and open the Serial Monitor at `115200` baud.
