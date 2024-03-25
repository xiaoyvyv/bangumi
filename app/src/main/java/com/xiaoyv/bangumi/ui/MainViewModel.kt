package com.xiaoyv.bangumi.ui

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.special.widget.AnimeWidgetDataService
import com.xiaoyv.bangumi.ui.HomeRobot.Companion.SHOW_DURATION
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchIO
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.config.annotation.FeatureType
import com.xiaoyv.common.config.bean.tab.HomeBottomTab
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.widget.kts.sendValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.util.concurrent.LinkedBlockingQueue

/**
 * Class: [MainViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MainViewModel : BaseViewModel() {
    internal val onDiscoverPageIndex = MutableLiveData<Int>()
    internal val onClipboardLiveData = UnPeekLiveData<String>()

    internal val onRobotSay = MutableLiveData<String>()

    /**
     * 1 条数据，队列额外保存一条数据
     */
    private val robotSayQueue = LinkedBlockingQueue<String>(1)

    /**
     * Tabs
     */
    internal val mainTabs: List<HomeBottomTab> by lazy {
        val tab1 = fetchTab(ConfigHelper.homeTab1, FeatureType.TYPE_DISCOVER)
        val tab2 = fetchTab(ConfigHelper.homeTab2, FeatureType.TYPE_TIMELINE)
        val tab3 = fetchTab(ConfigHelper.homeTab3, FeatureType.TYPE_RANK)
        val tab4 = fetchTab(ConfigHelper.homeTab4, FeatureType.TYPE_RAKUEN)
        val tab5 = fetchTab(ConfigHelper.homeTab5, FeatureType.TYPE_PROFILE)
        listOf(tab1, tab2, tab3, tab4, tab5).filter { it.type != FeatureType.TYPE_UNSET }
    }

    /**
     * Cache Clipboard Url
     */
    private var cacheClipboardUrl: String
        get() = SPStaticUtils.getString("ClipboardUrl")
        set(value) = SPStaticUtils.put("ClipboardUrl", value.trim())

    override fun onViewCreated() {
        AnimeWidgetDataService.refresh()
    }

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

    /**
     * 替换默认TAB为对应的功能
     */
    private fun fetchTab(@FeatureType type: String, @FeatureType default: String): HomeBottomTab {
        val tabType = if (type == FeatureType.TYPE_DEFAULT || type.isBlank()) default else type
        val tabIcon = when (tabType) {
            FeatureType.TYPE_DISCOVER -> CommonDrawable.ic_bottom_home
            FeatureType.TYPE_TIMELINE -> CommonDrawable.ic_bottom_timeline
            FeatureType.TYPE_RANK -> CommonDrawable.ic_bottom_rank
            FeatureType.TYPE_PROCESS -> CommonDrawable.ic_bottom_rank
            FeatureType.TYPE_RAKUEN -> CommonDrawable.ic_bottom_group
            FeatureType.TYPE_PROFILE -> CommonDrawable.ic_bottom_profile
            else -> CommonDrawable.ic_help
        }
        return HomeBottomTab(FeatureType.name(type), View.generateViewId(), tabIcon, tabType)
    }

    /**
     * 读取默认的 TAB
     */
    fun mainDefaultTab(): Int {
        val defaultTab = when (ConfigHelper.homeDefaultTab) {
            0 -> ConfigHelper.homeTab1
            1 -> ConfigHelper.homeTab2
            2 -> ConfigHelper.homeTab3
            3 -> ConfigHelper.homeTab4
            4 -> ConfigHelper.homeTab5
            else -> FeatureType.TYPE_UNSET
        }
        val tabIndex = mainTabs.indexOfFirst { it.type == defaultTab }
        return if (tabIndex != -1) tabIndex else 0
    }

    fun handleClipboardText() {
        launchUI {
            val text = withContext(Dispatchers.IO) {
                delay(1000)

                // 匹配链接
                "https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%\\p{L}]+".toRegex()
                    .findAll(ClipboardUtils.getText())
                    .filter { RouteHelper.handleHost.contains(it.value.toHttpUrlOrNull()?.host) }
                    .map { it.value }
                    .toList()
            }

            if (text.isNotEmpty()) {
                val url = text.first().toString()
                if (url != cacheClipboardUrl) {
                    onClipboardLiveData.value = url
                    cacheClipboardUrl = url
                }
            }
        }
    }
}