package com.thissu.poetryalarm.Utilitys

import android.content.Context
import android.media.MediaPlayer
import android.os.Vibrator
import java.io.IOException

/**
 * Created by apple on 2017/9/8.
 */
class AudioPlayer private constructor(private val mContext: Context) {

    /**
     * 音频播放
     */
    private var mPlayer: MediaPlayer? = null

    /**
     * 振动
     */
    private var mVibrator: Vibrator? = null

    /**
     * 停止播放，振动
     */
    fun stop() {
        if (mPlayer != null) {
            mPlayer!!.release()
            mPlayer = null
        }
        if (mVibrator != null) {
            mVibrator!!.cancel()
        }
    }

    /**
     * 停止播放
     */
    private fun stopPlay() {
        if (mPlayer != null) {
            mPlayer!!.release()
            mPlayer = null
        }

    }

    /**
     * 开始播放
     *
     * @param url     音频文件地址
     * @param looping 是否循环播放
     * @param vibrate 是否振动
     */
    fun play(url: String, looping: Boolean, vibrate: Boolean) {
        stop()
        // 当设为振动时
        if (vibrate) {
            vibrate()
        }

        mPlayer = MediaPlayer()
        try {
            // 设置数据源
            mPlayer!!.setDataSource(url)
            // 异步准备，不会阻碍主线程
            mPlayer!!.prepareAsync()
        } catch (e: IllegalArgumentException) {
        } catch (e: SecurityException) {
        } catch (e: IllegalStateException) {
        } catch (e: IOException) {
        }

        // 当准备好时
        mPlayer!!.setOnPreparedListener {
            if (looping) {
                mPlayer!!.isLooping = true
                mPlayer!!.start()
            } else {
                mPlayer!!.start()
            }
        }
        // 当播放完成时
        mPlayer!!.setOnCompletionListener {
            if (!mPlayer!!.isLooping) {
                stopPlay()
            }
        }
        // 当播放出现错误时
        mPlayer!!.setOnErrorListener { mp, what, extra ->
            false
        }
    }

    /**
     * 开始播放
     *
     * @param resId   音频资源文件ID
     * @param looping 是否循环播放
     * @param vibrate 是否振动
     */
    fun playRaw(resId: Int, looping: Boolean, vibrate: Boolean) {
        stop()
        // 当设为振动时
        if (vibrate) {
            vibrate()
        }

        // 设置音频资源文件
        mPlayer = MediaPlayer.create(mContext, resId)
        if (looping) {
            mPlayer!!.isLooping = true
            mPlayer!!.start()
        } else {
            mPlayer!!.start()
        }
        // 当播放完成时
        mPlayer!!.setOnCompletionListener {
            if (!mPlayer!!.isLooping) {
                stopPlay()
            }
        }
        // 当播放出现错误时
        mPlayer!!.setOnErrorListener { mp, what, extra ->
            false
        }
    }

    /**
     * 振动
     */
    fun vibrate() {
        mVibrator = mContext
                .getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // 前一个代表等待多少毫秒启动vibrator，后一个代表vibrator持续多少毫秒停止。
        // 从repeat索引开始的振动进行循环。-1表示只振动一次，非-1表示从pattern的指定下标开始重复振动。
        mVibrator!!.vibrate(longArrayOf(1000, 1000), 0)
    }

    companion object {

        /**
         * 共通音频播放器实例
         */
        var sAudioPlayer: AudioPlayer? = null

        /**
         * 是否正在播放停止录音音乐
         */
        var sIsRecordStopMusic = false

        /**
         * 取得音频播放器实例
         *
         * @param context context
         * @return 音频播放器实例
         */
        fun instance(context: Context): AudioPlayer {
            if (sAudioPlayer == null) {
                sAudioPlayer = AudioPlayer(context.applicationContext)
            }
            return sAudioPlayer!!
        }
    }

}