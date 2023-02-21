package com.a401.artwalk.base

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.a401.artwalk.R
import com.a401.artwalk.utills.LocationPermissionHelper
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.PuckBearingSource
import com.mapbox.maps.plugin.compass.CompassView
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.locationcomponent.location2
import com.mapbox.maps.plugin.scalebar.scalebar
import java.lang.ref.WeakReference


abstract class UsingMapFragment<T: ViewDataBinding>(layoutId: Int): BaseFragment<T>(layoutId) {

    lateinit var mapView: MapView
    lateinit var locationPermissionHelper: LocationPermissionHelper

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
    }
    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                0
            )
        } else {
            onMapReady()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    private fun onMapReady() {
        mapView.scalebar.enabled = false
        mapView.compass.updateSettings {
            enabled = false
        }
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            initLocationComponent()
        }
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location2
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                topImage = AppCompatResources.getDrawable(
                    requireActivity(),
                    R.drawable.logo_rotate
                ),
            )
        }
        setupGesturesListener()
        onCameraTracking()
        headerTo()
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    open fun onCameraTrackingDismissed() {
        mapView.location2.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location2.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    open fun onCameraTracking() {
        mapView.location2.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location2.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun headerTo() {
        mapView.location2.updateSettings2 {
            puckBearingSource = PuckBearingSource.HEADING
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onMapReady()
                } else {
                    val builder = AlertDialog.Builder(context)

                    builder.setTitle("원활한 어플 사용을 위해서는 위치 권한 허용이 필요합니다")
                        .setPositiveButton("확인",
                            DialogInterface.OnClickListener { dialog, id ->
                            }
                        )
                    builder.show()
                }
                return
            }
            else -> {
                // 다른 권한 요청에 대한 결과 처리
            }
        }
    }
}