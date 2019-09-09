package com.test.obvious

import android.app.Application

/**
 * Application class
 */
class ObviousApplication : Application() {

    init {
        INSTANT = this
    }

    companion object {
        lateinit var INSTANT: ObviousApplication
            private set
    }

}