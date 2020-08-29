package com.llt.dragon

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.llt.baselibrary.utils.StatusUtils
import com.tbruyelle.rxpermissions3.RxPermissions
import com.llt.baselibrary.base.BaseVmActivity
import com.llt.baselibrary.common.toast
import com.llt.baselibrary.utils.PrefUtils
import com.llt.dragon.constants.Constants
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


/**
 * 闪屏页面
 *
 */
class SplashActivity : BaseVmActivity() {

    private var disposable: Disposable? = null
    //https://github.com/tbruyelle/RxPermissions  可以对每个权限单独监听和处理
    val rxPermissions = RxPermissions(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        changeTheme()
        super.onCreate(savedInstanceState)
    }

    override fun init(savedInstanceState: Bundle?) {
        requestPermission()
    }

    /**
     * 申请权限
     */
    private fun requestPermission() {
        rxPermissions
            .request(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .subscribe { granted: Boolean ->
                if (granted) {
                    startIntent()
                } else {
                    toast("请开启相应权限")
                }
            }
    }

    /**
     * 开始倒计时跳转
     */
    private fun startIntent() {
        disposable = Observable.timer(2000, TimeUnit.MILLISECONDS)
            .subscribe {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }


    override fun getLayoutId() = R.layout.splash_layout

    /**
     * 动态切换主题
     */
    private fun changeTheme() {
        val theme = PrefUtils.getBoolean(Constants.SP_THEME_KEY, false)
        if (theme) {
            setTheme(R.style.AppTheme_Night)
        } else {
            setTheme(R.style.AppTheme)
        }
    }

    /**
     * 沉浸式状态,随主题改变
     */
    override fun setSystemInvadeBlack() {
        val theme = PrefUtils.getBoolean(Constants.SP_THEME_KEY, false)
        if (theme) {
            StatusUtils.setSystemStatus(this, true, false)
        } else {
            StatusUtils.setSystemStatus(this, true, true)
        }
    }
}
