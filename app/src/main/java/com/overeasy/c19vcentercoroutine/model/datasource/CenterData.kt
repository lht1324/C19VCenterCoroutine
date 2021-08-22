package com.overeasy.c19vcentercoroutine.model.datasource

import androidx.room.Entity
import androidx.room.Ignore
import com.naver.maps.map.overlay.Marker

// API를 호출해 받아온 데이터를 가공해 저장하는 객체. Room의 Entity로 사용된다.
@Entity(tableName = "centerDataTable", primaryKeys = ["centerType", "facilityName"])
class CenterData {
    var lat = 0.0 // 위도
    var lng = 0.0 // 경도
    var centerType = "" // 센터 구분
    var facilityName = "" // 시설명

    var address = "" // 주소
    var centerName = "" // 센터명
    var org = "" // 관할 기관
    var phoneNumber = "" // 전화번호
    var zipCode = "" // 우편번호

    // 마커 객체. DB에 저장되지 않는다.
    @Ignore
    var marker: Marker? = null
}