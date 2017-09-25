package com.thissu.poetryalarm.Data

import android.content.Context
import com.chaopaikeji.poetryalert.Utility.DebugUtility
import java.util.*
import android.app.Activity
import android.app.AlarmManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.thissu.poetryalarm.ActivityModule.AlarmPendingActivity
import com.thissu.poetryalarm.ActivityModule.Sound_url


/**
 * Created by apple on 2017/8/24.
 * 数据管理器，实现数据的存储，读取和全局调度
 *
 */
class DataManager private constructor(){

    var alarmList = LinkedList<AlarmModel>()//!<闹钟数组

    var poetryList = LinkedList<PoetryModel>()//!<诗词数组

    var currentAlarm:AlarmModel? = null

    val fileName = "alarmjson.json"
    val poetryFileName = "poetrys.json"

    var filePath:String? = null

    var applicationContext:Context? = null//!<全局的上下文

    var today:Calendar = Calendar.getInstance()//!<今天的日期


    companion object {
        val instance = DataManager()
    }


    fun initAlarmData(){

        //读取存储的闹钟数据，并解析。数据使用json来存储，gson工具来读取和解析

        try {

            val inputStream = applicationContext!!.openFileInput(fileName)
            val bytes = ByteArray(inputStream.available())
            val stringb = StringBuilder()
            while (inputStream.read(bytes) != -1) {
                stringb.append(String(bytes,0,bytes.size))
            }
            inputStream.close()


            val gson = Gson()

            val list:LinkedList<AlarmModel> = gson.fromJson(stringb.toString(),object:TypeToken<LinkedList<AlarmModel>>(){}.type)

            if(list!= null){
                alarmList = list
            }

            DebugUtility.NSLog("alarms:${list}")


        }
        catch (e:Exception){
            DebugUtility.NSLog("exception:${e.message}")

        }
    }

    /**
     * 保存，把数组保存到文件中去。
     * */
    fun save(){
        //先把数组转换成json

        val gson = Gson()

        val jsonstr = gson.toJson(alarmList)

        DebugUtility.NSLog("save json:${jsonstr}")
        val outputStream = applicationContext!!.openFileOutput(fileName,
                Activity.MODE_PRIVATE)
        outputStream.write(jsonstr.toByteArray())
        outputStream.flush()
        outputStream.close()




    }

    /**
     * 初始化诗词的数据
     * */
    fun  initPoetryData(){

        //读取存储的诗词数据，并解析。数据使用json来存储，gson工具来读取和解析

        try {

            val inputStream = applicationContext!!.assets.open(poetryFileName)
            val bytes = ByteArray(inputStream.available())
            val stringb = StringBuilder()
            while (inputStream.read(bytes) != -1) {
                stringb.append(String(bytes,0,bytes.size))
            }
            inputStream.close()


            val gson = Gson()

            val list:LinkedList<PoetryModel> = gson.fromJson(stringb.toString(),object:TypeToken<LinkedList<PoetryModel>>(){}.type)

            if(list!= null){
                poetryList = list
            }

            DebugUtility.NSLog("Poetrys:${list}")


        }
        catch (e:Exception){
            DebugUtility.NSLog("exception:${e.message}")

        }
    }//end of initPoetryData

    /**
     * 随机获取一个诗词。
     * */
    fun getRandomPoetry():PoetryModel?{

        if(poetryList.size > 0){
            //生成问题
            val random = Random()
            val poeIndex = random.nextInt(poetryList.size)//

            return poetryList[poeIndex]
        }

        return null

    }



    /**
     * 闹钟的相关操作
     * @param alarmIndex:闹钟的索引
     * @param open是否打开
     * */
    fun openOrCloseAlarm(alarmIndex:Int, open:Boolean){

        val alarm = alarmList[alarmIndex]

        alarm.isopen = open

        DebugUtility.NSLog("open alarm:${alarm}")
        DebugUtility.NSLog("open alarm:${alarmList}")


        val alarmManager = applicationContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if(alarmManager == null){

            //报错
            DebugUtility.NSLog("alramManager 为空")

            return
        }

        if(open){

            try {
                //用于传给闹钟的日期
                var cal = Calendar.getInstance()

                //每次进行闹钟判断之前，还得把当前的时间更新一下
                today = Calendar.getInstance()

                var dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd,HH:mm")//时间格式

                //calendar的月份默认为0~11，但是解析的时候又按照1~12，所以要加1
                cal.time = dateFormat.parse("${today.get(Calendar.YEAR)}-${today.get(Calendar.MONTH) + 1}-${today.get(Calendar.DAY_OF_MONTH)},${alarm.timeString}")

                var alarmTime = cal.timeInMillis

                //如果时间比当前时间小，说明推送了一个过时的闹钟，那就计算下一个闹钟时间
                if(alarmTime > today.timeInMillis){

                }
                else{
                    val addInterval = calculateNextAlarmTimeAddInterval(cal = cal, alarm = alarm)

                    cal.add(Calendar.DAY_OF_WEEK,addInterval)
                    alarmTime = cal.timeInMillis

                }// end of else ,计算下一个时间并设置闹钟

                DebugUtility.NSLog("alarm calendar:${cal.get(Calendar.MONTH)}    today:${today.get(Calendar.MONTH)}")
                DebugUtility.NSLog("alarm timeInMillis:${cal.timeInMillis}")

                //推送闹钟
                val intent = Intent(applicationContext , AlarmPendingActivity::class.java)

                //设置闹钟的闹铃
                intent.putExtra(Sound_url,alarm.ringtoneUrl)

                /**
                 * 第二个参数requestCode相同的话后面的定时器会将前面的定时器"覆盖"掉，只会启动最后一个定时器，所以同一时间的定时器可以用同一个requestCode，不同时间的定时器用不同的requestCode。
                */

                val pendingintent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setWindow(AlarmManager.RTC_WAKEUP, alarmTime,
                            60*1000, pendingintent)
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingintent)
//                    if (flag == 0) {
//                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender)
//                    } else {
//                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calMethod(week, calendar.getTimeInMillis()), intervalMillis, sender)
//                    }
                }

                Toast.makeText(applicationContext,"闹钟会在${cal.get(Calendar.MONTH) + 1}月" +
                        "${cal.get(Calendar.DAY_OF_MONTH)}日-" +
                        "${cal.get(Calendar.HOUR_OF_DAY)}点" +
                        "${cal.get(Calendar.MINUTE)}分响起",Toast.LENGTH_SHORT).show()


            }
            catch(e:Exception){
                DebugUtility.NSLog("make alarm error:${e.message}")
            }


        }//end of open
        else{
            //去掉这个alarm注册的闹钟服务
        }

        save()

    }//end of openOrCloseAlarm

    //
    /**
     * 根据传递过来的日期和闹钟，计算下一个闹钟应该加几天的时间
     * @param cal:日期
     * @param alarm:闹钟
     * @return 应该增加几天
     *
     * */
    fun calculateNextAlarmTimeAddInterval(cal:Calendar, alarm:AlarmModel):Int{
        //计算下一个闹钟时间，注意Calendar中，周日是1，周一是2，依此类推。

        var cday = cal.get(Calendar.DAY_OF_WEEK)

        var addInterval = 0
        if(cday == 1){
            //当前时间是周日，从周一开始计算
            if(alarm.monday){
                addInterval = 1
            }
            else if(alarm.tuesday){
                addInterval = 2
            }
            else if(alarm.wednesday){
                addInterval = 3
            }
            else if(alarm.thursday){
                addInterval = 4
            }
            else if(alarm.friday){
                addInterval = 5
            }
            else if(alarm.saturday){
                addInterval = 6
            }
            else if(alarm.sunday){
                addInterval = 7
            }
        }
        else if(cday == 2){//当前是周一，从周二开始
            if(alarm.tuesday){
                addInterval = 1
            }
            else if(alarm.wednesday){
                addInterval = 2
            }
            else if(alarm.thursday){
                addInterval = 3
            }
            else if(alarm.friday){
                addInterval = 4
            }
            else if(alarm.saturday){
                addInterval = 5
            }
            else if(alarm.sunday){
                addInterval = 6
            }
            else if(alarm.monday){
                addInterval = 7
            }
        }
        else if(cday == 3){//当前是周二
            if(alarm.wednesday){
                addInterval = 1
            }
            else if(alarm.thursday){
                addInterval = 2
            }
            else if(alarm.friday){
                addInterval = 3
            }
            else if(alarm.saturday){
                addInterval = 4
            }
            else if(alarm.sunday){
                addInterval = 5
            }
            else if(alarm.monday){
                addInterval = 6
            }
            else if(alarm.tuesday){
                addInterval = 7
            }
        }
        else if(cday == 4){//当前是周三
            if(alarm.thursday){
                addInterval = 1
            }
            else if(alarm.friday){
                addInterval = 2
            }
            else if(alarm.saturday){
                addInterval = 3
            }
            else if(alarm.sunday){
                addInterval = 4
            }
            else if(alarm.monday){
                addInterval = 5
            }
            else if(alarm.tuesday){
                addInterval = 6
            }
            else if(alarm.wednesday){
                addInterval = 7
            }
        }
        else if(cday == 5){//当前是周四
            if(alarm.friday){
                addInterval = 1
            }
            else if(alarm.saturday){
                addInterval = 2
            }
            else if(alarm.sunday){
                addInterval = 3
            }
            else if(alarm.monday){
                addInterval = 4
            }
            else if(alarm.tuesday){
                addInterval = 5
            }
            else if(alarm.wednesday){
                addInterval = 6
            }
            else if(alarm.thursday){
                addInterval = 7
            }
        }
        else if(cday == 6){//当前是周五
            if(alarm.saturday){
                addInterval = 1
            }
            else if(alarm.sunday){
                addInterval = 2
            }
            else if(alarm.monday){
                addInterval = 3
            }
            else if(alarm.tuesday){
                addInterval = 4
            }
            else if(alarm.wednesday){
                addInterval = 5
            }
            else if(alarm.thursday){
                addInterval = 6
            }
            else if(alarm.friday){
                addInterval = 7
            }
        }
        else if(cday == 7){//当前是周六
            if(alarm.sunday){
                addInterval = 1
            }
            else if(alarm.monday){
                addInterval = 2
            }
            else if(alarm.tuesday){
                addInterval = 3
            }
            else if(alarm.wednesday){
                addInterval = 4
            }
            else if(alarm.thursday){
                addInterval = 5
            }
            else if(alarm.friday){
                addInterval = 6
            }
            else if(alarm.saturday){
                addInterval = 7
            }
        }
        return addInterval
    }//end of calculateNextAlarmTimeAddInterval


}