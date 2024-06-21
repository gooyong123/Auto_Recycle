<?php
// MySQL 서버 정보
$servername = "recycle-db.ct6mo2gmazns.ap-northeast-2.rds.amazonaws.com";
$username = "admin";
$password = "Dptmeldml1!";
$dbname = "user";

// MySQL 데이터베이스에 연결
$conn = new mysqli($servername, $username, $password, $dbname);

// 연결 확인
if ($mysqli->connect_error) {
    die("연결 실패: " . $mysqli->connect_error);
}

//사용자가 제공한 데이터
$userID = $_POST['userID'];
$history = $_POST['history'];

// 쿼리 작성
$query = "INSERT INTO history(userID, history) VALUES ('$userID', '$history')";

// 쿼리 실행
if ($conn->query($query)) {
    $response["message"] = "sucess";
} else {;
    $response["message"] = "false";
}

// 연결 종료
$mysqli->close();
?>


