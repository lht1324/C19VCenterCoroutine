package com.overeasy.c19vcentercoroutine.viewmodel

import android.app.Application
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import com.overeasy.c19vcentercoroutine.model.datasource.CenterData
import com.overeasy.c19vcentercoroutine.model.repository.RepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// SplashViewModel에서 저장된 데이터를 불러와 가공하는 뷰모델
class MainViewModel(application: Application) : ViewModel() {
    private val scope by lazy {
        CoroutineScope(Dispatchers.Main)
    }
    private val repository by lazy {
        RepositoryImpl(application)
    }

    private val centerDatas = SingleLiveEvent<ArrayList<CenterData>>()

    init {
        processSavedData()
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(application) as T
        }
    }

    fun getCenterDatas() = centerDatas

    private fun processSavedData() = scope.run {
        lateinit var centerDataList: List<CenterData>

        launch(Dispatchers.IO) {
            centerDataList = repository.getSavedCenterDatas()
        }

        launch(Dispatchers.Default) {
            for (i in centerDataList.indices) {
                val centerData = centerDataList[i]

                centerDataList[i].marker = Marker().apply {
                    width = Marker.SIZE_AUTO
                    height = Marker.SIZE_AUTO
                    position = LatLng(centerData.lat, centerData.lng)
                    captionText = centerData.facilityName
                    icon = MarkerIcons.BLACK
                    iconTintColor = Color.parseColor(if (centerData.centerType == "지역") "#4AE18E" else "#008000")
                    captionMinZoom = 8.4
                    zIndex = if (centerData.centerType == "지역") 0 else 100
                    isHideCollidedSymbols = true
                    isHideCollidedCaptions = true
                }
            }
        }

        launch(Dispatchers.Main) {
            centerDatas.value = ArrayList(centerDataList)
        }
    }
    // DB에서 저장된 CenterData의 리스트를 가져와 기본적인 속성을 설정한 marker를 추가하고 SingleLiveEvent centerDatas의 value에 넣어준다.
    /* private fun processSavedData() = compositeDisposable.add(repository.getSavedCenterDatas()
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.computation())
        .map {
            for (i in it.indices) {
                val centerData = it[i]

                // 각 centerData의 Marker를 생성, map은 뷰에 가까운 객체이니 MainActivity에서 centerDatas의 변경이 관찰된 뒤 넣어준다.
                it[i].marker = Marker().apply {
                    width = Marker.SIZE_AUTO
                    height = Marker.SIZE_AUTO
                    position = LatLng(centerData.lat, centerData.lng)
                    captionText = centerData.facilityName
                    icon = MarkerIcons.BLACK
                    iconTintColor = Color.parseColor(if (centerData.centerType == "지역") "#4AE18E" else "#008000")
                    captionMinZoom = 8.4
                    zIndex = if (centerData.centerType == "지역") 0 else 100
                    isHideCollidedSymbols = true
                    isHideCollidedCaptions = true
                }
            }

            ArrayList(it)
        }
        // LiveData.setValue() 메서드 사용을 위해 스레드를 AndroidSchedulers.mainThread()로 교체한다
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            {
                centerDatas.value = it
            },
            {
                println("error in processSavedData() of MainViewModel: ${it.message}")
            }
        )
    ) */

    // 로그 확인용
    private fun println(data: String) = Log.d("MainViewModel", data)
}