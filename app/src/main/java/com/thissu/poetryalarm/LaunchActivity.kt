package com.thissu.poetryalarm

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.thissu.poetryalarm.ActivityModule.AlarmListActivity
import com.thissu.poetryalarm.Data.DataManager
import android.content.IntentFilter
import com.thissu.poetryalarm.Utilitys.HomeKeyEventBroadCastReceiver


class LaunchActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        DataManager.instance.applicationContext = applicationContext
        DataManager.instance.initAlarmData()


        Handler().postDelayed({

            val intent = Intent()
            intent.setClass(this@LaunchActivity,AlarmListActivity::class.java)
            startActivity(intent)
            finish()

        },2000)

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //or
//                    View.SYSTEM_UI_FLAG_FULLSCREEN or//
//                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY//

            window.setStatusBarColor(Color.parseColor("#e0000000"));
        }
    }
}
