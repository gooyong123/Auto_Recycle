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
    die("연결 실패: " . $conn->connect_error);
}

// 사용자가 제공한 데이터
$userID = $_POST['userID'];

// 쿼리 작성
$query = "SELECT history FROM history WHERE userID = '$userID' ORDER BY NUM DESC";

// 쿼리 실행
$result = $conn->query($query);

// 결과를 담을 배열 초기화
$response = array();

// 결과가 있는지 확인
if ($result->num_rows > 0) {
    $response["success"] = true;
    // 결과를 배열로 변환
    while ($row = $result->fetch_assoc()) {
        // 검색 결과를 JSON 형식으로 변환하여 itemList에 추가
        $item = array("history" => $row["history"]);
        $response["itemList"][] = $item;
    }
} else {
    // 결과 없음 메시지 설정
    $response["success"] = false;
}

// JSON 형식으로 출력
echo json_encode($response);

// 연결 종료
$conn->close();
?>
