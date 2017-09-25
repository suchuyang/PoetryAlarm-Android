package com.thissu.poetryalarm.Utilitys

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import com.chaopaikeji.poetryalert.Utility.DebugUtility


/**
 * Created by apple on 2017/9/8.
 */
internal class HomeKeyEventBroadCastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
            val reason = intent.getStringExtra(SYSTEM_REASON)
            if (reason != null) {
                if (reason == SYSTEM_HOME_KEY) {
                    // home key处理点

                    DebugUtility.NSLog("home key pressed")

                } else if (reason == SYSTEM_RECENT_APPS) {
                    // long home key处理点
                }
            }
        }
    }

    companion object {

        val SYSTEM_REASON = "reason"
        val SYSTEM_HOME_KEY = "homekey"//home key
        val SYSTEM_RECENT_APPS = "recentapps"//long home key
    }
}