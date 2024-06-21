package com.rkdrndyd.myapplication

import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

/*  매개변수
    MapList = 키-값 쌍의 리스트
    listener = 콜백 인터페이스(네트워크 요청 처리, Volly 라이브러리의 패턴 중 하나)
*/
    class AllForOneRequest(MapList: List<Pair<String, String>>, listener: Response.Listener<String>) :

/*  URL 밑에서 선언하지 않고 직접 설정
    (이유: 밑에서
    companion object {
        // 서버 URL 설정 ( PHP 파일 연동 )
        private const val URL = "http://3.38.227.45/"+URLEndPoint
    } 이렇게 companion object내에서 상수를 선언하는 경우, 컴파일 타임에 결정되는 값만 허용
    하지만 URLEndPoint는 런타임에 init 블록 내에서 초기화되서 사용 불가능

    "http://3.38.227.45/${MapList.lastOrNull()?.second ?: ""}"
    ${MapList.lastOrNull()?.second ?: ""} = MapList의 마지막의 second(=값)을 가져옴(없으면 널)
*/
    StringRequest(Method.POST,"http://3.38.227.45/${MapList.lastOrNull()?.second ?: ""}" , listener, null) {

    private val map: MutableMap<String, String> // private = 접근 제한자(클래스 내부에서만 쓸 수 있음)
    init { // init블록 = 클래스의 초기화 블록
        map = HashMap() // 빈 HashMap을 생성하여 map 프로퍼티에 할당
        val sublist = MapList.subList(0, MapList.size - 1) // 마지막 요소를 제외한 서브리스트 생성 (마지막 요소만 가져오기 val lastItem = MapList.last())
        for (param in sublist) {
            map[param.first] = param.second // 키-값 쌍은 map에 추가
        }
    }

    @Throws(AuthFailureError::class) // @Throws(AuthFailureError::class) = Kotlin에서 사용되는 어노테이션(annotation)
    override fun getParams(): Map<String, String>? {
        return map
    }
}
