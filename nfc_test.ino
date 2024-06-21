#include <SPI.h>
#include <MFRC522.h>
#include <Servo.h>

#define SS_PIN 10
#define RST_PIN 9
#define SERVO1_PIN 3
#define SERVO2_PIN 5

MFRC522 mfrc522(SS_PIN, RST_PIN);  // MFRC522
Servo servo1;
Servo servo2;

const int redPin = 7;  // RGB LED의 Red 핀

bool cardPresent = false;

void setup() {
  Serial.begin(9600);  // PC와의 시리얼 통신 초기화
  SPI.begin();          // SPI 버스 초기화
  mfrc522.PCD_Init();   // MFRC522 초기화
  servo1.attach(SERVO1_PIN); // 서보모터1 핀 설정
  servo2.attach(SERVO2_PIN); // 서보모터2 핀 설정

  pinMode(redPin, OUTPUT);
  analogWrite(redPin, 255);
}

void loop() {
   // nfc 태그 감지
  if (mfrc522.PICC_IsNewCardPresent() && mfrc522.PICC_ReadCardSerial()) {

    String content = "";
    for (byte i = 0; i < mfrc522.uid.size; i++) {
      // 문자열로 변환하여 content에 추가
      content.concat(String(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " "));
      content.concat(String(mfrc522.uid.uidByte[i], HEX));
    }
    
    analogWrite(redPin, 0);
    delay(50);
    analogWrite(redPin, 255);
    delay(50);
    analogWrite(redPin, 0);
    delay(50);
    analogWrite(redPin, 255);
    delay(50);
    analogWrite(redPin, 0);
    delay(50);
    analogWrite(redPin, 255);


    Serial.println("양주시-001");
    delay(500); // 2초 대기
  }

  if (Serial.available() > 0) { // 시리얼 포트로부터 데이터를 읽을 수 있는지 확인
    char data = Serial.read(); // 시리얼 데이터 읽기
    
    // 받은 데이터에 따라 서보모터 제어
    if (data == '0') {
      
      servo1.write(150); // 160도 각도로 회전
      delay(1000);
      servo1.write(90); // 90도 각도로 회전
      delay(500);

      servo2.write(180); // 90도 각도로 회전
      delay(1010);
      servo2.write(90);
      delay(1000);
      servo2.write(0);
      delay(1050);
      servo2.write(90);
      

    } else if (data == '1') {
      
      servo1.write(150); // 160도 각도로 회전
      delay(1000);
      servo1.write(90); // 90도 각도로 회전
      delay(500);

      servo2.write(0); // 90도 각도로 회전
      delay(1010);
      servo2.write(90);
      delay(1000);
      servo2.write(180);
      delay(972);
      servo2.write(90);

    } else if (data == '2') {

      servo1.write(30);
      delay(1000);
      servo1.write(90);

    }
  }
  delay(1000);
}