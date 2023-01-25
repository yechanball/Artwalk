package com.a401.artwalk.view.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _durationHour: MutableLiveData<Int> = MutableLiveData(1)
    val durationHour: LiveData<Int> = _durationHour

    private val _durationMinute: MutableLiveData<Int> = MutableLiveData(2)
    val durationMinute: LiveData<Int> = _durationMinute

    private val _durationSecond: MutableLiveData<Int> = MutableLiveData(3)
    val durationSecond: LiveData<Int> = _durationSecond

    private val _distance: MutableLiveData<Float> = MutableLiveData(0f)
    val distance: LiveData<Float> = _distance

    private val _startButtonEvent: MutableLiveData<Unit> = MutableLiveData()
    val startButtonEvent: LiveData<Unit> = _startButtonEvent

    fun onClickStartButton(){
        _startButtonEvent.value = Unit

    }

    fun testclick(){
        Log.e("test", "pressed")
    }
}
