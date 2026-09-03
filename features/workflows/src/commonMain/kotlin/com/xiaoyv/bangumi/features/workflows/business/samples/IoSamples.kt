package com.xiaoyv.bangumi.features.workflows.business.samples

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCapability
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionClipboardConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionConfirmConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFileConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFlowConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionImagePreviewConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionInputDialogConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNotificationConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionOpenAppConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionOpenUrlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionOpenWebConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionProgressDialogMode
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSelectDialogConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionShareConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionStorageConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSyncCookieConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionToastConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionVideoPreviewConfigKey
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

/**
 * 本地存储、HTTP 网络请求、系统交互及 UI 副作用弹窗工作流样例集合。
 */
internal object IoSamples {
    val all: List<ActionWorkflow> = buildList {
        // Storage 节点
        add(fileReadWriteSample())
        add(
            linear(
                "storage_preferences_set",
                "写入工作流私有存储",
                ActionNodeType.STORAGE_PREFERENCES_SET,
                config(ActionStorageConfigKey.KEY to "sample_favorite", ActionStorageConfigKey.VALUE to true, ActionStorageConfigKey.OUTPUT_KEY to "savedFavorite")
            )
        )
        add(
            linear(
                "storage_preferences_get",
                "读取工作流私有存储",
                ActionNodeType.STORAGE_PREFERENCES_GET,
                config(ActionStorageConfigKey.KEY to "sample_favorite", ActionStorageConfigKey.OUTPUT_KEY to "savedFavorite")
            )
        )
        add(
            linear(
                "storage_preferences_delete",
                "删除工作流私有存储",
                ActionNodeType.STORAGE_PREFERENCES_DELETE,
                config(ActionStorageConfigKey.KEY to "sample_favorite", ActionStorageConfigKey.OUTPUT_KEY to "deleted")
            )
        )
        add(
            linear(
                "storage_preferences_has",
                "检查工作流私有存储",
                ActionNodeType.STORAGE_PREFERENCES_HAS,
                config(ActionStorageConfigKey.KEY to "sample_favorite", ActionStorageConfigKey.OUTPUT_KEY to "exists")
            )
        )
        add(linear("storage_preferences_clear", "清空工作流私有存储", ActionNodeType.STORAGE_PREFERENCES_CLEAR))

        // Intent & UI 动作节点
        add(linear("action_show_toast", "显示提示", ActionNodeType.SHOW_TOAST, config(ActionToastConfigKey.MESSAGE to "工作流节点运行成功")))
        add(
            linear(
                "action_write_clipboard",
                "写入剪贴板",
                ActionNodeType.WRITE_CLIPBOARD,
                config(ActionClipboardConfigKey.TEXT to "Bangumi 工作流"),
                setOf(ActionCapability.CLIPBOARD_WRITE)
            )
        )
        add(
            linear(
                "action_read_clipboard",
                "读取剪贴板",
                ActionNodeType.READ_CLIPBOARD,
                config(ActionClipboardConfigKey.OUTPUT_KEY to "clipContent")
            )
        )
        add(
            linear(
                "action_open_external_url",
                "打开外部网页",
                ActionNodeType.OPEN_EXTERNAL_URL,
                config(ActionOpenUrlConfigKey.URL to "https://bgm.tv/subject/633836"),
                setOf(ActionCapability.OPEN_EXTERNAL_URL)
            )
        )
        add(
            linear(
                "action_open_external_app",
                "打开应用市场",
                ActionNodeType.OPEN_EXTERNAL_APP,
                config(ActionOpenAppConfigKey.URI to "market://details?id=com.xiaoyv.bangumi.multiplatform"),
                setOf(ActionCapability.OPEN_EXTERNAL_APP)
            )
        )
        add(
            linear(
                "action_open_internal_web",
                "打开应用内网页",
                ActionNodeType.OPEN_INTERNAL_WEB,
                config(ActionOpenWebConfigKey.URL to "https://hanime1.me"),
                setOf(ActionCapability.OPEN_INTERNAL_WEB)
            )
        )
        add(
            linear(
                "action_sync_cookie",
                "同步 Web Cookie",
                ActionNodeType.SYNC_COOKIE,
                config(
                    ActionSyncCookieConfigKey.URL to "https://hanime1.me",
                    ActionSyncCookieConfigKey.TITLE to "同步 hanime1 Cookie"
                ),
                setOf(ActionCapability.NETWORK_COOKIE_SYNC)
            )
        )
        add(
            linear(
                "ui_confirm",
                "二次确认弹窗",
                ActionNodeType.UI_CONFIRM,
                config(ActionConfirmConfigKey.TITLE to "温馨提示", ActionConfirmConfigKey.MESSAGE to "是否确认执行此操作？"),
                setOf(ActionCapability.CONFIRM_DIALOG)
            )
        )
        add(
            linear(
                "ui_input_dialog",
                "输入框弹窗",
                ActionNodeType.UI_INPUT_DIALOG,
                config(
                    ActionInputDialogConfigKey.TITLE to "请输入条目备注",
                    ActionInputDialogConfigKey.SUBTITLE to "支持自定义文本输入",
                    ActionInputDialogConfigKey.OUTPUT_KEY to "userNote"
                ),
                setOf(ActionCapability.INPUT_DIALOG)
            )
        )
        add(progressDialogLifecycleSample())
        add(progressDialogLoopSample())
        add(progressDialogParallelSample())
        add(
            linear(
                "ui_select_dialog_single",
                "单选列表弹窗",
                ActionNodeType.UI_SELECT_DIALOG,
                buildJsonObject {
                    put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("请选择收藏状态"))
                    put(ActionSelectDialogConfigKey.SUBTITLE, JsonPrimitive("点击任意选项即可选中并完成"))
                    put(ActionSelectDialogConfigKey.IS_MULTI_SELECT, JsonPrimitive(false))
                    put(
                        ActionSelectDialogConfigKey.OPTIONS,
                        buildJsonArray {
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("想看"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("wish"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("看过"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("collect"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("在看"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("do"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("搁置"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("on_hold"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("抛弃"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("dropped"))
                            })
                        }
                    )
                    put(ActionSelectDialogConfigKey.DEFAULT_VALUES, buildJsonArray { add(JsonPrimitive("collect")) })
                    put(ActionSelectDialogConfigKey.OUTPUT_KEY, JsonPrimitive("selectedStatus"))
                },
                setOf(ActionCapability.SELECT_DIALOG)
            )
        )
        add(
            linear(
                "ui_select_dialog_multi",
                "多选列表弹窗",
                ActionNodeType.UI_SELECT_DIALOG,
                buildJsonObject {
                    put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("请选择感兴趣的标签"))
                    put(ActionSelectDialogConfigKey.SUBTITLE, JsonPrimitive("勾选选项后点击确定提交"))
                    put(ActionSelectDialogConfigKey.IS_MULTI_SELECT, JsonPrimitive(true))
                    put(
                        ActionSelectDialogConfigKey.OPTIONS,
                        buildJsonArray {
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("原创"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("original"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("搞笑"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("comedy"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("日常"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("slice_of_life"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("音乐"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("music"))
                            })
                        }
                    )
                    put(
                        ActionSelectDialogConfigKey.DEFAULT_VALUES,
                        buildJsonArray {
                            add(JsonPrimitive("original"))
                            add(JsonPrimitive("music"))
                        }
                    )
                    put(ActionSelectDialogConfigKey.OUTPUT_KEY, JsonPrimitive("selectedTags"))
                },
                setOf(ActionCapability.SELECT_DIALOG)
            )
        )
        add(
            linear(
                "ui_select_dialog_index",
                "列表选择 (输出 Index 索引模式)",
                ActionNodeType.UI_SELECT_DIALOG,
                buildJsonObject {
                    put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("请选择排序方式"))
                    put(ActionSelectDialogConfigKey.IS_MULTI_SELECT, JsonPrimitive(false))
                    put(
                        ActionSelectDialogConfigKey.OPTIONS,
                        buildJsonArray {
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("按热度排序"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("hot"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("按评分排序"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("rating"))
                            })
                            add(buildJsonObject {
                                put(ActionSelectDialogConfigKey.TITLE, JsonPrimitive("按上映时间排序"))
                                put(ActionSelectDialogConfigKey.VALUE, JsonPrimitive("date"))
                            })
                        }
                    )
                    put(ActionSelectDialogConfigKey.DEFAULT_INDICES, buildJsonArray { add(JsonPrimitive(1)) })
                    put(ActionSelectDialogConfigKey.OUTPUT_KEY, JsonPrimitive("selectedIndex"))
                },
                setOf(ActionCapability.SELECT_DIALOG)
            )
        )
        add(linear("system_share", "系统分享", ActionNodeType.SYSTEM_SHARE, config(ActionShareConfigKey.TEXT to "Bangumi 多平台工作流引擎"), setOf(ActionCapability.SHARE)))
        add(
            linear(
                "system_notification",
                "发送系统通知",
                ActionNodeType.SYSTEM_NOTIFICATION,
                config(ActionNotificationConfigKey.CONTENT to "您有一条新的通知消息"),
                setOf(ActionCapability.NOTIFICATION)
            )
        )
        add(linear("system_vibrate", "设备震动", ActionNodeType.SYSTEM_VIBRATE))
        add(
            linear(
                "action_http_request",
                "HTTP GET 请求与重试",
                ActionNodeType.HTTP_REQUEST,
                config(
                    ActionHttpConfigKey.URL to "https://next.bgm.tv/p1/subjects/633836",
                    ActionHttpConfigKey.METHOD to "GET",
                    ActionHttpConfigKey.RETRY_COUNT to 2,
                    ActionHttpConfigKey.RETRY_DELAY_MILLIS to 300L,
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                ),
                setOf(ActionCapability.NETWORK, ActionCapability.NETWORK_LOCAL_COOKIE_ACCESS)
            )
        )
        add(
            linear(
                "image_preview",
                "图片预览",
                ActionNodeType.IMAGE_PREVIEW,
                config(
                    ActionImagePreviewConfigKey.INDEX to 0,
                    ActionImagePreviewConfigKey.IMAGES to JsonArray(listOf(JsonPrimitive("https://lain.bgm.tv/pic/photo/l/47/7e/837364_do644.jpg"))),
                ),
                setOf(ActionCapability.IMAGE_PREVIEW)
            )
        )
        add(
            linear(
                "video_preview",
                "视频预览",
                ActionNodeType.VIDEO_PREVIEW,
                config(
                    ActionVideoPreviewConfigKey.URL to "https://qiniu-web-assets.dcloud.net.cn/unidoc/zh/uni-app-video-courses.mp4",
                    ActionVideoPreviewConfigKey.HEADERS to buildJsonObject {
                        put("User-Agent", JsonPrimitive("Mozilla/5.0"))
                    },
                ),
                setOf(ActionCapability.VIDEO_PREVIEW)
            )
        )
    }

    /**
     * 在同一工作流沙箱中写入日志后读取，验证文件节点的隔离存储。
     */
    private fun fileReadWriteSample(): ActionWorkflow = workflow(
        id = "file_read_write",
        name = "测试：文件沙箱读写",
        description = "写入工作流专属日志文件后读取内容，文件不会暴露到其他工作流沙箱。",
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "write", ActionNodeType.FILE_WRITE_TEXT, "写入日志", config(
                    ActionFileConfigKey.PATH to "logs/workflow.log",
                    ActionFileConfigKey.TEXT to "Bangumi workflow started",
                    ActionFileConfigKey.OUTPUT_KEY to "written",
                )
            ),
            node(
                "read", ActionNodeType.FILE_READ_TEXT, "读取日志", config(
                    ActionFileConfigKey.PATH to "logs/workflow.log",
                    ActionFileConfigKey.OUTPUT_KEY to "logContent",
                )
            ),
            node("end", ActionNodeType.FLOW_END, "结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "write"),
            edge("write", ActionControlPortId.NEXT, "read"),
            edge("read", ActionControlPortId.NEXT, "end"),
        ),
    )

    /**
     * 进度弹窗的完整生命周期工作流：请求展示 -> 延时 -> 更新进度 -> 延时 -> 关闭弹窗 -> Toast 提示。
     */
    private fun progressDialogLifecycleSample(): ActionWorkflow = workflow(
        id = "ui_progress_dialog",
        name = "测试：进度弹窗生命周期",
        description = "演示进度弹窗的非阻塞生命周期：启动展示 -> 动态更新 -> 关闭弹窗。",
        capabilities = setOf(ActionCapability.PROGRESS_DIALOG),
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "show_progress",
                ActionNodeType.UI_PROGRESS_DIALOG,
                "展示进度",
                config(
                    ActionProgressDialogConfigKey.TITLE to "下载视频",
                    ActionProgressDialogConfigKey.MESSAGE to "正在连接服务器 (35%)",
                    ActionProgressDialogConfigKey.MODE to ActionProgressDialogMode.DETERMINATE,
                    ActionProgressDialogConfigKey.PROGRESS to 35,
                    ActionProgressDialogConfigKey.MAX_PROGRESS to 100,
                ),
            ),
            node(
                "delay_1",
                ActionNodeType.FLOW_DELAY,
                "模拟连接耗时",
                config(ActionFlowConfigKey.DELAY_MILLIS to 1000),
            ),
            node(
                "update_progress",
                ActionNodeType.UI_PROGRESS_UPDATE,
                "更新进度",
                config(
                    ActionProgressDialogConfigKey.MESSAGE to "正在写入本地文件 (80%)",
                    ActionProgressDialogConfigKey.PROGRESS to 80,
                ),
            ),
            node(
                "delay_2",
                ActionNodeType.FLOW_DELAY,
                "模拟写入耗时",
                config(ActionFlowConfigKey.DELAY_MILLIS to 1000),
            ),
            node(
                "dismiss_progress",
                ActionNodeType.UI_PROGRESS_DISMISS,
                "关闭进度",
                config(),
            ),
            node(
                "finish_toast",
                ActionNodeType.SHOW_TOAST,
                "完成提示",
                config(ActionToastConfigKey.MESSAGE to "视频下载完成！"),
            ),
            node("end", ActionNodeType.FLOW_END, "结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "show_progress"),
            edge("show_progress", ActionControlPortId.SUCCESS, "delay_1"),
            edge("delay_1", ActionControlPortId.NEXT, "update_progress"),
            edge("update_progress", ActionControlPortId.SUCCESS, "delay_2"),
            edge("delay_2", ActionControlPortId.NEXT, "dismiss_progress"),
            edge("dismiss_progress", ActionControlPortId.SUCCESS, "finish_toast"),
            edge("finish_toast", ActionControlPortId.SUCCESS, "end"),
        ),
    )

    /**
     * 进度弹窗循环动态刷新工作流：
     * 展示进度 -> 循环 10 次 (间隔 100ms 增加 10% 进度并实时刷新 UI) -> 达到 100% 退出循环 -> 关闭弹窗 -> Toast 提示。
     */
    private fun progressDialogLoopSample(): ActionWorkflow = workflow(
        id = "ui_progress_dialog_loop",
        name = "测试：进度弹窗循环刷新",
        description = "演示进度弹窗在循环中每 100ms 动态累加进度并实时刷新 UI，直到 100% 后自动关闭。",
        capabilities = setOf(ActionCapability.PROGRESS_DIALOG),
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "init_progress",
                ActionNodeType.SET_VARIABLE,
                "初始化进度",
                config(
                    ActionDataConfigKey.KEY to "progress",
                    ActionDataConfigKey.VALUE to 0,
                ),
            ),
            node(
                "show_progress",
                ActionNodeType.UI_PROGRESS_DIALOG,
                "展示进度弹窗",
                config(
                    ActionProgressDialogConfigKey.TITLE to "模拟下载中",
                    ActionProgressDialogConfigKey.MESSAGE to "准备开始...",
                    ActionProgressDialogConfigKey.MODE to ActionProgressDialogMode.DETERMINATE,
                    ActionProgressDialogConfigKey.PROGRESS to 0,
                    ActionProgressDialogConfigKey.MAX_PROGRESS to 100,
                ),
            ),
            node(
                "loop",
                ActionNodeType.LOOP_REPEAT,
                "下载进度循环",
                config(
                    ActionLoopConfigKey.COUNT to 10,
                    ActionLoopConfigKey.MAX_ITERATIONS to 20,
                ),
            ),
            node(
                "delay",
                ActionNodeType.FLOW_DELAY,
                "模拟 100ms 耗时",
                config(ActionFlowConfigKey.DELAY_MILLIS to 100),
            ),
            node(
                "add_progress",
                ActionNodeType.MATH_ADD,
                "进度加 10",
                config(
                    ActionMathConfigKey.LEFT to "\${vars.progress}",
                    ActionMathConfigKey.RIGHT to 10,
                    ActionMathConfigKey.OUTPUT_KEY to "progress",
                ),
            ),
            node(
                "update_progress",
                ActionNodeType.UI_PROGRESS_UPDATE,
                "刷新进度 UI",
                config(
                    ActionProgressDialogConfigKey.MESSAGE to "已下载 \${vars.progress}%",
                    ActionProgressDialogConfigKey.PROGRESS to "\${vars.progress}",
                ),
            ),
            node(
                "loop_control",
                ActionNodeType.LOOP_NEXT,
                "继续下一次循环",
                config(ActionLoopConfigKey.LOOP_ID to "loop"),
            ),
            node(
                "dismiss_progress",
                ActionNodeType.UI_PROGRESS_DISMISS,
                "关闭进度",
                config(),
            ),
            node(
                "finish_toast",
                ActionNodeType.SHOW_TOAST,
                "完成提示",
                config(ActionToastConfigKey.MESSAGE to "模拟下载完成 (100%)！"),
            ),
            node("end", ActionNodeType.FLOW_END, "结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "init_progress"),
            edge("init_progress", ActionControlPortId.NEXT, "show_progress"),
            edge("show_progress", ActionControlPortId.SUCCESS, "loop"),
            edge("loop", ActionControlPortId.BODY, "delay"),
            edge("delay", ActionControlPortId.NEXT, "add_progress"),
            edge("add_progress", ActionControlPortId.NEXT, "update_progress"),
            edge("update_progress", ActionControlPortId.SUCCESS, "loop_control"),
            edge("loop", ActionControlPortId.COMPLETED, "dismiss_progress"),
            edge("dismiss_progress", ActionControlPortId.SUCCESS, "finish_toast"),
            edge("finish_toast", ActionControlPortId.SUCCESS, "end"),
        ),
    )

    /**
     * 进度弹窗并发双分支下载与合并工作流：
     * 展示进度 (0%) -> flow.parallel 分出两个并发任务分支：
     *   - 分支 1 (视频流)：耗时 600ms 后更新进度到 45% -> 汇入 flow.join
     *   - 分支 2 (音频流)：耗时 1200ms 后更新进度到 90% -> 汇入 flow.join
     * -> flow.join 汇合全部任务 -> 更新进度到 100% -> 关闭弹窗 -> Toast 提示。
     */
    private fun progressDialogParallelSample(): ActionWorkflow = workflow(
        id = "ui_progress_dialog_parallel",
        name = "测试：并发分支进度弹窗",
        description = "演示通过 flow.parallel 并发执行两条任务分支并异步刷新进度，最后在 flow.join 汇合后关闭弹窗。",
        capabilities = setOf(ActionCapability.PROGRESS_DIALOG),
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "show_progress",
                ActionNodeType.UI_PROGRESS_DIALOG,
                "展示并发下载弹窗",
                config(
                    ActionProgressDialogConfigKey.TITLE to "多资源并发下载",
                    ActionProgressDialogConfigKey.MESSAGE to "启动并发下载任务...",
                    ActionProgressDialogConfigKey.MODE to ActionProgressDialogMode.DETERMINATE,
                    ActionProgressDialogConfigKey.PROGRESS to 0,
                    ActionProgressDialogConfigKey.MAX_PROGRESS to 100,
                ),
            ),
            node("parallel", ActionNodeType.FLOW_PARALLEL, "启动并发任务"),
            // 分支 1：视频流
            node(
                "download_video",
                ActionNodeType.FLOW_DELAY,
                "下载视频流 (600ms)",
                config(ActionFlowConfigKey.DELAY_MILLIS to 600),
            ),
            node(
                "update_video",
                ActionNodeType.UI_PROGRESS_UPDATE,
                "更新视频下载进度",
                config(
                    ActionProgressDialogConfigKey.MESSAGE to "视频流下载完成 (45%)",
                    ActionProgressDialogConfigKey.PROGRESS to 45,
                ),
            ),
            // 分支 2：音频流
            node(
                "download_audio",
                ActionNodeType.FLOW_DELAY,
                "下载音频流 (1200ms)",
                config(ActionFlowConfigKey.DELAY_MILLIS to 1200),
            ),
            node(
                "update_audio",
                ActionNodeType.UI_PROGRESS_UPDATE,
                "更新音频下载进度",
                config(
                    ActionProgressDialogConfigKey.MESSAGE to "音频与字幕下载完成 (90%)",
                    ActionProgressDialogConfigKey.PROGRESS to 90,
                ),
            ),
            // 汇合
            node(
                "join",
                ActionNodeType.FLOW_JOIN,
                "等待并发分支汇合",
                config(
                    ActionFlowConfigKey.VALUES to JsonArray(emptyList()),
                    ActionFlowConfigKey.OUTPUT_KEY to "joinResult",
                ),
            ),
            node(
                "update_complete",
                ActionNodeType.UI_PROGRESS_UPDATE,
                "完成合并",
                config(
                    ActionProgressDialogConfigKey.MESSAGE to "资源下载与合并完成 (100%)",
                    ActionProgressDialogConfigKey.PROGRESS to 100,
                ),
            ),
            node(
                "delay_finish",
                ActionNodeType.FLOW_DELAY,
                "稍作停留",
                config(ActionFlowConfigKey.DELAY_MILLIS to 400),
            ),
            node(
                "dismiss_progress",
                ActionNodeType.UI_PROGRESS_DISMISS,
                "关闭进度",
                config(),
            ),
            node(
                "finish_toast",
                ActionNodeType.SHOW_TOAST,
                "完成提示",
                config(ActionToastConfigKey.MESSAGE to "全部并发资源下载合并完毕！"),
            ),
            node("end", ActionNodeType.FLOW_END, "结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "show_progress"),
            edge("show_progress", ActionControlPortId.SUCCESS, "parallel"),
            edge("parallel", ActionControlPortId.BRANCHES, "download_video"),
            edge("parallel", ActionControlPortId.BRANCHES, "download_audio"),
            edge("download_video", ActionControlPortId.NEXT, "update_video"),
            edge("update_video", ActionControlPortId.SUCCESS, "join"),
            edge("download_audio", ActionControlPortId.NEXT, "update_audio"),
            edge("update_audio", ActionControlPortId.SUCCESS, "join"),
            edge("join", ActionControlPortId.NEXT, "update_complete"),
            edge("update_complete", ActionControlPortId.SUCCESS, "delay_finish"),
            edge("delay_finish", ActionControlPortId.NEXT, "dismiss_progress"),
            edge("dismiss_progress", ActionControlPortId.SUCCESS, "finish_toast"),
            edge("finish_toast", ActionControlPortId.SUCCESS, "end"),
        ),
    )
}
