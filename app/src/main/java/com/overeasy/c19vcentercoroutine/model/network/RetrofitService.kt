package com.overeasy.c19vcentercoroutine.model.network

import com.overeasy.c19vcentercoroutine.model.datasource.pojo.Centers
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit의 인터페이스
interface RetrofitService {
    // https://api.odcloud.kr/api/15077586/v1/centers?page=1&perPage=10&serviceKey=bNmSjmL3NWL%2FmAmsQV0SyDT%2B8DCdZckhVg5%2FtSsmJHa47eBZBE%2BaFvCHYxeM1Dsz2FcgQ64elqYL3mr6GUyjOg%3D%3D
    // https://api.odcloud.kr/api/15077586/v1/centers?page={page}&perPage={perPage}&serviceKey={serviceKey}

    // serviceKey는 그대로 넣을 경우 '%2'가 '%252'로 바뀌는 등 오류가 발생하므로 encoded를 true로 설정한다.
    @GET("centers?")
    fun getCenters(
        @Query("page") page: Int,
        @Query("perPage") perPage: Int,
        @Query("serviceKey", encoded = true) serviceKey: String): Call<Centers>
}