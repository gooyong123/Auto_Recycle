import socket
import serial
import threading
import time
import pymysql
import requests

# MySQL 서버 연결 설정
connection = pymysql.connect(
    host='recycle-db.ct6mo2gmazns.ap-northeast-2.rds.amazonaws.com',
    user='admin',
    password='Dptmeldml1!',
    database='user',
    charset='utf8'
)

# 시리얼 포트 설정
serial_port = '/dev/ttyUSB0'  # 아두이노와 연결된 시리얼 포트 (Windows의 경우 'COMX')

# 아두이노와 시리얼 통신 설정
arduino = serial.Serial(serial_port, 9600, timeout=1)

# 전역으로 누적 포인트와 이전 데이터를 저장할 변수 설정
total_points = 0
previous_data = ""
last_data_update_time = time.time() # 마지막으로 데이터가 갱신된 시간을 저장할 변수

reset_interval = 5 # 중복 데이터 초기화 주기 (초)

# 서버 설정
host = '0.0.0.0'  # 모든 IP 주소에서 들어오는 연결 허용
port = 12345  # 포트 번호 (임의로 선택)

# 소켓 생성
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# 소켓 바인딩
server_socket.bind((host, port))

# 클라이언트 연결 대기
server_socket.listen(1)
print('서버가 시작되었습니다.')


# 데이터를 처리하여 포인트 저장하는 함수
def process_data(data):
	global total_points, previous_data, last_data_update_time, reset_interval
	
	# 현재 시간을 가져옵니다.
	current_time = time.time()
	
	if current_time - last_data_update_time >= reset_interval:
		previous_data = ""
		last_data_update_time = current_time
	# 중복 데이터 확인
	if data != previous_data:
		# 중복되지 않는 경우에만 포인트 누적
		points = {
			'label_p': 10,
			'glass-bottle': 20,
			'can': 20,
			'non_label_p': 30
		}
		if data in points:
			point = points[data]
			print(f'{data}에 {point}점 지급됨')
			# 포인트 누적
			total_points += point
			print(f'누적 포인트: {total_points}')
		# 이전 데이터 업데이트
		previous_data = data

		last_data_update_time = current_time
		
# 클라이언트 요청을 처리하는 함수
def handle_client(client_socket):
	# 데이터 수신 및 출력
	while True:
		data = client_socket.recv(1024).decode()
		if not data:
			break
			
		# 데이터 처리 및 포인트 저장
		process_data(data)
		
		# 데이터에 따라 LED 제어
		if data == 'label_p':
			arduino.write(b'1')
		elif data =='glass-bottle':
			arduino.write(b'0')
		elif data =='non_label_p':
			arduino.write(b'1')
		elif data =='can':
			arduino.write(b'2')
	

	client_socket.close()
def send_data_to_ec2(arduino_data):
    ec2_url = 'http://3.38.227.45:8080/data'  # EC2 서버의 공용 IP 주소와 엔드포인트
    try:
        response = requests.post(ec2_url, json={'data': arduino_data})
        if response.status_code == 200:
            print('데이터가 EC2 서버에 성공적으로 전송되었습니다.')
        else:
            print(f'EC2 서버로 데이터 전송 실패: {response.status_code}')
    except Exception as e:
        print(f'EC2 서버로 데이터 전송 중 오류 발생: {e}')
        
def read_nfc_and_update():
	global total_points
	try:
		cursor = connection.cursor()
		while True:
			if arduino.in_waiting > 0:
				tag = arduino.readline().decode().strip()
				arduino_data = tag.replace(" ", "")
				print(arduino_data)
				if arduino_data:
					cursor.execute("SELECT point FROM PI WHERE ID = %s", (arduino_data,))
					result = cursor.fetchone()
					if result:
						# arduino_data exists, update the point
						cursor.execute("UPDATE PI SET point = %s WHERE ID = %s", (total_points, arduino_data))
						print(f"Updated ID: {arduino_data} with new points: {total_points}")
					else:
						 # arduino_data does not exist, insert new record
						 cursor.execute("INSERT INTO PI (ID, point) VALUES (%s, %s)", (arduino_data, total_points))
						 print(f"Inserted new record for ID: {arduino_data} with points: {total_points}")
						 
					connection.commit()
					
					total_points = 0
					
					# Send arduino_data to EC2 server
					send_data_to_ec2(arduino_data)
	except Exception as e:
		print(f"NFC 읽기 및 업데이트 중 오류 발생: {e}")
	finally:
		cursor.close()
	
# NFC 읽기 및 업데이트를 처리하는 스레드 시작
nfc_thread = threading.Thread(target=read_nfc_and_update)
nfc_thread.start()
while True:
	# 클라이언트 연결 수락
	client_socket, addr = server_socket.accept()
	print('클라이언트가 연결되었습니다:', addr)
	
	# 클라이언트를 처리하는 새로운 스레드 시작
	client_handler = threading.Thread(target=handle_client, args=(client_socket,))
	client_handler.start()
	

	
	

	
