package com.overeasy.c19vcentercoroutine.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding4.view.clicks
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.overeasy.c19vcentercoroutine.R
import com.overeasy.c19vcentercoroutine.databinding.ActivityMainBinding
import com.overeasy.c19vcentercoroutine.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding
    private var backPressedLast: Long = 0
    private val mapFragment by lazy {
        MapFragment.newInstance(
            NaverMapOptions()
                .camera(CameraPosition(LatLng(36.0, 127.83), 5.7)) // 앱의 첫 실행시 시점을 남한 전체로 고정
                .mapType(NaverMap.MapType.Navi) // 지도의 유형 설정
                .zoomControlEnabled(false) // 줌 버튼의 위치 변경을 위해 기본 줌 버튼을 비활성화한다.
        ).also {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.mapFragment, it as Fragment)
                .commit()
        }
    }
    private val viewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(application)).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 비동기로 NaverMap 객체를 얻는다. NaverMap 객체가 준비되면 onMapReady() 메서드가 호출된다.
        mapFragment.getMapAsync(this)

        // '도움말' 버튼이 클릭되면 HelpDialog를 연다.
        binding.helpButton.clicks().subscribe {
            HelpDialog(this@MainActivity).show()
        }
    }

    // 뒤로 가기 버튼을 2초 안에 1번 더 눌러야 종료되게 하는 메서드
    override fun onBackPressed() {
        if (System.currentTimeMillis() - backPressedLast < 2000) {
            finish()
            return
        }

        showShortToast("종료하려면 뒤로 가기 버튼을\n한 번 더 눌러주세요.")

        backPressedLast = System.currentTimeMillis()
    }

    // mapFragment.getMapAsync() 메서드 실행 후 NaverMap 객체가 준비되면 호출된다.
    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        binding.apply {
            // 임의로 지정한 줌 버튼의 map 속성에 naverMap을 삽입한다.
            zoom.map = naverMap

            // 초기화 버튼을 누르면 앱을 처음 시작했을 때 나온 시점으로 이동한다.
            initButton.clicks().subscribe {
                naverMap.moveCamera(
                    CameraUpdate
                        .toCameraPosition(CameraPosition(LatLng(36.0, 127.83), 5.7))
                        .animate(CameraAnimation.Easing)
                )
            }
        }
        // 보이는 부분을 한반도로 고정한다
        naverMap.extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0))

        /*
        MainViewModel의 SingleLiveEvent centerDatas의 데이터 변경 여부를 관찰한다.
        데이터가 변경되면 centerData.marker의 OnClickListener를 설정하고 map 속성을 지정한다.
        */
        viewModel.getCenterDatas().observe(this, {
            val iterator = it.iterator()

            while(iterator.hasNext()) {
                val centerData = iterator.next()

                with(centerData) {
                    marker!!.setOnClickListener {

                        showLongToast("센터명: $centerName\n" +
                                "센터 구분: $centerType\n" +
                                (if(org.isEmpty()) "" else "관할 기관: $org\n") +
                                "시설명: $facilityName\n" +
                                "주소: $address ($zipCode)\n" +
                                "전화번호: $phoneNumber")

                        true
                    }

                    marker!!.map = naverMap
                }
            }
        })
    }

    // 로그 확인용
    private fun println(data: String) = Log.d("MainActivity", data)

    // Toast 출력용
    private fun showShortToast(data: String) = Toast.makeText(this, data, Toast.LENGTH_SHORT).show()

    private fun showLongToast(data: String) = Toast.makeText(this, data, Toast.LENGTH_LONG).show()
}