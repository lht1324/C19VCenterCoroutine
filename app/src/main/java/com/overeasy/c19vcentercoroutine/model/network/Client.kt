package com.overeasy.c19vcentercoroutine.model.network

import android.util.Log
import com.overeasy.c19vcentercoroutine.model.datasource.pojo.Centers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

// 네트워크 통신을 담당하는 객체
class Client {
    private val baseUrl = "https://api.odcloud.kr/api/15077586/v1/"
    private val serviceKey = "bNmSjmL3NWL%2FmAmsQV0SyDT%2B8DCdZckhVg5%2FtSsmJHa47eBZBE%2BaFvCHYxeM1Dsz2FcgQ64elqYL3mr6GUyjOg%3D%3D"

    // 통신 도중 발생하는 오류를 확인하기 위해 사용한다.
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }).build()

    // 한 페이지 당 10개씩 가져와야 하니 perPage엔 10을 넣고, serviceKey도 그대로 넣어준다.
    fun getCentersData(page: Int): Response<Centers> = getService().getCenters(page, 10, serviceKey).execute()

    // 레트로핏 인터페이스를 생성한다.
    private fun getService(): RetrofitService = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory((RxJava2CallAdapterFactory.create())) // RxKotlin의 Observable 형태로 데이터를 받아오기 위해 추가한다.
        .addConverterFactory(GsonConverterFactory.create()) // 데이터의 파싱을 위해 추가한다.
        .client(client)
        .build()
        .create(RetrofitService::class.java)

    // 로그 확인용
    private fun println(data: String) = Log.d("Client", data)
}