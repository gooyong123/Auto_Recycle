<?php
// MySQL 서버 정보
$servername = "recycle-db.ct6mo2gmazns.ap-northeast-2.rds.amazonaws.com";
$username = "admin";
$password = "Dptmeldml1!";
$dbname = "user";

// 사용자가 제공한 데이터
$userID = $_POST['userID'];
$userPassword = $_POST['userPassword'];
$userSalt = $_POST['userSalt'];
$userName = $_POST['userName'];
$userAge = $_POST['userAge'];

// MySQL 데이터베이스에 연결
$conn = new mysqli($servername, $username, $password, $dbname);

// 연결 확인
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// 데이터를 삽입할 SQL 쿼리 작성
$sql = "INSERT INTO user (userID, userPassword, userSalt,  userName, userAge) VALUES ('$userID', '$userPassword', '$userSalt', '$userName', '$userAge')";

// 쿼리 실행 여부 확인
// 데이터를 삽입할 SQL 쿼리 작성
if ($conn->query($sql) === TRUE) {
    $response = array();
    $response["success"] = true;
    $response["message"] = "새 레코드가 성공적으로 생성되었습니다.";
    echo json_encode($response);
} else {
    $response = array();
    $response["success"] = false;
    $response["message"] = "레코드 생성 중 오류가 발생했습니다.";
    echo json_encode($response);
}

// MySQL 연결 종료
$conn->close();
?>
