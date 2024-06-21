#include <Servo.h>

const int trigPin = 2; // 초음파 센서의 Trig 핀
const int echoPin = 3; // 초음파 센서의 Echo 핀

const int redPin = 9;  // RGB LED의 Red 핀
const int greenPin = 10; // RGB LED의 Green 핀
const int bluePin = 11; // RGB LED의 Blue 핀

const int lightSensorPin = A0; // 조도센서의 핀

const int servoPin = 6;

Servo servo1; // 서보모터 객체

void setup() {
  Serial.begin(9600);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(redPin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  pinMode(bluePin, OUTPUT);
  pinMode(lightSensorPin, INPUT);
  servo1.attach(servoPin);
}

void loop() {
  long duration, distance;
  
  // 초음파 센서로 거리 측정
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
  duration = pulseIn(echoPin, HIGH);
  distance = duration * 0.034 / 2;

  if (distance <= 15) { // 물체가 12cm 이내에 있는 경우
    analogWrite(redPin, 255); // 빨간색 LED 켜기
    analogWrite(greenPin, 255); // 녹색 LED 켜기
    analogWrite(bluePin, 255); // 파란색 LED 켜기
    delay(1500); // 1초 대기

    // 조도센서로 밝기 측정
    int lightValue = analogRead(lightSensorPin);
    Serial.println(lightValue);
    
    if (lightValue > 700) { // 밝기가 700보다 큰 경우
      servo1.write(180);
      delay(1080);
      servo1.write(90);
      delay(1080);
      servo1.write(0);
      delay(1100);
      servo1.write(90);
    
    } else {
      servo1.write(0);
      delay(1080);
      servo1.write(90);
      delay(1080);
      servo1.write(180);
      delay(1060);
      servo1.write(90);
    }
    
    // LED 끄기
    analogWrite(redPin, 0);
    analogWrite(greenPin, 0);
    analogWrite(bluePin, 0);
  }
}