#include <ClickButton.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <NeoPixelBrightnessBus.h>
#include <NeoPixelBus.h>
#include <NeoPixelAnimator.h>
#include <Math.h>
#include "LoopSinGenerator.h"

#define SLAVE_ADDRESS 0x04

const uint8_t buttonPin = 5;
const uint8_t rpiPin = 6;
const uint8_t PixelPin = 9;
const uint16_t PixelCount = 1;
bool buttonPressed = false;
int state = -2;
float rads = 0.0;

LiquidCrystal_I2C lcd(0x3F, 16, 2);
NeoPixelBrightnessBus<NeoRgbFeature, Neo800KbpsMethod> strip(PixelCount, PixelPin);
ClickButton button(buttonPin, LOW, CLICKBTN_PULLUP);
LoopSinGenerator sinGen = LoopSinGenerator(256, .01);

int lightLevel = 0;
RgbColor color(255, 0, 0);

void setup() {
  pinMode(buttonPin, INPUT_PULLUP);
  Serial.begin(9600);

  Wire.begin(SLAVE_ADDRESS);
  Wire.onReceive(receiveData);
  Wire.onRequest(handleRequest);

  lcd.display();
  lcd.home();
  showMessage("Please wait...", "");

  strip.Begin();  
  strip.SetPixelColor(0, color);
  strip.Show();

  Serial.println("Ready!");

}

void loop() {
  // put your main code here, to run repeatedly:
  button.Update();
  if (state == -2) {
    int lcdBrightness = sinGen.getValue();
    strip.SetPixelColor(0, color);
    strip.SetBrightness(lcdBrightness);
    strip.Show();
  } else if (state == -1) {
    int lcdBrightness = sinGen.getValue();
    strip.SetPixelColor(0, color);
    strip.SetBrightness(lcdBrightness);
    strip.Show();
  } else if (state==10) {
    Serial.println("state == 10");
    color = RgbColor(0, 255, 0);
    strip.SetPixelColor(0, color);
    strip.Show();
    showMessage("Press button to begin","");
    state = -1;
  } else if (state==1) {
    Serial.println("state == 1");
    showMessage("Booting...", "");
    state = -1;
  } else if (state == 20) {
    Serial.println("state == 20");
    showMessage("Result:", "Air");
    digitalWrite(rpiPin, LOW);
    state = -1;
  } else if (state == 21) {
    Serial.println("state == 21");
    showMessage("Result:", "Water");
    digitalWrite(rpiPin, LOW);
    state = -1;
  } else if (state == 22) {
    Serial.println("state == 22");
    showMessage("Result:", "Lead Detected");
    digitalWrite(rpiPin, LOW);
    state = -1;
  } 
  if (button.clicks == 1) {
    Serial.println("Running analysis");
    buttonPressed = true;
    showMessage("Running", "Analysis...");
    digitalWrite(rpiPin, HIGH);
    state = -3;
  } else if (button.clicks == 2) {
    showMessage("You could show", "IP here");
  } else if (button.clicks == 3) {
    showMessage("You pressed 3", "times");
  }
//  delay(100);
//  lcd.print(lightLevel);
}

void showMessage(char* line1, char* line2) {
  lcd.clear();
  lcd.setCursor(0,0);
  lcd.print(line1);
  lcd.setCursor(0,1);
  lcd.print(line2);
}

void handleRequest() {
  Serial.println("Handling request: ");
  lightLevel = analogRead(0);     // read the input pin
  Wire.write(lightLevel);
  Serial.println(lightLevel);             // debug value
//  lcd.print(lightLevel);
}


void receiveData(int byteCount){
  Serial.print("Rcv Data byte count: ");
  Serial.print(byteCount);
  if (byteCount == 1) {
    state = Wire.read();
  } else if (byteCount == 3) {
    while(Wire.available()) {
      int red = Wire.read();
      int green = Wire.read();
      int blue = Wire.read();
      Serial.print(" - data received: ");
      Serial.print(red);
      Serial.print(", ");
      Serial.print(green);
      Serial.print(", ");
      Serial.println(blue);
      RgbColor color(red, green, blue);
      strip.SetPixelColor(0, color);
      strip.Show();
    }
  } else if (byteCount == 2) {
      int num1 = Wire.read();
      int num2 = Wire.read();
//      Serial.print("Num1: ");
//      Serial.print(num1);
//      Serial.print(" Num2: ");
//      Serial.println(num2);
    }  else if (byteCount == 8) {
      lcd.clear();
      lcd.home();
      lcd.print("Done...");
    }
//  RgbColor color(0, 0, 0);
//  strip.SetPixelColor(0, color);
//  strip.Show();
}
