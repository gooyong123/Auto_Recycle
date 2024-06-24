<?php
// MySQL 정보
$servername = "recycle-db.ct6mo2gmazns.ap-northeast-2.rds.amazonaws.com";
$username = "admin";
$password = "Dptmeldml1!";
$dbname = "user";

// 사용자가 제공한 데이터
$userID = $_POST['userID'];
$userPW = $_POST['userPW'];

// MySQL 데이터베이스에 연결
$conn = new mysqli($servername, $username, $password, $dbname);

// 연결 확인
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// 데이터를 삽입할 SQL 쿼리 작성 (prepared statement 사용)
$sql = $conn->prepare("SELECT userPassword, userSalt, userName, userAge, uid, point FROM user WHERE userID = ?");
$sql->bind_param("s", $userID);

// 쿼리 실행
$sql->execute();

// 결과 바인딩
$sql->bind_result($hashedPW, $salt, $userName, $userAge, $uid, $point);

// 결과 가져오기
$response = array();

if ($sql->fetch()) {
    $inputHash = hash('sha256', $userPW . $salt); // 입력된 비밀번호와 데이터베이스에서 가져온 salt를 사용하여 해시 생성

    // 비밀번호 해시 비교
    if (hash_equals($hashedPW, $inputHash)) {
        $response["success"] = true;
        $response["userID"] = $userID;
        $response["userName"] = $userName;
        $response["userAge"] = $userAge;
        $response["uid"] = $uid;
        $response["point"] = $point;
    } else {
        $response["success"] = false;
        $response["message"] = "아이디 또는 비밀번호가 맞지 않습니다.";
    }
} else {
    $response["success"] = false;
    $response["message"] = "아이디 또는 비밀번호가 맞지 않습니다.";
}

echo json_encode($response);

// MySQL 연결 종료
$sql->close();
$conn->close();
?>
