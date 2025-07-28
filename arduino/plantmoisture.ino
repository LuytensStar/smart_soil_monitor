/*
  Wemos D1 mini  (ESP8266)
  BH1750       ->  SDA=D4, SCL=D3, VCC=3V3, GND
  Soil module  ->  A0, VCC=3V3, GND
  OLED SSD1306 ->  SDA=D4, SCL=D3, VCC=3V3, GND
*/

#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ESP8266WebServer.h>
#include <Wire.h>
#include <BH1750.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include "config.h"

#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, -1);
BH1750 lightMeter;

ESP8266WebServer server(80);

const char* ssid     = WIFI_SSID;
const char* password = WIFI_PASSWORD ;


const int SOIL_PIN = A0;
const int DRY_READING  = 950;   
const int WET_READING  = 350;   

void handleMetrics() {
  
  uint16_t lux   = lightMeter.readLightLevel();
  int rawSoil    = analogRead(SOIL_PIN);
  int moistPct   = constrain(map(rawSoil, DRY_READING, WET_READING, 0, 100), 0, 100);
  int lightPct   = constrain(map(lux, 0, 2000, 0, 100), 0, 100);

  String payload = "{";
  payload += "\"brightness\":" + String(lightPct) + ",";
  payload += "\"moisture\":"   + String(moistPct);
  payload += "}";
  server.send(200, "application/json", payload);
}


void drawBar(int y, const char* label, int percent) {
  display.setCursor(0, y);
  display.printf("%s %3d%%", label, percent);
  int barX = 60, barW = 60, barH = 8;
  display.drawRect(barX, y + 1, barW, barH, WHITE);
  int fill = map(percent, 0, 100, 0, barW - 2);
  display.fillRect(barX + 1, y + 2, fill, barH - 2, WHITE);
}

void setup() {
  Serial.print("ESP IP address: ");
  Serial.println(WiFi.localIP());
  Serial.begin(115200);
  Wire.begin(D14, D15);                     
  lightMeter.begin(BH1750::CONTINUOUS_HIGH_RES_MODE);
  display.begin(SSD1306_SWITCHCAPVCC, 0x3C);
  display.clearDisplay();

  WiFi.begin(ssid, password);
  display.setTextSize(1);
  display.setTextColor(WHITE);

  display.println("Connecting WiFi...");
  display.display();
  while (WiFi.status() != WL_CONNECTED) {
    delay(400);
  }
  display.clearDisplay();
  display.println("WiFi connected");
  display.display();
  delay(800);

  server.on("/metrics", HTTP_GET, handleMetrics);
  server.begin();
  Serial.println("HTTP server started");
}

void loop() {
  
  
  uint16_t lux = lightMeter.readLightLevel();          
  int rawSoil  = analogRead(SOIL_PIN);                 
  int moistPct = constrain(
                   map(rawSoil, DRY_READING, WET_READING, 0, 100),
                 0, 100);
  Serial.printf("RAW lux = %u\n", lux);

  const int MAX_LUX = 2000;
  int lightPct = map(lux, 0, MAX_LUX, 0, 100);
  lightPct = constrain(lightPct, 0, 100);               
  

  
  display.clearDisplay();
  drawBar(0,  "Light",  lightPct);
  drawBar(24, "Soil ",  moistPct);
  display.display();

   server.handleClient();

  delay(10);    
}
