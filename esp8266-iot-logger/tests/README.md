# Tests Directory

This directory is structured for automated testing within the PlatformIO ecosystem.

## Testing Strategy
In embedded firmware development, tests are typically split into:
1. **Native Tests:** Executed on the host compiler (your development machine) to verify hardware-independent logic (e.g., parsing, data modeling, formatting).
2. **Embedded Tests:** Executed on the target hardware (ESP8266) to verify hardware-dependent behavior (e.g., Wi-Fi handshake, file system writing, NTP sockets).

## How to Run Tests
You can run automated tests using the PlatformIO Core CLI:

```bash
# Run tests on the target board / simulator
pio test -e nodemcuv2
```

The testing harness leverages the **Unity** testing framework built directly into PlatformIO.
