# 경동대학교 컴퓨터 공학과 4학년 1학기 캡스톤 프로젝트

## 프로젝트명
- **Auto Recycle**(자동 분리수거 장치)

### 1. 프로젝트의 개요
#### 1-1. 프로젝트 배경
현재 대한민국 길거리에는 무분별하게 버려져있는 쓰레기들을 적지 않게 볼 수 있다.   
그에 대한 대응책으로는 길거리에 배치된 공공 쓰레기통을 설치하였다.   
하지만 이러한 길거리 쓰레기통은 재활용품에 대한 분류 작업이 없기 때문에 수거물 폐기 시 재분류를 해야하는 불편함이 있다.   
이번 작품을 통해 재분류를 통한 불편함을 줄이고 시민들에게 분리수거에 대한 인식을 재정립 하도록 하였다.   
#### 1-2. 프로젝트 목표 및 주요 기능
최종 목표: 재활용품에 대한 자동 분류와 시민들의 분리수거 인식을 개선   

기능   
1. 재활용품(캔, 유리, 투명 페트병)에 대한 자동 분류
2. 재활용품 투입에 대한 포인트 적립   
3. 투명 페트병에 대한 라벨 제거 유무 확인 후 포인트 적립에 차이를 두어 분리수거 인식 개선
### 2. 구현 환경 및 개발 언어
#### 2-1. 구현 환경
![image](https://github.com/gooyong123/Auto_Recycle/assets/143383060/f01fae38-6cb2-4d35-bf64-3db3b9106173)
- 쓰레기 투입
  1. 쓰레기를 투입할 시 라즈베리파이를 통해 실시간으로 스트리밍하던 영상(mjpeg를 사용)과 PC를 연결
  2. PC에선 영상과 함께 YOLO(딥러닝 기반 객체 탐지 모델)를 사용하여 쓰레기 탐지 후 탐지된 값을 TCP 통신을 통하여 라즈베리파이에 전송
  3. 탐지된 값을 라즈베리파이는 시리얼 통신을 통해 아두이노에 전송
  4. 아두이노는 탐지된 값을 토대로 장치를 작동

- NFC 태그(포인트 적립)
  1. 태그를 하기 전 클라우드 서버에서 아이디 매칭을 위해 장치명(임의 값)과 유저 아이디를 전송
  2. 핸드폰으로 태그를 한 후 아두이노는 시리얼 통신을 통해 라즈베리파이에 장치명을 전송
  3. 라즈베리파이는 누적된 포인트와 장치명을 클라우드 서버에 배포된 RDS에 전송   
      3-1. 라즈베리파이 데이터베이스 테이블을 생성 후 임의로 포인트를 전송
  4. 클라우드 서버에서 앱에서 보낸 장치명과 라즈베리파이에서 받은 장치명이 일치할 시 누적 포인트를 유저에게 포인트를 전송   
      4-1. 유저 테이블에 있던 포인트 컬럼에 라즈베리파이 누적 포인트를 전송   
#### 2-2. 개발 언어
**HardWare**&nbsp; <img src="https://img.shields.io/badge/Raspberry Pi-A22846?style=for-the-badge&logo=Raspberry Pi&logoColor=white"> &nbsp;<img src="https://img.shields.io/badge/Arduino-00878F?style=for-the-badge&logo=Arduino&logoColor=white">   
**Cloud Server** <img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">&nbsp;<img src="https://img.shields.io/badge/amazonrds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white">&nbsp;<img src="https://img.shields.io/badge/flask-000000?style=for-the-badge&logo=flask&logoColor=white">                
**Database**&nbsp;<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">     
**개발 언어** &nbsp;<img src="https://img.shields.io/badge/python-3776AB?style=for-the-badge&logo=python&logoColor=white">&nbsp;<img src="https://img.shields.io/badge/kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white">&nbsp;<img src="https://img.shields.io/badge/php-777BB4?style=for-the-badge&logo=php&logoColor=white">         
**인공지능**  &nbsp;<a href="https://github.com/ultralytics/yolov5/actions/workflows/ci-testing.yml"><img src="https://github.com/ultralytics/yolov5/actions/workflows/ci-testing.yml/badge.svg" alt="YOLOv5 CI"></a>
 
### 3. 작품 구현
#### 3-1. 장치 사진(작품 시연 ppt 사용)   
![image](https://github.com/gooyong123/Auto_Recycle/assets/143383060/d27af257-f561-4c91-a5b4-62280a5762cb)

![image](https://github.com/gooyong123/Auto_Recycle/assets/143383060/3d7e4e3e-c207-4b77-b246-fb0862c46c91)   
![image](https://github.com/gooyong123/Auto_Recycle/assets/143383060/5362d250-d6ee-4301-9ca8-174976b74d17)   
![image](https://github.com/gooyong123/Auto_Recycle/assets/143383060/388fe7a0-2bce-4bc7-87f9-83cbaeda8abc)   
![image](https://github.com/gooyong123/Auto_Recycle/assets/143383060/9f0111ce-25ed-4f59-9da0-e2c9e8f6ecf0)   
![image](https://github.com/gooyong123/Auto_Recycle/assets/143383060/c4a7a9a7-850d-49a6-9c03-5fb236d10687)


![GIFMaker_me (1)](https://github.com/gooyong123/Auto_Recycle/assets/143383060/e4149b44-32fe-4cb0-a121-1bc19a6bf8a9)



#### 3-2. YOLO 모델 학습 진행 및 학습 결과
데이터셋 라벨링 작업은 Roboflow를 사용하였고 클래스는 총 5개로 구성(glass-bottle, can, non_label_p, label_p, alcohol_bottle)   
![image](https://github.com/gooyong123/Auto_Recycle/assets/143383060/a3ca2350-b9d6-4f34-a135-9990b28baa89)   

총 3248개의 이미지를 사용하였고 데이터 증강을 통해 8730개의 이미지로 구성된 데이터셋을 생성   
![image](https://github.com/gooyong123/Auto_Recycle/assets/143383060/f7610c7b-c3d1-4794-826b-f9c6136105aa)

Yolov5를 사용하였고 아래에 코드를 사용해 모델 학습을 진행      
**python train.py --epochs 150 --batch 16 --data data.yaml --weights yolov5s.pt**   

confusion_matrix를 보면 클래스 예측 값이 0.9 이상을 이루고 있다.
![confusion_matrix](https://github.com/gooyong123/Auto_Recycle/assets/143383060/56420e63-fa7b-448c-927b-69e92dfd9a5a)   

결과 그래프를 보면 loss값은 안정된 값을 보이며 감소하는 것을 볼 수 있고 mAP 치수는 0.9 이상을 보여준다.
![results](https://github.com/gooyong123/Auto_Recycle/assets/143383060/049dffe9-a692-4178-8a75-f6f98a6bb482)
### 4. 프로젝트 기간   
2024.03 - 2024.06.11
### 5. 팀원 및 맡은 역할
팀장: 강구용 - 라즈베리 파이 관리 및 클라우드 서버 구현, AI 생성 및 데이터셋 라벨링 작업    
팀원: 안성식 - 아두이노 코드 구현 및 장치 설계, 앱 일부 구현, 데이터셋 라벨링 작업   
팀원: 장규석 - 앱 구현, 데이터셋 라벨링 작업
