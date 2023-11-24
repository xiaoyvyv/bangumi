package com.xiaoyv.common

import android.app.Application
import com.xiaoyv.blueprint.BluePrint

/**
 * Class: [MainApp]
 *
 * @author why
 * @since 11/24/23
 */
class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        BluePrint.init(this, false)
    }
}