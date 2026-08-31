package com.bgm.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 为目标应用生成启动 Baseline Profile 的测试类。
 *
 * 建议在人工录制时覆盖核心使用路径，以提升这些路径的运行性能。更多说明请参考
 * [Baseline Profile 文档](https://d.android.com/topic/performance/baselineprofiles)。
 *
 * 可以通过 Android Studio 的 “Generate Baseline Profile” 运行配置，或对应的 Gradle 任务执行生成：
 * ```
 * ./gradlew :composeApp:generateReleaseBaselineProfile
 * ```
 * 该运行配置会执行 Gradle 任务，并只运行 Profile 生成器。
 *
 * 可用的 instrumentation 参数请参考
 * [Macrobenchmark 参数文档](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args)。
 *
 * 生成完成后，可通过 [StartupBenchmarks] 验证启动性能的改善效果。
 *
 * 生成 Baseline Profile 仅支持 API 33+，或已 root 的 API 28+ 设备。
 *
 * 生成 Baseline Profile 所需的 androidx.benchmark 最低版本为 1.2.0。
 *
 * 生成器只会执行一轮。应用启动后会保留一段时间供人工操作，超时后自动完成 Profile 采集；
 * 通过 instrumentation 参数 `manualProfileRecordingSeconds` 可调整录制时长，默认 120 秒。
 *
 * 执行下面的命令，用 180 秒的窗口期手动录制 Baseline Profile，优化常见的页面和启动速度；
 * ```
 * ./gradlew :android:generateReleaseBaselineProfile \
 *   -Pandroid.testInstrumentationRunnerArguments.manualProfileRecordingSeconds=180
 * ```
 **/
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        val manualRecordingSeconds = InstrumentationRegistry.getArguments()
            .getString("manualProfileRecordingSeconds")
            ?.toLongOrNull()
            ?.takeIf { it > 0L }
            ?: 120L

        // The application id for the running build variant is read from the instrumentation arguments.
        rule.collect(
            packageName = InstrumentationRegistry.getArguments().getString("targetAppId")
                ?: throw Exception("targetAppId not passed as instrumentation runner arg"),
            maxIterations = 1,
            stableIterations = 1,
            // See: https://d.android.com/topic/performance/baselineprofiles/dex-layout-optimizations
            includeInStartupProfile = true
        ) {
            pressHome()
            startActivityAndWait()
            delay(manualRecordingSeconds * 1_000)
        }
    }

    private fun delay(time: Long) {
        Thread.sleep(time)
    }

}
