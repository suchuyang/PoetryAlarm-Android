package com.thissu.poetryalarm.Data

/**
 * Created by apple on 2017/8/21.
 * 闹钟的数据模型
 */
class AlarmModel {

    var timeString:String = "00:00"//!<闹钟的时间

    var repeatWeekday:String = ""//!<重复，里面存储从1到7的数字，有几就表示周几响

    var remarkString:String = ""//!<备注

    var ringtoneName:String = ""//!<铃声名

    var ringtoneUrl:String = ""//!<铃声地址

    var isopen:Boolean = false//默认关闭状态

    //星期的重复，默认是每天重复
    var sunday = true
    var monday = true
    var tuesday = true
    var wednesday = true
    var thursday = true
    var friday = true
    var saturday = true


    override fun toString(): String {
        return "AlarmModel(timeString='$timeString', repeatWeekday='$repeatWeekday', remarkString='$remarkString', isopen=$isopen)"
    }


}