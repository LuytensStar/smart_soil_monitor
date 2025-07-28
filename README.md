# Soil Moisture Monitor

A full-stack IoT project that monitors light and soil moisture using an ESP8266 (Wemos D1 mini), displays data locally on an SSD1306 OLED, serves a REST API via Flask, and provides a Kotlin Android client.

---

## Features

- **Real-time sensing** of ambient light (BH1750) and soil moisture (LM393).
- **Local display**: progress bars on 0.96" SSD1306 OLED.
- **REST API & dashboard**: Flask server collects JSON data every 10 s and serves a live dashboard.
- **Android client**: Jetpack Compose app polls the Flask API every 2 s, showing identical progress bars.
- **Configurable thresholds**: easily adapt moisture thresholds for alerts or watering logic.

---

## Hardware

- **Wemos D1 mini** (ESP8266)
- **BH1750** light sensor (I²C: SDA = D2, SCL = D1)
- **LM393** soil moisture sensor (A0)
- **SSD1306** 128×64 OLED (I²C: SDA = D2, SCL = D1)
- (Optional) MOSFET (e.g., IRL540N) + pump for watering
- Breadboard, jumper wires, 3.3 V power for sensors

### Wiring Diagram



| Device           | Pin        | Connection |
| ---------------- | ---------- | ---------- |
| BH1750 SDA       | D2 (GPIO4) | SDA        |
| BH1750 SCL       | D1 (GPIO5) | SCL        |
| LM393 analog out | A0         | Soil pin   |
| SSD1306 SDA      | D2 (GPIO4) | SDA        |
| SSD1306 SCL      | D1 (GPIO5) | SCL        |
| SSD1306 VCC, GND | 3.3 V, GND | Power      |

---

## Software Components

1. **Arduino sketch** (`/arduino`)
2. **Flask server** (`/flask`)
3. **Android app** (`/android`)

### Repository Structure

```
smart-plant/
├─ arduino/        # ESP8266 sketch + secrets.h
├─ flask/          # Flask app, requirements.txt, Dockerfile
└─ android/        # Android Studio project (Kotlin + Compose)
```

---

## Setup Instructions

### 1. Arduino Firmware

1. Open `/arduino` in Arduino IDE.
2. Copy `config.example.h` to `config.h` and fill in:
   ```cpp
   #define WIFI_SSID     "ssid"
   #define WIFI_PASSWORD "pass"
   #define FLASK_BASE_URL "http://<FLASK_HOST>:<PORT>"
   ```
3. Upload the sketch to the Wemos D1 mini.

### 2. Flask Server

#### Prerequisites

- Python 3.11+
- pip

#### Local Run

```bash
cd flask
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

Access the dashboard at `http://localhost:<port>/`.


### 3. Android App

1. Open `/android` in Android Studio.
2. Create or update `local.properties`:
   ```properties
   FLASK_BASE_URL=http://localhost:<port>/
   ```
3. Sync Gradle and build the project.
4. Run on emulator or device.


