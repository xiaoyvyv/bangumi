package com.xiaoyv.bangumi

import com.xiaoyv.common.config.GlobalConfig
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        jumpWeb(GlobalConfig.docPrivacy, fitToolbar = true, smallToolbar = true)
    }

    private fun jumpWeb(
        url: String,
        fitToolbar: Boolean = true,
        smallToolbar: Boolean = false,
        forceBrowser: Boolean = false,
        disableHandUrl: Boolean = false,
        injectJs: String = "",
    ) {
    }
}