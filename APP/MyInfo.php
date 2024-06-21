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

// 사용자 솔트 가져오기
$statement = mysqli_prepare($conn, "SELECT userSalt FROM user WHERE userID = ?");
mysqli_stmt_bind_param($statement, "s", $userID);
mysqli_stmt_execute($statement);
mysqli_stmt_store_result($statement);
mysqli_stmt_bind_result($statement, $salt);

$response = array();
$response["success"] = false;

// 사용자가 제공한 데이터
$userID = $_POST['userID'];
$userPW = $_POST['userPW'];
$userName = $_POST['userName'];
$userAge = $_POST['userAge'];

// 비밀번호를 변경하는 경우에만 새로운 해시된 비밀번호 생성
$passwordUpdateClause = "";
if (!empty($userPW)) {
    if (mysqli_stmt_fetch($statement)) {
        // 입력된 비밀번호와 데이터베이스에서 가져온 salt를 사용하여 해시 생성
        $inputHash = hash('sha256', $userPW . $salt);
        $passwordUpdateClause = "userPassword = '$inputHash',";
    }
}

// 데이터를 삽입할 SQL 쿼리 작성
$sql = "UPDATE user SET $passwordUpdateClause userName ='$userName', userAge ='$userAge' WHERE userID = '$userID'";

// 쿼리 실행 여부 확인
if ($conn->query($sql) === TRUE) {
   //응답
    $response["success"] = true;
    $response["message"] = "회원 정보가 정상적으로 변경되었습니다.";
    $response["userID"] = $userID;
    $response["userName"] = $userName;
    $response["userAge"] = $userAge;
    $response["userPW"] = $userPW;
    echo json_encode($response);
} else {
    $response["message"] = "회원 정보 변경에 오류가 발생했습니다.";
    echo json_encode($response);
}

// MySQL 연결 종료
$conn->close();
?>

