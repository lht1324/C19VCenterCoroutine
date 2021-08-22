package com.overeasy.c19vcentercoroutine.viewmodel

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

// LiveData 사용 중 lifecycleOwner의 생명주기에 따라 중복 관찰되는 문제를 해결하기 위해 사용
class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers())
            println("Multiple observers registered but only one will be notified of changes.")

        super.observe(owner, {
            if (mPending.compareAndSet(true, false))
                observer.onChanged(it)
        })
    }

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    @MainThread
    fun call() {
        value = null
    }

    private fun println(data: String) = Log.d("SingleLiveEvent", data)
}