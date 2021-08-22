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
import kotlinx.coroutines.*

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
        scope.launch(Dispatchers.Default) {
            processSavedData()
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(application) as T
        }
    }

    fun getCenterDatas() = centerDatas

    // DB에서 저장된 CenterData의 리스트를 가져와 기본적인 속성을 설정한 marker를 추가하고 SingleLiveEvent centerDatas의 value에 넣어준다.
    private suspend fun processSavedData() = scope.run {
        var centerDataList = ArrayList<CenterData>()

        // 시작하는 순서는 같지만 실행되는 건 제각각
        // 하나가 끝나면 하나를 실행하는 방식?
        // 순차적 비동기 실행
        launch(Dispatchers.IO) {
            centerDataList = ArrayList(repository.getSavedCenterDatas())
        }.join()

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
        }.join()

        launch(Dispatchers.Main) {
            centerDatas.value = ArrayList(centerDataList)
        }
    }

    // 로그 확인용
    private fun println(data: String) = Log.d("MainViewModel", data)
}