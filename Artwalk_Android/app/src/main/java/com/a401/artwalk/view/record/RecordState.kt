package com.a401.artwalk.view.record

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class RecordState : Parcelable {
    INITIALIZED,
    START,
    PAUSE,
    STOP,
    GET_STATUS,
    SET_ROUTE,
    GET_ROUTE
}