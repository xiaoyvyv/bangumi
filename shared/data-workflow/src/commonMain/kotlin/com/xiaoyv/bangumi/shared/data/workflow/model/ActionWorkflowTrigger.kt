package com.xiaoyv.bangumi.shared.data.workflow.model

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * 详情页等入口触发工作流的规则。
 */
@Immutable
@Serializable
data class ActionTrigger(
    /**
     * 触发器稳定类型标识，用于匹配宿主页面或业务事件。
     */
    val type: String,
    /**
     * 在入口菜单或编辑器中展示的触发器名称。
     */
    val label: String = "",
    /**
     * 可选的图标资源或图标协议标识。
     */
    val icon: String? = null,
    /**
     * 触发器特有的扩展筛选条件，键和值由具体触发器类型解释。
     */
    val filters: SerializeMap<String, JsonElement> = persistentMapOf(),
) {
    /**
     * 内置触发器预设。
     */
    companion object {
        /**
         * 在条目详情页操作菜单中展示的默认触发器。
         */
        val SubjectDetail = ActionTrigger(type = ActionTriggerType.SUBJECT_DETAIL_ACTION)

        /**
         * 手动点击触发器。
         */
        val Manual = ActionTrigger(type = ActionTriggerType.MANUAL)

        /**
         * 应用启动触发器。
         */
        val AppStart = ActionTrigger(type = ActionTriggerType.APP_START)

        /**
         * 定时任务调度触发器。
         */
        val Schedule = ActionTrigger(type = ActionTriggerType.SCHEDULE)

        /**
         * 外部 DeepLink 唤醒触发器。
         */
        val DeepLink = ActionTrigger(type = ActionTriggerType.DEEP_LINK)
    }
}

/**
 * 内置触发器类型。
 */
object ActionTriggerType {
    /**
     * 条目详情页动作入口的触发器类型。
     */
    const val SUBJECT_DETAIL_ACTION = "subject_detail_action"

    /**
     * 手动注入/点击触发。
     */
    const val MANUAL = "trigger.manual"

    /**
     * 应用启动触发。
     */
    const val APP_START = "trigger.app_start"

    /**
     * 定时调度触发。
     */
    const val SCHEDULE = "trigger.schedule"

    /**
     * DeepLink 唤醒触发。
     */
    const val DEEP_LINK = "trigger.deep_link"
}
