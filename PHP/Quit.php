<?php
// MySQL 정보
$servername = "recycle-db.ct6mo2gmazns.ap-northeast-2.rds.amazonaws.com";
$username = "admin";
$password = "Dptmeldml1!";
$dbname = "user";

// 사용자가 제공한 데이터
$userID = $_POST['userID'];

// MySQL 데이터베이스에 연결
$conn = new mysqli($servername, $username, $password, $dbname);

// 연결 확인
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// 데이터를 삽입할 SQL 쿼리 작성 (prepared statement 사용)
$sql = $conn->prepare("DELETE FROM user WHERE userID = ?");
$sql->bind_param("s", $userID);

// 쿼리 실행
$sql->execute();
    $response["success"] = true;
    $response["message"] = "회원 탈퇴되었습니다.";
if ($sql->fetch()) {
    } else {
    $response["success"] = false;
    $response["message"] = "오류 발생";
}

echo json_encode($response);

// MySQL 연결 종료
$sql->close();
$conn->close();
?>
