package com.overeasy.c19vcentercoroutine.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.overeasy.c19vcentercoroutine.model.datasource.CenterData
import com.overeasy.c19vcentercoroutine.model.repository.RepositoryImpl
import com.overeasy.c19vcentercoroutine.model.datasource.pojo.Center
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// 앱이 처음 실행될 때 SplashActivity 동작 중 API를 호출해 데이터를 받아 가공한 뒤 DB에 저장하는 뷰모델
class SplashViewModel(application: Application): ViewModel() {
    private val scope by lazy {
        CoroutineScope(Dispatchers.Main)
    }
    private val repository by lazy {
        RepositoryImpl(application)
    }
    private val downloadFinished = SingleLiveEvent<Void>()

    init {
        getAndProcessDatas()
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SplashViewModel(application) as T
        }
    }

    fun getDownloadFinished() = downloadFinished

    private fun getAndProcessDatas() = scope.run {
        val centerDatas = ArrayList<Center>()
        val resultList = ArrayList<CenterData>()

        launch(Dispatchers.IO) {
            for (i in 1..10) {
                centerDatas.addAll(repository.getCentersData(i).body()!!.data)
            }
        }

        launch(Dispatchers.Default) {
            centerDatas.forEach {
                resultList.add(centerToCenterData(it))
            }
        }

        launch(Dispatchers.IO) {
            insertAll(resultList)
        }

        launch(Dispatchers.Main) {
            downloadFinished.call()
        }
    }
    // 1부터 10페이지까지 10개씩 총 100개의 데이터를 호출해 가공한 뒤 DB에 저장한다.
    /* private fun getAndProcessDatas() = compositeDisposable.add(Observable.range(1, 10)
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())
        .map {
            repository.getCentersData(it)
        }
        .observeOn(Schedulers.computation())
        .mergeAll()
        // 이 시점부터 Response<Centers>를 여러 개 통지하는 하나의 Observable이 된다.
        .map {
            it.body()!!.data
        }.toList() // toList() 메서드로 ArrayList<Center>를 여러 개 가진 하나의 List로 묶어준다.
        .map {
            val resultList = ArrayList<Center>()

            it.forEach { centerList ->
                resultList.addAll(centerList)
            }

            resultList
        }
        .map {
            val resultList = ArrayList<CenterData>()

            // it이 가진 Center를 CenterData로 가공한 뒤 리스트로 만든다.
            it.forEach { center ->
                resultList.add(centerToCenterData(center))
            }

            resultList
        }
        .observeOn(Schedulers.io())
        .map {
            // DB에 리스트를 저장한다.
            insertAll(it.toList())
        }
        // LiveData.setValue() 메서드 사용을 위해 스레드를 AndroidSchedulers.mainThread()로 교체한다
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
                // 데이터 다운로드 후 DB에 저장하는 과정이 끝났다는 것을 downloadFinished.call() 메서드를 사용해 관찰자에게 알린다.
                downloadFinished.call()
            },
            {
                println("error in getAndProcessData() of SplashViewModel: ${it.message}")
            }
        )
    ) */

    // Center 객체를 매개 변수로 받아 CenterData 객체로 가공한 뒤 출력하는 메서드
    private fun centerToCenterData(center: Center) = CenterData().apply {
        lat = center.lat.toDouble()
        lng = center.lng.toDouble()
        centerType = center.centerType
        facilityName = center.facilityName

        address = center.address
        centerName = center.centerName
        org = center.org
        phoneNumber = if (center.phoneNumber.isNotEmpty()) center.phoneNumber else "없음"
        zipCode = center.zipCode
    }

    // DB에 리스트를 저장하는 메서드
    private suspend fun insertAll(centerDatas: List<CenterData>) = repository.insertAll(centerDatas)

    // 로그 확인용
    private fun println(data: String) = Log.d("SplashViewModel", data)
}