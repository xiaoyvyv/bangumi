package com.xiaoyv.common.helper

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xiaoyv.common.api.dns.BgmDns
import com.xiaoyv.common.config.annotation.FeatureType
import com.xiaoyv.common.config.annotation.TimelinePageType

/**
 * Class: [ConfigHelper]
 *
 * @author why
 * @since 12/10/23
 */
object ConfigHelper {
    private const val KEY_BAIDU_TRANSLATE_APP_ID = "baidu-translate-app-id"
    private const val KEY_BAIDU_TRANSLATE_APP_SECRET = "baidu-translate-secret"
    private const val KEY_ROBOT = "robot-disable"
    private const val KEY_ROBOT_VOICE = "robot-voice-disable"
    private const val KEY_IMAGE_ANIMATION = "image-animation"
    private const val KEY_IMAGE_COMPRESS = "image-compress"
    private const val KEY_GRID_ANIMATION = "grid-animation"
    private const val KEY_TIMELINE_TYPE = "timeline"
    private const val KEY_DYNAMIC_THEME = "dynamic-theme"
    private const val KEY_FILTER_DELETE_COMMENT = "filter-delete-comment"
    private const val KEY_FILTER_BREAK_UP_COMMENT = "filter-break-up-comment"
    private const val KEY_SPLIT_EP_LIST = "split-ep-list"
    private const val KEY_TOPIC_TIME_TAG = "topic-time-tag"
    private const val KEY_HOME_DEFAULT_TAB = "home-tab-default"
    private const val KEY_HOME_TAB = "home-tab"
    private const val KEY_SMOOTH_FONT = "smooth-font"
    private const val KEY_DEFAULT_COMMENT_SORT = "default-comment-sort"
    private const val KEY_ANIME_DENIED_TAGS = "anime-denied-tags"
    private const val KEY_ANIME_MAGNET_API = "anime-magnet-api"
    private const val KEY_GITHUB_USER = "github-user"
    private const val KEY_GITHUB_REPO = "github-repo"
    private const val KEY_GITHUB_TOKEN = "github-token"
    private const val KEY_NETWORK_HOSTS = "network-hosts"
    private const val KEY_VP_SLOP = "vp-slop"

    private val KEY_VERSION_TIP get() = "version-tip-" + AppUtils.getAppVersionCode()

    const val DIALOG_DIM_AMOUNT = 0.5f

    fun configBaiduTranslateId(appId: String) {
        SPStaticUtils.put(KEY_BAIDU_TRANSLATE_APP_ID, appId)
    }

    fun configBaiduTranslateSecret(secret: String) {
        SPStaticUtils.put(KEY_BAIDU_TRANSLATE_APP_SECRET, secret)
    }

    fun readBaiduTranslateConfig(): Pair<String, String> {
        return SPStaticUtils.getString(KEY_BAIDU_TRANSLATE_APP_ID).orEmpty() to
                SPStaticUtils.getString(KEY_BAIDU_TRANSLATE_APP_SECRET).orEmpty()
    }

    /**
     * 首页默认TAB
     */
    var homeDefaultTab: Int
        get() = SPStaticUtils.getInt(KEY_HOME_DEFAULT_TAB, 0)
        set(value) = SPStaticUtils.put(KEY_HOME_DEFAULT_TAB, value)

    /**
     * 首页 Tab-1 配置
     */
    var homeTab1: String
        get() = SPStaticUtils.getString(KEY_HOME_TAB + "_1", FeatureType.TYPE_DISCOVER)
        set(value) = SPStaticUtils.put(KEY_HOME_TAB + "_1", value)

    /**
     * 首页 Tab-2 配置
     */
    var homeTab2: String
        get() = SPStaticUtils.getString(KEY_HOME_TAB + "_2", FeatureType.TYPE_TIMELINE)
        set(value) = SPStaticUtils.put(KEY_HOME_TAB + "_2", value)

    /**
     * 首页 Tab-3 配置
     */
    var homeTab3: String
        get() = SPStaticUtils.getString(KEY_HOME_TAB + "_3", FeatureType.TYPE_RANK)
        set(value) = SPStaticUtils.put(KEY_HOME_TAB + "_3", value)

    /**
     * 首页 Tab-4 配置
     */
    var homeTab4: String
        get() = SPStaticUtils.getString(KEY_HOME_TAB + "_4", FeatureType.TYPE_RAKUEN)
        set(value) = SPStaticUtils.put(KEY_HOME_TAB + "_4", value)

    /**
     * 首页 Tab-5 配置
     */
    var homeTab5: String
        get() = SPStaticUtils.getString(KEY_HOME_TAB + "_5", FeatureType.TYPE_PROFILE)
        set(value) = SPStaticUtils.put(KEY_HOME_TAB + "_5", value)

    /**
     * 配置条目加载动画
     */
    fun configAdapterAnimation(
        adapter: BaseQuickAdapter<*, *>,
        recyclerView: RecyclerView,
    ) {
        if (isAdapterAnimation.not()) return
        if (recyclerView.layoutManager is GridLayoutManager) {
            adapter.setItemAnimation(BaseQuickAdapter.AnimationType.ScaleIn)
        }
    }

    /**
     * Robot 是否开启
     */
    var isRobotEnable: Boolean
        get() = SPStaticUtils.getBoolean(KEY_ROBOT, false)
        set(value) = SPStaticUtils.put(KEY_ROBOT, value)

    /**
     * Robot 语音是否开启
     */
    var isRobotVoiceEnable: Boolean
        get() = SPStaticUtils.getBoolean(KEY_ROBOT_VOICE, false)
        set(value) = SPStaticUtils.put(KEY_ROBOT_VOICE, value)

    /**
     * 上传开启图片加载动画
     */
    var isImageAnimation: Boolean
        get() = SPStaticUtils.getBoolean(KEY_IMAGE_ANIMATION, true)
        set(value) = SPStaticUtils.put(KEY_IMAGE_ANIMATION, value)

    /**
     * 条目宫格类型是否加载动画，Debug 默认不开启
     */
    var isAdapterAnimation: Boolean
        get() = SPStaticUtils.getBoolean(KEY_GRID_ANIMATION, !AppUtils.isAppDebug())
        set(value) = SPStaticUtils.put(KEY_GRID_ANIMATION, value)

    /**
     * 上传是否压缩图片
     */
    var isImageCompress: Boolean
        get() = SPStaticUtils.getBoolean(KEY_IMAGE_COMPRESS, true)
        set(value) = SPStaticUtils.put(KEY_IMAGE_COMPRESS, value)

    /**
     * 时间线默认展示好友还是全部
     */
    var timelinePageType: Int
        get() = SPStaticUtils.getInt(KEY_TIMELINE_TYPE, TimelinePageType.TYPE_ALL)
        set(value) = SPStaticUtils.put(KEY_TIMELINE_TYPE, value)

    /**
     * 是否展示启动提示弹窗
     */
    var showVersionTip: Boolean
        get() = SPStaticUtils.getBoolean(KEY_VERSION_TIP, true)
        set(value) = SPStaticUtils.put(KEY_VERSION_TIP, value)

    /**
     * 是否动态主题
     */
    var isDynamicTheme: Boolean
        get() = SPStaticUtils.getBoolean(KEY_DYNAMIC_THEME, false)
        set(value) = SPStaticUtils.put(KEY_DYNAMIC_THEME, value)

    /**
     * 是否过滤删除的回复
     */
    var isFilterDeleteComment: Boolean
        get() = SPStaticUtils.getBoolean(KEY_FILTER_DELETE_COMMENT, true)
        set(value) = SPStaticUtils.put(KEY_FILTER_DELETE_COMMENT, value)

    /**
     * 是否过滤绝交者的回复
     */
    var isFilterBreakUpComment: Boolean
        get() = SPStaticUtils.getBoolean(KEY_FILTER_BREAK_UP_COMMENT, true)
        set(value) = SPStaticUtils.put(KEY_FILTER_BREAK_UP_COMMENT, value)

    /**
     * 平滑字体
     */
    var isSmoothFont: Boolean
        get() = SPStaticUtils.getBoolean(KEY_SMOOTH_FONT, true)
        set(value) = SPStaticUtils.put(KEY_SMOOTH_FONT, value)

    /**
     * 章节是否分割
     */
    var isSplitEpList: Boolean
        get() = SPStaticUtils.getBoolean(KEY_SPLIT_EP_LIST, true)
        set(value) = SPStaticUtils.put(KEY_SPLIT_EP_LIST, value)

    /**
     * 帖子时间标记
     */
    var isTopicTimeFlag: Boolean
        get() = SPStaticUtils.getBoolean(KEY_TOPIC_TIME_TAG, true)
        set(value) = SPStaticUtils.put(KEY_TOPIC_TIME_TAG, value)

    /**
     * 默认评论排序
     */
    var commentDefaultSort: String
        get() = SPStaticUtils.getString(KEY_DEFAULT_COMMENT_SORT, "desc")
        set(value) = SPStaticUtils.put(KEY_DEFAULT_COMMENT_SORT, value)

    /**
     * 磁力搜索 API
     */
    var magnetSearchApi: String
        get() = SPStaticUtils.getString(KEY_ANIME_MAGNET_API)
        set(value) = SPStaticUtils.put(KEY_ANIME_MAGNET_API, value)

    /**
     * Anime-Pictures 默认禁用标签
     */
    var animePicDeniedTags: String
        get() = SPStaticUtils.getString(
            KEY_ANIME_DENIED_TAGS,
            "breasts||erotic||light erotic||hard erotic||bare shoulders||empty eyes||black background||spoilers"
        )
        set(value) = SPStaticUtils.put(KEY_ANIME_DENIED_TAGS, value)


    /**
     * Github User
     */
    var githubUser: String
        get() = UserHelper.userSp.getString(KEY_GITHUB_USER)
        set(value) = UserHelper.userSp.put(KEY_GITHUB_USER, value.trim())

    /**
     * Github Repo
     */
    var githubRepo: String
        get() = UserHelper.userSp.getString(KEY_GITHUB_REPO)
        set(value) = UserHelper.userSp.put(KEY_GITHUB_REPO, value.trim())

    /**
     * Github Token
     */
    var githubToken: String
        get() = UserHelper.userSp.getString(KEY_GITHUB_TOKEN)
        set(value) = UserHelper.userSp.put(KEY_GITHUB_TOKEN, value.trim())

    /**
     * Network hosts
     */
    var netHosts: String
        get() = SPStaticUtils.getString(KEY_NETWORK_HOSTS, BgmDns.DEFAULT_HOSTS)
        set(value) = SPStaticUtils.put(KEY_NETWORK_HOSTS, value.trim())

    /**
     * VP 滚动斜率阈值倍数，SDK 默认值为 2，越大越不容易左右滑动误触发
     */
    var vpTouchSlop: Int
        get() = SPStaticUtils.getInt(KEY_VP_SLOP, 2)
        set(value) = SPStaticUtils.put(KEY_VP_SLOP, value)
}