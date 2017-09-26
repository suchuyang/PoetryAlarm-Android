package com.thissu.poetryalarm.ActivityModule

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle

import com.thissu.poetryalarm.R

import android.os.Build
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.chaopaikeji.poetryalert.Utility.DebugUtility
import java.util.*
import com.thissu.poetryalarm.Data.AlarmModel
import com.thissu.poetryalarm.Data.DataManager
import java.text.DateFormat
import java.text.SimpleDateFormat


class AlarmDetailActivity : Activity() {

    /**导航栏*/
    val navigationTitle: TextView by lazy {
        findViewById(R.id.navigationTitleText) as TextView
    }//标题

    val navigationLeftButton : Button by lazy{
        findViewById(R.id.navigationLeftButton) as Button
    }

    val navigationRightButton: Button by lazy {
        findViewById(R.id.navigationRightButton) as Button
    }//

    /**页面元素*/
    val timeButton:Button by lazy {
        findViewById(R.id.timeButton) as Button
    }//时间

    val remarkEdit:EditText by lazy {
        findViewById(R.id.remarkEdit) as EditText
    }//备注

    val ringSelectButton:Button by lazy {
        findViewById(R.id.ringSelectButton) as Button
    }//选择铃声的按钮

    /**周一到周日*/
    val monButton:Button by lazy {
        findViewById(R.id.monButton) as Button
    }
    val tuesButton:Button by lazy {
        findViewById(R.id.tuesButton) as Button
    }
    val wedButton:Button by lazy {
        findViewById(R.id.wedButton) as Button
    }
    val thurButton:Button by lazy {
        findViewById(R.id.thurButton) as Button
    }
    val friButton:Button by lazy {
        findViewById(R.id.friButton) as Button
    }
    val satButton:Button by lazy {
        findViewById(R.id.satButton) as Button
    }
    val sunButton:Button by lazy {
        findViewById(R.id.sunButton) as Button
    }

    /**数据相关*/
    var currentAlarm:AlarmModel? = null//!<本页面的闹钟模型。


    var dateFormat:SimpleDateFormat = SimpleDateFormat("HH:mm")//时间格式


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_detail)

        initToolbarElements()
        initElements()

    }

    override fun onResume() {
        super.onResume()

        if(!TextUtils.isEmpty(currentAlarm!!.ringtoneName )){
            ringSelectButton.text = "铃声：${currentAlarm!!.ringtoneName }"
        }
        else{
            ringSelectButton.text = "铃声：默认"
        }



    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //or
//                    View.SYSTEM_UI_FLAG_FULLSCREEN or//
//                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY//

            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 设置导航栏里的元素，包括动作
     * */
    fun initToolbarElements(){


        navigationLeftButton.setOnClickListener {
            DataManager.instance.currentAlarm = null
            finish()
        }

//        navigationTitle.text = DataManager.instance.currentDevice?.serialNumber

        navigationRightButton.setOnClickListener {


            if(DataManager.instance.currentAlarm == null){
                //保存新的
                currentAlarm!!.timeString = timeButton.text.toString()
                currentAlarm!!.remarkString = remarkEdit.text.toString()
                currentAlarm!!.isopen = true

                DataManager.instance.alarmList.add(currentAlarm!!)

                //应用新的闹钟
                DataManager.instance.openOrCloseAlarm(DataManager.instance.alarmList.indexOf(currentAlarm!!),open = true)
            }
            else{

                //改变旧的
                DataManager.instance.currentAlarm!!.timeString = timeButton.text.toString()
                DataManager.instance.currentAlarm!!.remarkString = remarkEdit.text.toString()

                //如果旧的闹钟是打开状态，那需要重新设置一次闹钟
                if(DataManager.instance.currentAlarm!!.isopen){
                    DataManager.instance.openOrCloseAlarm(DataManager.instance.alarmList.indexOf(currentAlarm!!),open = true)
                }
            }


            //每次改变都要存储
            DataManager.instance.save()
            finish()
        }
    }

    /**
     * 初始化页面的元素
     * */
    fun initElements(){

        //如果当前的alarm有值，说明是查看已有的闹钟，如果为空就是新建。
        if(DataManager.instance.currentAlarm != null){
            val timestr = DataManager.instance.currentAlarm!!.timeString

            timeButton.setText(timestr)

            remarkEdit.setText(DataManager.instance.currentAlarm!!.remarkString)

            currentAlarm = DataManager.instance.currentAlarm!!

        }
        else{
            //使用当前的时间
            //显示一个时间选择器
            val calender = Calendar.getInstance()
            timeButton.text = "${calender.get(Calendar.HOUR_OF_DAY)}:${calender.get(Calendar.MINUTE)}"

            //创建一个闹钟，如果最后保存了，就把这个闹钟保存起来，如果最后不保存，那当然就直接废弃掉。
            currentAlarm = AlarmModel()
        }


        timeButton.setOnClickListener{

            //显示一个时间选择器
            val calender = Calendar.getInstance()

            TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                var newTime = "${hourOfDay}:${minute}"

                if(minute < 10){
                    newTime = "${hourOfDay}:0${minute}"
                }

                timeButton.setText(newTime)

            },calender.get(Calendar.HOUR_OF_DAY),calender.get(Calendar.MINUTE),true).show()
        }

        //设置周一到周日的按钮
        monButton.isSelected = currentAlarm!!.monday
        tuesButton.isSelected = currentAlarm!!.tuesday
        wedButton.isSelected = currentAlarm!!.wednesday
        thurButton.isSelected = currentAlarm!!.thursday
        friButton.isSelected = currentAlarm!!.friday
        satButton.isSelected = currentAlarm!!.saturday
        sunButton.isSelected = currentAlarm!!.sunday


        //铃声
        if(!TextUtils.isEmpty(currentAlarm!!.ringtoneName )){
            ringSelectButton.text = "铃声：${currentAlarm!!.ringtoneName }"
        }
        else{
            ringSelectButton.text = "铃声：默认"
        }

        ringSelectButton.setOnClickListener {

            //注意这里直接操作了全局的currentAlarm，这可能会在以后的操作中导致出现数据莫名其妙被修改的问题。所以在使用这个变量的时候，一定要搞清楚变量的使用范围。
            DataManager.instance.currentAlarm = currentAlarm

            val intent = Intent()
            intent.setClass(this@AlarmDetailActivity,RingtoneSelectActivity::class.java)


            startActivity(intent)
        }
    }


    /**
     * 星期的按钮的点击事件
     * */
    fun weekdayButtonClick(button:View){

        var tb = button as Button

        val weekdayString = tb.text.toString()//周几

        var repeatWeekStr = DataManager.instance.currentAlarm?.repeatWeekday

        button.isSelected = !button.isSelected


        //把周几转换为数字。
        when(weekdayString){
            "日"->currentAlarm!!.sunday = button.isSelected
            "一"->currentAlarm!!.monday = button.isSelected
            "二"->currentAlarm!!.tuesday = button.isSelected
            "三"->currentAlarm!!.wednesday = button.isSelected
            "四"->currentAlarm!!.thursday = button.isSelected
            "五"->currentAlarm!!.friday = button.isSelected
            "六"->currentAlarm!!.saturday = button.isSelected
            else -> DebugUtility.NSLog("不知道点了什么，走了错误的分支。")
        }


    }//end of weekdayButtonClick

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        DebugUtility.NSLog("requestcode:$requestCode\nresultcode:$resultCode\ndata:$data")
    }

}
