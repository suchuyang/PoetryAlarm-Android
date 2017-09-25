package com.thissu.poetryalarm.ActivityModule

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.thissu.poetryalarm.Data.AlarmModel
import com.thissu.poetryalarm.Data.DataManager

import com.thissu.poetryalarm.R
import java.util.*

class AlarmListActivity : Activity() {

    val navigationTitle: TextView by lazy {
        findViewById(R.id.navigationTitleText) as TextView
    }//标题

    val navigationLeftButton : Button by lazy{
        findViewById(R.id.navigationLeftButton) as Button
    }

    val navigationAddButton: Button by lazy {
        findViewById(R.id.navigationAddButton) as Button
    }//

    val alarmlistView:ListView by lazy {
        findViewById(R.id.alarmList) as ListView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_list)



        initToolbarElements()

        initElements()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
//            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
//                    View.SYSTEM_UI_FLAG_FULLSCREEN or//
//                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY//
//
//            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    override fun onResume() {
        super.onResume()

        val adapter = AlarmListAdapter(datalist = DataManager.instance.alarmList, context = this@AlarmListActivity)

        alarmlistView.adapter = adapter

        DataManager.instance.currentAlarm = null

    }

    /**
     * 设置导航栏里的元素，包括动作
     * */
    fun initToolbarElements(){


        navigationLeftButton.setOnClickListener {
            val intent = Intent()
            intent.setClass(this@AlarmListActivity,AlarmPendingActivity::class.java)

            // 存放一个默认的铃声
            startActivity(intent)
        }


        navigationAddButton.setOnClickListener {

            val intent = Intent()
            intent.setClass(this@AlarmListActivity,AlarmDetailActivity::class.java)
            startActivity(intent)
        }
    }

    fun initElements(){

        val adapter = AlarmListAdapter(datalist = DataManager.instance.alarmList, context = this@AlarmListActivity)

        alarmlistView.adapter = adapter

        alarmlistView.setOnItemClickListener { parent, view, position, id ->

            val alarm = DataManager.instance.alarmList!![position]
            DataManager.instance.currentAlarm = alarm

            val intent = Intent()
            intent.setClass(this@AlarmListActivity,AlarmDetailActivity::class.java)
            startActivity(intent)
        }

    }

}
