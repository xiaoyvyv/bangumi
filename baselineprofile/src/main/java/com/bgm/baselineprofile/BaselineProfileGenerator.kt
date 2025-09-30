package com.bgm.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This test class generates a basic startup baseline profile for the target package.
 *
 * We recommend you start with this but add important user flows to the profile to improve their performance.
 * Refer to the [baseline profile documentation](https://d.android.com/topic/performance/baselineprofiles)
 * for more information.
 *
 * You can run the generator with the "Generate Baseline Profile" run configuration in Android Studio or
 * the equivalent `generateBaselineProfile` gradle task:
 * ```
 * ./gradlew :composeApp:generateReleaseBaselineProfile
 * ```
 * The run configuration runs the Gradle task and applies filtering to run only the generators.
 *
 * Check [documentation](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args)
 * for more information about available instrumentation arguments.
 *
 * After you run the generator, you can verify the improvements running the [StartupBenchmarks] benchmark.
 *
 * When using this class to generate a baseline profile, only API 33+ or rooted API 28+ are supported.
 *
 * The minimum required version of androidx.benchmark to generate a baseline profile is 1.2.0.
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        // The application id for the running build variant is read from the instrumentation arguments.
        rule.collect(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),
            maxIterations = 15,
            stableIterations = 3,
            // See: https://d.android.com/topic/performance/baselineprofiles/dex-layout-optimizations
            includeInStartupProfile = true
        ) {
            // This block defines the app's critical user journey. Here we are interested in
            // optimizing for app startup. But you can also navigate and scroll through your most important UI.

            // Start default activity for your app
            pressHome()
            startActivityAndWait()

            // 交互
            waitForHomeMainContent()
        }
    }

    private fun delay(time: Long) {
        Thread.sleep(time)
    }

    fun MacrobenchmarkScope.waitForHomeMainContent() {
        device.waitAndFind("已登录", 40_000)

        listOf(
            "新番", "排行榜", "追番进度", "年鉴", "每日放送",
            "条目浏览", "标签", "时间胶囊", "超展开"
        ).forEach {
            val homePager = device.waitAndFind("home_main_pager")
            val mainList = homePager.waitAndFind("home_main_list")

            mainList.waitAndFind(it).click()
            delay(4000)

            device.pressBack()
            delay(2000)
        }

        delay(2000)
        val homePager = device.waitAndFind("home_main_pager")
        val mainList = device.waitAndFind("home_main_list")

        mainList.setGestureMargin(device.displayWidth / 5)
        mainList.scroll(Direction.DOWN, 1f)
        delay(1000)

        val calendarRowToday = mainList.waitAndFind("calendar_card_row_today")
        calendarRowToday.fling(Direction.RIGHT)
        delay(1000)
        calendarRowToday.fling(Direction.LEFT)
        delay(1000)

        val calendarRowTomorrow = mainList.waitAndFind("calendar_card_row_tomorrow")
        calendarRowTomorrow.fling(Direction.RIGHT)
        delay(1000)
        calendarRowTomorrow.fling(Direction.LEFT)
        delay(1000)

        mainList.fling(Direction.DOWN)
        delay(1000)

        mainList.fling(Direction.UP)
        delay(1000)

        listOf("人物", "日志", "小组", "目录", "首页").forEach {
            homePager.waitAndFind(it).click()
            delay(2000)
            homePager.fling(Direction.DOWN)
            delay(2000)
        }

        device.waitAndFind("home_main_list").fling(Direction.UP)
        device.waitForIdle()
        delay(2000)
        device.waitAndFind("home_main_list").scroll(Direction.DOWN, 1f)
        device.waitForIdle()

        runCatching {
            val snacks = device.findObjects(By.desc("calendar_card_item"))
            val index = (iteration ?: 0) % snacks.size
            snacks[index].click()
            delay(4000)

            device.waitAndFind("角色").click()
            delay(4000)

            val characters = device.findObjects(By.desc("character_item"))
            val characterIdx = (iteration ?: 0) % snacks.size
            characters[characterIdx].click()
            delay(4000)

            device.waitAndFind("scrollable_tab").fling(Direction.RIGHT)
            delay(2000)

            device.waitAndFind("收藏").click()
            delay(2000)

            device.waitAndFind("user_item").click()
            device.waitForIdle()
        }
    }

    fun UiObject2.waitAndFind(desc: String, timeout: Long = 10_000): UiObject2 {
        wait(Until.hasObject(By.descContains(desc)), timeout)
        return findObject(By.descContains(desc))
    }

    fun UiDevice.waitAndFind(desc: String, timeout: Long = 10_000): UiObject2 {
        wait(Until.hasObject(By.descContains(desc)), timeout)
        return findObject(By.descContains(desc))
    }
}