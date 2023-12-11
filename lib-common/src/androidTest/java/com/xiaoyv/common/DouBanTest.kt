package com.xiaoyv.common

import android.content.pm.PackageManager
import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DouBanTest {

    @Test
    fun testApi() {
        System.err.println("豆瓣：sign -> " + sign())

        System.err.println(requestCelebrityPhoto())
    }

    private fun sign(): String {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val packageInfo = appContext.packageManager.getPackageInfo(
            "com.douban.frodo",
            PackageManager.GET_SIGNATURES
        )
        return Base64.encodeToString(packageInfo.signatures[0].toByteArray(), 0);
    }

    private fun requestCelebrityPhoto(): String {
        val paramsMap = HashMap<String, String>()
        paramsMap["q"] = "鲁路修"
        paramsMap["count"] = "1"
        paramsMap["start"] = "1"
        return DoubanWebRequest.downloadWebSiteUseGet(
            "https://frodo.douban.com/api/v2/search/subjects",
            paramsMap
        )
    }
}