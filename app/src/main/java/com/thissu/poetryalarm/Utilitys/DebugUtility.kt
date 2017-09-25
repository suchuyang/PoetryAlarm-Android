package com.chaopaikeji.poetryalert.Utility

import android.util.Log

/**
 * Created by apple on 2017/8/11.
 * 调试工具类，打印日志、缓存日志等都集成在这里
 */
class DebugUtility {

    companion object {

        fun NSLog(logString: String){

            Log.d("PoertyAlarm",logString)
        }

    }
}