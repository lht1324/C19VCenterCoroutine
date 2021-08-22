package com.overeasy.c19vcentercoroutine.model.repository

import com.overeasy.c19vcentercoroutine.model.datasource.CenterData
import com.overeasy.c19vcentercoroutine.model.datasource.pojo.Centers
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response

// Repository의 인터페이스
interface Repository {
    suspend fun getCentersData(page: Int): Response<Centers>

    suspend fun getSavedCenterDatas(): List<CenterData>

    suspend fun insertAll(centerDatas: List<CenterData>)
}