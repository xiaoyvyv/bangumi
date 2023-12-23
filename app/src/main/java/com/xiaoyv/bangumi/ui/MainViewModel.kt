package com.xiaoyv.bangumi.ui

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.bangumi.ui.HomeRobot.Companion.SHOW_DURATION
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchIO
import com.xiaoyv.widget.kts.sendValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import java.util.concurrent.LinkedBlockingQueue

/**
 * Class: [MainViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MainViewModel : BaseViewModel() {
    internal val onDiscoverPageIndex = MutableLiveData<Int>()

    internal val onRobotSay = MutableLiveData<String>()

    /**
     * 30 条数据
     */
    private val robotSayQueue = LinkedBlockingQueue<String>(30)

    /**
     * 队列轮询
     */
    fun startRobotSayQueue() {
        launchIO(error = { it.printStackTrace() }) {
            while (true) {
                ensureActive()
                onRobotSay.sendValue(robotSayQueue.take())
                delay(SHOW_DURATION + 500)
            }
        }
    }

    fun resetDiscoverIndex() {
        onDiscoverPageIndex.value = 0
    }

    /**
     * 消息入队
     */
    fun addRobotSayQueue(content: String) {
        launchIO(error = { it.printStackTrace() }) {
            if (robotSayQueue.contains(content).not()) {
                robotSayQueue.offer(content)
            }
        }
    }
}