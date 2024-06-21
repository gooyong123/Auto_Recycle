<?php
// MySQL 서버 정보
$servername = "recycle-db.ct6mo2gmazns.ap-northeast-2.rds.amazonaws.com";
$username = "admin";
$password = "Dptmeldml1!";
$dbname = "user";

// MySQL 데이터베이스에 연결
$conn = new mysqli($servername, $username, $password, $dbname);

// 연결 확인
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

//사용자가 제공한 데이터
$userID = $_POST['userID'];

// 사용자 정보 가져오기 (비밀번호 해시 포함)
$statement = mysqli_prepare($conn, "SELECT point FROM user WHERE userID = ?");
mysqli_stmt_bind_param($statement, "s", $userID);
mysqli_stmt_execute($statement);
mysqli_stmt_store_result($statement);
mysqli_stmt_bind_result($statement, $DBPoint);
mysqli_stmt_fetch($statement); // 결과 가져오기

$response = array();

if(mysqli_stmt_num_rows($statement) > 0) {
    $response["success"] = true;
    $response["message"] = "포인트가 갱신되었습니다.";
    $response["point"] = $DBPoint;
} else {
    $response["success"] = false;
    $response["message"] = "사용자를 찾을 수 없습니다.";
}

// JSON 응답 헤더 설정
header('Content-Type: application/json');

// JSON 응답 출력
echo json_encode($response);

// MySQL 연결 종료
$conn->close();
?>
