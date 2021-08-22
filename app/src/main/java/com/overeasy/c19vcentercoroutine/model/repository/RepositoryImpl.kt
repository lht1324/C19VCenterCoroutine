package com.overeasy.c19vcentercoroutine.model.repository

import android.app.Application
import com.overeasy.c19vcentercoroutine.model.datasource.CenterData
import com.overeasy.c19vcentercoroutine.model.network.Client
import com.overeasy.c19vcentercoroutine.model.datasource.VCenterDatabase

// Repository 인터페이스의 구현 객체.
class RepositoryImpl(private val application: Application): Repository {
    private val client by lazy {
        Client()
    }
    private val vCenterDao by lazy {
        VCenterDatabase.getInstance(application)!!.vCenterDao()
    }

    // client에서 API를 호출해 데이터를 받아온다.
    override fun getCentersData(page: Int) = client.getCentersData(page)

    // Room DB에 저장된 CenterData 전체를 Single<List<CenterData>>의 형태로 받아온다.
    override fun getSavedCenterDatas() = vCenterDao.getCenterDatas()

    // Room DB에 CenterData의 리스트를 저장할 때 사용된다.
    override fun insertAll(centerDatas: List<CenterData>) = vCenterDao.insertAll(centerDatas)
}