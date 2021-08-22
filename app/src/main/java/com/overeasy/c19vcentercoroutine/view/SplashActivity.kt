package com.overeasy.c19vcentercoroutine.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.overeasy.c19vcentercoroutine.R
import com.overeasy.c19vcentercoroutine.viewmodel.SplashViewModel

class SplashActivity : AppCompatActivity() {
    private val viewModel by lazy {
        ViewModelProvider(this, SplashViewModel.Factory(application)).get(SplashViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        /*
        SplashViewModel의 SingleLiveEvent downloadFinished의 데이터 변경을 관찰한다.
        데이터가 변경되면 MainActivity를 연 뒤 SplashActivity를 종료한다.
        */
        viewModel.getDownloadFinished().observe(this, {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        })
    }

    // 로그 확인용
    private fun println(data: String) = Log.d("SplashActivity", data)
}