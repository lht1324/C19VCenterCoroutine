package com.overeasy.c19vcentercoroutine.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.jakewharton.rxbinding4.view.clicks
import com.overeasy.c19vcentercoroutine.R
import com.overeasy.c19vcentercoroutine.databinding.DialogHelpBinding

// '도움말' 버튼을 터치했을 때 열리는 다이얼로그
class HelpDialog(private val mContext: Context) : Dialog(mContext) {
    private lateinit var binding: DialogHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setCancelable(true)

        init()
    }

    private fun init() {
        setCancelable(true)

        // textView2(마커), textView3(지도 초기화)를 클릭했을 때의 동작을 정의한다.
        binding.apply {
            textView2.clicks().subscribe {
                showAlertDialog(R.string.marker, R.string.markerHelp)
            }

            textView3.clicks().subscribe {
                showAlertDialog(R.string.initMap, R.string.initMapHelp)
            }
        }
    }

    private fun showAlertDialog(titleId: Int, messageId: Int) = AlertDialog.Builder(mContext, R.style.DialogTheme)
        .setTitle(titleId)
        .setMessage(messageId)
        .setNegativeButton(R.string.back) { dialog, _ ->
            dialog.dismiss()
        }
        .create()
        .show()
}