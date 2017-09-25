package com.thissu.poetryalarm.ActivityModule

import android.app.Activity
import android.os.Bundle

import com.thissu.poetryalarm.R

class EmptyActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty)
    }
}
