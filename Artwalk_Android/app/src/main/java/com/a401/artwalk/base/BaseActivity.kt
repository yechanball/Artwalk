package com.a401.artwalk.base

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<T : ViewDataBinding>(private val layoutId: Int) : AppCompatActivity() {

    lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this

        val window = window
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        if(Build.VERSION.SDK_INT >= 30) {	// API 30 에 적용
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }

        val statusResourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = if(statusResourceId > 0) {resources.getDimensionPixelSize(statusResourceId)} else 0

        val navigationResourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val navigationHeight = if(navigationResourceId > 0) {resources.getDimensionPixelSize(navigationResourceId)} else 0

        binding.root.setPadding(
            0,
            statusBarHeight,
            0,
            navigationHeight
        )
    }
}