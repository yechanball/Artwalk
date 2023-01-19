package com.a401.artwalk.view.route.draw

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RouteDrawViewModel : ViewModel() {

    private val _durationHour: MutableLiveData<Int> = MutableLiveData(1)
    val durationHour: LiveData<Int> = _durationHour

    private val _durationMinute: MutableLiveData<Int> = MutableLiveData(2)
    val durationMinute: LiveData<Int> = _durationMinute

    private val _durationSecond: MutableLiveData<Int> = MutableLiveData(3)
    val durationSecond: LiveData<Int> = _durationSecond

    private val _distance: MutableLiveData<Float> = MutableLiveData(0f)
    val distance: LiveData<Float> = _distance

    private val _drawButtonEvent: MutableLiveData<Unit> = MutableLiveData()
    val drawButtonEvent: LiveData<Unit> = _drawButtonEvent

    fun onClickDrawButton() {
        // TODO: 그리기 토글 버튼 클릭시 event
        _drawButtonEvent.value = Unit
    }

    fun onClickUndoButton() {
        // TODO: 뒤로가기 버튼 클릭시 event
        Log.d("test", "undo button clicked")
    }

}