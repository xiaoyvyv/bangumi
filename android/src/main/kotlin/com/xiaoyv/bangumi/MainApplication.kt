package com.xiaoyv.bangumi

import android.app.Application
import com.xiaoyv.bangumi.shared.application

/**
 * [MainApplication]
 *
 * @author why
 * @since 2025/1/13
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        application = this
    }
}