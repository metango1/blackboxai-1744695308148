package com.example.markdowntasks

import android.app.Application
import android.content.Context

class TasksApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}
