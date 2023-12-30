package com.xiaoyv.common.helper

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.SPStaticUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xiaoyv.common.config.GlobalConfig
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
    private const val KEY_HOME_DEFAULT_TAB = "home-default-tab"
    private const val KEY_CENTER_TYPE = "center-tab-type"
    private const val KEY_SMOOTH_FONT = "smooth-font"
    private const val KEY_USER_BG = "user-bg"
    private const val KEY_DEFAULT_COMMENT_SORT = "default-comment-sort"

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
     * 配置条目加载动画
     */
    fun configAdapterAnimation(adapter: BaseQuickAdapter<*, *>) {
        if (isAdapterAnimation.not()) return
        adapter.setItemAnimation(BaseQuickAdapter.AnimationType.ScaleIn)
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
     * 中心 TAB 的类型
     */
    var centerTabType: Int
        get() = SPStaticUtils.getInt(KEY_CENTER_TYPE, GlobalConfig.PAGE_RANK)
        set(value) = SPStaticUtils.put(KEY_CENTER_TYPE, value)

    /**
     * 用户自定义背景
     */
    var userBackground: String
        get() = SPStaticUtils.getString(KEY_USER_BG, "")
        set(value) = SPStaticUtils.put(KEY_USER_BG, value)

    /**
     * 默认评论排序
     */
    var commentDefaultSort: String
        get() = SPStaticUtils.getString(KEY_DEFAULT_COMMENT_SORT, "desc")
        set(value) = SPStaticUtils.put(KEY_DEFAULT_COMMENT_SORT, value)
}