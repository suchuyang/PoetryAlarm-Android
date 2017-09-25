package com.thissu.poetryalarm.ActivityModule

import android.app.Activity
import android.content.ContentResolver
import android.database.Cursor
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.thissu.poetryalarm.R
import java.util.*
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Button
import com.chaopaikeji.poetryalert.Utility.DebugUtility
import com.thissu.poetryalarm.Data.DataManager
import com.thissu.poetryalarm.Utilitys.AudioPlayer
import kotlin.collections.HashMap

val Sound_ID = "id"//id
val Sound_title = "title"//文件标题
val Sound_album = "album"//专辑
val Sound_displayName = "displayName"//播放名
val Sound_artist = "artist"//艺术家
val Sound_duration = "duration"//音频持续时间
val Sound_size = "size"//文件大小
val Sound_url = "url"//文件地址


class RingtoneSelectActivity : Activity() {

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
    val ringtoneRecyclerView:RecyclerView by lazy {
        findViewById(R.id.ringtoneRecyclerView) as RecyclerView
    }//循环视图

    var datalist:LinkedList<HashMap<String,String>> = LinkedList<HashMap<String,String>>()

    private var adapter:RingtoneListAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ringtone_select)

        readLocalMediaInfo()

        //第一个参数是上下文，第二个参数是水平或竖直，第三个参数表示是否反转
        val layoutmanager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        adapter = RingtoneListAdapter(datalist = datalist)
        setItemAction()

        ringtoneRecyclerView.layoutManager = layoutmanager
        ringtoneRecyclerView.adapter = adapter

        initToolbarElements()


    }// end of onCreate

    override fun onDestroy() {
        super.onDestroy()

        AudioPlayer.instance(application).stop()
    }

    /**
     * 设置导航栏里的元素，包括动作
     * */
    fun initToolbarElements(){


        navigationLeftButton.setOnClickListener {

            AudioPlayer.instance(application).stop()

            finish()
        }

//        navigationTitle.text = DataManager.instance.currentDevice?.serialNumber

        navigationRightButton.setOnClickListener {



        }
    }

    /**
     * 设置单元格的动作
     * */
    fun setItemAction(){
        adapter!!.mClickListener = object :View.OnClickListener{
            override fun onClick(v: View?) {
                var tagPosition = v?.tag

                if(tagPosition != null && tagPosition is Int){
                    //播放选中的音频

                    val map = datalist[tagPosition]

                    val url = map[Sound_url]
                    DebugUtility.NSLog("music path:$url")

                    if(url != null && !TextUtils.isEmpty(url)){
                        AudioPlayer.instance(application).play(url,true,false)
                    }
                }
            }
        }

        adapter!!.selectButtonListener = object :View.OnClickListener{

            override fun onClick(v: View?) {
                //选中了之后设置数据
                val calarm = DataManager.instance.currentAlarm
                if(calarm != null){

                    var tagPosition = v?.tag

                    if(tagPosition != null && tagPosition is Int){

                        val map = datalist[tagPosition]

                        val url = map[Sound_url]
                        val name = map[Sound_title]

                        calarm.ringtoneName = if(name != null) name else ""
                        calarm.ringtoneUrl = if(url != null) url else ""

                        AudioPlayer.instance(application).stop()
                    }


                }
            }
        }

    }


    /**
     * 读取本地的音频信息
     * */
    fun readLocalMediaInfo(){

        val contentresolver = this.contentResolver

        val mCursor = contentresolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, //路径
                arrayOf(MediaStore.Audio.Media._ID, //写入我想要获得的信息（列）
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA), null, null, null)


        for (i in 0..mCursor.getCount() - 1) {
            mCursor.moveToNext()   //读取下一行，moveToNext()有boolean返回值，执行成功返回ture,反之false，可用于判断是否读取完毕。

            val id = mCursor.getLong(0)
            val title = mCursor.getString(1)
            val album = mCursor.getString(2)
            val displayName = mCursor.getString(3)
            val artist = mCursor.getString(4)
            val duration = mCursor.getLong(5)
            val size = mCursor.getLong(6)
            val url = mCursor.getString(7)

            DebugUtility.NSLog("music infos:$id\t$title\t$album\t$displayName\t$artist\t$duration\t$size\t$url")

            val asound = HashMap<String,String>()
            asound.put(Sound_ID,id.toString())
            asound.put(Sound_title,title)
            asound.put(Sound_album,album)
            asound.put(Sound_artist,artist)
            asound.put(Sound_displayName,displayName)
            asound.put(Sound_duration,duration.toString())
            asound.put(Sound_url,url)
            asound.put(Sound_size,size.toString())

            datalist.add(asound)
        }
    }
}

/**
 * 列表适配器
 * */
private class RingtoneListAdapter (val datalist:LinkedList<HashMap<String,String>>): RecyclerView.Adapter<RingtoneListAdapter.RViewHolder>(){


    var mClickListener:View.OnClickListener? = null

    var selectButtonListener:View.OnClickListener? = null

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun onBindViewHolder(holder: RViewHolder?, position: Int) {

        val asound = datalist[position]

        holder?.soundTitleText?.text = asound[Sound_title]

        holder?.propertyText?.text = asound[Sound_artist]+"\t"+asound[Sound_size]+"M\t"+asound[Sound_duration]

        holder?.itemView!!.tag = position
        holder?.selectButton!!.tag = position

        if(mClickListener != null){

            holder!!.itemView.setOnClickListener {
                mClickListener!!.onClick(holder!!.itemView)
            }

        }

        if(selectButtonListener != null){
            holder!!.selectButton.setOnClickListener (object :View.OnClickListener{
                override fun onClick(v: View?) {
                    selectButtonListener!!.onClick(holder!!.selectButton)
                }

            })
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RViewHolder {

        val itemView = LayoutInflater.from(parent?.context).inflate(R.layout.item_ringtone_select,parent!!,false)


        return RViewHolder(itemView)

    }

    class RViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

        val soundTitleText:TextView by lazy {
            itemView.findViewById(R.id.soundTitleText) as TextView
        }//歌曲名字

        val propertyText:TextView by lazy {
            itemView.findViewById(R.id.propertyText) as TextView
        }//属性文本

        val selectButton:Button by lazy {
            itemView.findViewById(R.id.selectButton) as Button
        }//选择按钮

        init {

        }
    }
}
