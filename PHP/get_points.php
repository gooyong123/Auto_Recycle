<?php
include 'db_config.php';

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $userID = $_POST['userID'];

    // 데이터베이스 연결
    $conn = new mysqli($servername, $username, $password, $dbname);

    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    // 포인트 조회 SQL 쿼리 실행
    $sql = "SELECT point FROM users WHERE userID = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $userID);
    $stmt->execute();
    $stmt->bind_result($point);
    $stmt->fetch();

    $response = array('points' => $point);
    echo json_encode($response);

    $stmt->close();
    $conn->close();
}
?>
