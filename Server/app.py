from flask import Flask, request, jsonify
import pymysql

app = Flask(__name__)

# MySQL 서버 연결 설정
db = pymysql.connect(
    host='recycle-db.ct6mo2gmazns.ap-northeast-2.rds.amazonaws.com',
    user='admin',
    password='Dptmeldml1!',
    database='user',
    charset='utf8'
)
arduino_data =None 

user_id = None

@app.route('/data', methods=['GET','POST'])
def receive_data():
    global arduino_data

    if request.method == 'GET':
        return jsonify(arduino_data), 200

    data = request.json
    if 'data' not in data:
        return 'No data provided', 400
    
    arduino_data = data['data']
    print(f"Received data from Arduino: {arduino_data}")
    
    return 'Data received', 200
    

@app.route('/user', methods=['GET', 'POST'])
def user():
    global arduino_data

    if request.method == 'POST':
        user_id = request.form.get('userID')
        option = request.form.get('option')  # 옵션 값 받기

        if user_id and option:  # 사용자 ID와 옵션 값이 모두 존재하는지 확인
            try:
                with db.cursor() as cursor:
                    # 사용자 ID를 통해 현재 사용자의 point 값을 가져옴
                    sql_user = "SELECT point FROM user WHERE userID = %s"
                    cursor.execute(sql_user, (user_id,))
                    result_user = cursor.fetchone()

                    if result_user:
                        print(f"User's point retrieved from database: {result_user[0]}")
                        # Arduino 데이터를 통해 PI 테이블에서 해당 ID의 point 값을 가져옴
                        sql_pi = "SELECT point FROM PI WHERE ID = %s"
                        cursor.execute(sql_pi, (arduino_data,))
                        result_pi = cursor.fetchone()

                        if result_pi:
                            print(f"PI's point retrieved from database: {result_pi[0]}")
                            # 옵션 값과 Arduino 데이터가 일치할 때 포인트를 지급
                            if option == arduino_data:
                                # 사용자의 point 값에 PI 테이블의 point 값을 합산하여 업데이트
                                add_point = result_pi[0]
                                total_point = result_user[0] + result_pi[0]
                                print(f"Add point calculated: {add_point}")
                                print(f"Total point calculated: {total_point}")
                                sql_update_user = "UPDATE user SET point = %s WHERE userID = %s"
                                cursor.execute(sql_update_user, (total_point, user_id,))
                                sql_update_user = "UPDATE user SET getPoint = %s WHERE userID = %s"
                                cursor.execute(sql_update_user, (add_point, user_id,))

                                db.commit()

                                # PI 테이블의 point 값을 0으로 설정
                                sql_zero_pi = "UPDATE PI SET point = 0 WHERE ID = %s"
                                cursor.execute(sql_zero_pi, (arduino_data,))
                                db.commit()

                                return jsonify({"success": True, "message": "Point updated successfully.", "total_point": total_point , "add_point": add_point}), 200
                            else:
                                return jsonify({"success": False, "message": "Option does not match Arduino data."}), 400
                        else:
                            return jsonify({"success": False, "message": "Arduino data not found in the PI table.", "sql_pi": sql_pi}), 4045
                        
                    else:
                        return jsonify({"success": False, "message": "User ID not found in the database."}), 404
            except Exception as e:
                return jsonify({"success": False, "message": "Failed to update point.", "error": str(e)}), 500
        else:
            return jsonify({"success": False, "message": "User ID or option not provided."}), 400

    else:
        return jsonify({"success": False, "message": "Invalid request method."}), 405


        
if __name__ == '__main__':
    app.run(host='0.0.0.0',debug = True, port=8080)

