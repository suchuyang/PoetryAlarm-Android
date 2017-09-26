package com.thissu.poetryalarm.ActivityModule

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.thissu.poetryalarm.Data.DataManager
import com.thissu.poetryalarm.Data.PoetryModel
import com.thissu.poetryalarm.R
import com.chaopaikeji.poetryalert.Utility.DebugUtility
import com.thissu.poetryalarm.Data.Sound_url
import com.thissu.poetryalarm.Utilitys.AudioPlayer
import com.thissu.poetryalarm.Utilitys.HomeKeyEventBroadCastReceiver
import android.view.WindowManager


val FLAG_HOMEKEY_DISPATCHED = 0x80000000.toInt()

/**
 * 闹钟闹铃时展示的页面
 * */

class AlarmPendingActivity : Activity() {


/**   property */

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

    val nameText:TextView by lazy {
        findViewById(R.id.nameText) as TextView
    }

    val writterText:TextView by lazy {
        findViewById(R.id.writterText) as TextView
    }

    val bodyPartText1:TextView by lazy {
        findViewById(R.id.bodyPartText1) as TextView
    }

    val bodyPartText2:TextView by lazy {
        findViewById(R.id.bodyPartText2) as TextView
    }

    val answerEdit:EditText by lazy {
        findViewById(R.id.answerEdit) as EditText
    }

    val confirmButton: Button by lazy {
        findViewById(R.id.confirmButton) as Button
    }//确定按钮

    var poetry: PoetryModel? = null

    var ringtoneUrl:String = ""

    var istest:Boolean = false//!<是否是测试模式，测试模式下和非测试模式显示的按钮不一样。

    private val receiver = HomeKeyEventBroadCastReceiver()

    /** functions */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_pending)

        if(DataManager.instance.applicationContext == null){
            DataManager.instance.applicationContext = applicationContext
        }
        //初始化数据
        DataManager.instance.initPoetryData()

        poetry = DataManager.instance.getRandomPoetry()

        //获取指定的闹钟
        if(intent.hasExtra(Sound_url)){
            ringtoneUrl = intent.getStringExtra(Sound_url)

            //当能从extra中取到铃声时，说明是闹钟的pending，设置istest
            istest = false
        }
        else{
            istest = true
        }


        initToolbarElements()

        //注意初始化元素的操作一定要在初始化数据之后执行
        initElements()

        //这个是锁屏下启动的关键代码。但是在红米3上不能实现锁屏时弹出页面，只能让屏幕亮
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or //这个在锁屏状态下
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        window.setFlags(FLAG_HOMEKEY_DISPATCHED,FLAG_HOMEKEY_DISPATCHED)




    }

    override fun onDestroy() {

        AudioPlayer.instance(DataManager.instance.applicationContext!!).stop()

        super.onDestroy()


    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //or
//                    View.SYSTEM_UI_FLAG_FULLSCREEN or//
//                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY//

            window.setStatusBarColor(R.color.colorHalfAlpha);
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        DebugUtility.NSLog("press menu key")

        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            return false
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {//MENU键
            //监控/拦截菜单键
            DebugUtility.NSLog("press menu key")
            return false
        }
        else if(keyCode == KeyEvent.KEYCODE_HOME){
            DebugUtility.NSLog("onKeyDown home key ")
            return false
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {

        DebugUtility.NSLog("press  key :${event?.keyCode}")


        if(event?.keyCode == KeyEvent.KEYCODE_BACK){
            return false
        }
        else if(event?.keyCode == KeyEvent.KEYCODE_HOME){
            return false
        }
        else if(event?.keyCode == KeyEvent.KEYCODE_BACK){
            return false
        }

        return super.dispatchKeyEvent(event)
    }

    /**
     * 设置导航栏里的元素，包括动作
     * */
    fun initToolbarElements(){

        if(istest){
            navigationLeftButton.visibility = View.VISIBLE
            navigationRightButton.visibility = View.GONE
        }
        else{
            navigationLeftButton.visibility = View.GONE
            navigationLeftButton.setText("休息10分钟")
        }

        navigationLeftButton.setOnClickListener {
            if(istest){

                finish()
            }
            else{
                //推送一个十分钟后的通知
            }

        }

//        navigationTitle.text = DataManager.instance.currentDevice?.serialNumber

        navigationRightButton.setOnClickListener {


            finish()

        }
    }






    /**
     * 初始化页面元素
     * */
    fun initElements(){


        //让model设计问题
        poetry?.buildQuestion()

        ////问题种类，0代表标题，1代表作者，2代表年代，3代表主体，大于3都代表主体。通过增加随机数的上限来提高主体问题的概率
        if(0 == poetry?.questionType){
            nameText.text = poetry!!.question
            writterText.text = poetry!!.writer
            bodyPartText1.text = poetry!!.mainbody
        }
        else if (1 == poetry?.questionType){
            nameText.text = poetry!!.name
            writterText.text = poetry!!.question
            bodyPartText1.text = poetry!!.mainbody
        }
        else if (2 == poetry?.questionType){
            nameText.text = poetry!!.name
            writterText.text = poetry!!.writer
            bodyPartText1.text = poetry!!.mainbody
        }
        else if (poetry!!.questionType >= 3){
            nameText.text = poetry!!.name
            writterText.text = poetry!!.writer
            bodyPartText1.text = poetry!!.bodyPart1
            bodyPartText2.text = poetry!!.bodyPart2
        }

        //确定按钮
        confirmButton.setOnClickListener {
            checkAnswer()
        }

        //播放闹铃
        if(ringtoneUrl != null && !TextUtils.isEmpty(ringtoneUrl)){
            AudioPlayer.instance(DataManager.instance.applicationContext!!).play(ringtoneUrl!!,true,false)
        }
        else{
            AudioPlayer.instance(DataManager.instance.applicationContext!!).playRaw(R.raw.ring,true,false)
        }


        //提示答案
        if(istest){
            answerEdit.hint = poetry!!.answer
        }

    }// end of initElements

    /**
     * 检查答案
     * */
    fun checkAnswer(){

        //先去掉键盘
        answerEdit.clearFocus()

        var asstring = answerEdit.text.toString()

        if(asstring.equals(poetry!!.answer)){
            Toast.makeText(this,"回答正确",Toast.LENGTH_SHORT).show()

            ////问题种类，0代表标题，1代表作者，2代表年代，3代表主体，大于3都代表主体。通过增加随机数的上限来提高主体问题的概率
            if(0 == poetry?.questionType){
                nameText.text = poetry!!.name
            }
            else if (1 == poetry?.questionType){
                writterText.text = poetry!!.writer
            }
            else if (2 == poetry?.questionType){
                writterText.text = poetry!!.writer
            }
            else if (poetry!!.questionType >= 3){
                bodyPartText1.text = poetry!!.mainbody
                bodyPartText2.text = ""
            }

            AudioPlayer.instance(DataManager.instance.applicationContext!!).stop()

            //设置导航栏的按钮
            navigationRightButton.visibility = View.VISIBLE
            navigationLeftButton.visibility = View.GONE
        }
        else{
            Toast.makeText(this,"答案不对",Toast.LENGTH_SHORT).show()
        }

    }
}
