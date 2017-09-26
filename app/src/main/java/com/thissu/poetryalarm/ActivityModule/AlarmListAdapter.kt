package com.thissu.poetryalarm.ActivityModule

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import com.chaopaikeji.poetryalert.Utility.DebugUtility
import com.thissu.poetryalarm.Data.AlarmModel
import com.thissu.poetryalarm.Data.DataManager
import com.thissu.poetryalarm.R
import java.util.*

/**
 * Created by apple on 2017/8/21.
 */
class AlarmListAdapter(var datalist:LinkedList<AlarmModel>?, var context:Context?):BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var cellview = convertView

        try {

            var holder:ViewHolder? = null

            if(cellview == null){
                cellview = LayoutInflater.from(context).inflate(R.layout.item_alarm_list,parent,false)

                holder = ViewHolder()

                holder.timeText = cellview.findViewById(R.id.timeText) as TextView
                holder.stateSwitch = cellview.findViewById(R.id.alarmStateSwitch) as Switch
                holder.repeatModeText = cellview.findViewById(R.id.repeatModeText) as TextView
                holder.deleteButton = cellview.findViewById(R.id.deleteButton) as Button

                cellview.setTag(holder)
            }
            else{
                holder = cellview.tag as ViewHolder
            }

            val alarm = datalist!![position]

            holder.timeText!!.text = alarm.timeString

            holder.stateSwitch!!.tag = position

            //先取消状态改变的listener，设置完了状态再设置回来。
            holder.stateSwitch!!.setOnCheckedChangeListener { buttonView, isChecked ->  }

            holder.stateSwitch!!.isChecked = alarm.isopen

            holder.stateSwitch!!.setOnCheckedChangeListener { buttonView, isChecked ->

                stateChangeAction(button = buttonView, ischecked = isChecked)
            }

            //拼接重复模式的字符串
            var repeatModeStr = ""

            if(alarm.monday){
                repeatModeStr += "一、"
            }
            if(alarm.tuesday){
                repeatModeStr += "二、"
            }
            if(alarm.wednesday){
                repeatModeStr += "三、"
            }
            if(alarm.thursday){
                repeatModeStr += "四、"
            }
            if(alarm.friday){
                repeatModeStr += "五、"
            }
            if(alarm.saturday){
                repeatModeStr += "六、"
            }
            if(alarm.sunday){
                repeatModeStr += "日、"
            }

            if(TextUtils.isEmpty(repeatModeStr)){
                repeatModeStr = "不重复"
            }

            holder.repeatModeText!!.text = repeatModeStr;

            //删除按钮的动作
            holder.deleteButton!!.tag = position
            holder.deleteButton!!.setOnClickListener{ v:View ->
                deleteAlarmAction(v as Button)
            }

        }
        catch (e:Exception){


        }



        return cellview!!
    }

    override fun getItem(p0: Int): Any {
        return datalist!![p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return datalist!!.size
    }

    //Actions
    fun stateChangeAction(button:Button , ischecked:Boolean){

        //根据switch的状态设置对应的闹钟的启用或暂停。


        DataManager.instance.openOrCloseAlarm(button.tag as Int, ischecked)

    }

    /**
     * 删除对应位置的闹钟
     * */
    fun deleteAlarmAction(button:Button){

        AlertDialog.Builder(context)
                .setIcon(R.mipmap.delete)
                .setTitle("提示")
                .setMessage("确认删除这个闹钟吗？")
                .setNegativeButton("取消",DialogInterface.OnClickListener { dialog, which ->

                    DebugUtility.NSLog("点击了取消")

                })
                .setPositiveButton("确定",DialogInterface.OnClickListener { dialog, which ->
                    DebugUtility.NSLog("点击了确定")

                    val index = button.tag as Int

                    DataManager.instance.deleteAlarmAtIndex(index = index)

                    this.notifyDataSetChanged()

                })
                .create()
                .show()

    }// end of deleteAlarmAction

}

private class ViewHolder{

    var timeText:TextView? = null

    var stateSwitch:Switch ? = null

    var repeatModeText:TextView? = null

    var deleteButton: Button? = null

}