<?php
// 데이터베이스 설정
$servername = "recycle-db.ct6mo2gmazns.ap-northeast-2.rds.amazonaws.com"; // 데이터베이스 서버 IP 또는 호스트 이름
$username = "admin";    // 데이터베이스 사용자 이름
$password = "yDptmeldml1!";    // 데이터베이스 비밀번호
$dbname = "user";          // 데이터베이스 이름

// 데이터베이스 연결 생성
$conn = new mysqli($servername, $username, $password, $dbname);

// 연결 확인
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
?>

