package com.xiaoyv.bangumi.features.workflows.business.samples

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionArrayConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionBilibiliConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCapability
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionConfirmConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHtmlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionImagePreviewConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionInputDialogConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionJsonConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionLoopConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionMathConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionOpenUrlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionSyncCookieConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionTextConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionToastConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionUrlConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionVideoPreviewConfigKey
import com.xiaoyv.bangumi.shared.libnative.System
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

/**
 * 复合多节点业务实操工作流样例集合。
 */
internal object BusinessSamples {
    val all: List<ActionWorkflow> = listOf(
        subjectTagsToToast(),
        searchBilibiliBangumiWithWebWbi(),
        searchMangaDexAndPreviewImages(),
        searchHanimeVideo(),
        complexWorkflowSample(),
    )

    /**
     * 请求 Bangumi 条目标签，逐项读取标签名并组合为逗号分隔文本。
     */
    private fun subjectTagsToToast(): ActionWorkflow = workflow(
        id = "subject_tags_to_toast",
        name = "测试：请求条目标签并循环拼接",
        description = "请求条目 tags，循环读取每个 tag.name，以逗号拼接后显示 Toast。",
        capabilities = setOf(ActionCapability.NETWORK, ActionCapability.NETWORK_LOCAL_COOKIE_ACCESS),
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "request_subject", ActionNodeType.HTTP_REQUEST, "请求条目 JSON", config(
                    ActionHttpConfigKey.URL to "https://next.bgm.tv/p1/subjects/633836",
                    ActionHttpConfigKey.METHOD to "GET",
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                )
            ),
            node(
                "extract_tags", ActionNodeType.JSON_EXTRACT, "提取 tags 数组", config(
                    ActionJsonConfigKey.SOURCE to "\${steps.request_subject.body}",
                    ActionJsonConfigKey.PATH to "$.tags",
                    ActionJsonConfigKey.OUTPUT_KEY to "tags",
                )
            ),
            node(
                "initialize_tag_names", ActionNodeType.SET_VARIABLE, "初始化标签文本", config(
                    ActionDataConfigKey.KEY to "tagNames",
                    ActionDataConfigKey.VALUE to "",
                )
            ),
            node(
                "loop_tags", ActionNodeType.LOOP_FOR_EACH, "遍历标签", config(
                    ActionLoopConfigKey.ITEMS to "\${vars.tags}",
                )
            ),
            node(
                "is_first_tag", ActionNodeType.CONDITION_EQUALS, "是否第一个标签", config(
                    ActionControlConfigKey.LEFT to "\${loop.index}",
                    ActionControlConfigKey.RIGHT to 0,
                )
            ),
            node(
                "set_first_tag", ActionNodeType.SET_VARIABLE, "写入第一个标签", config(
                    ActionDataConfigKey.KEY to "tagNames",
                    ActionDataConfigKey.VALUE to "\${loop.item.name}",
                )
            ),
            node("append_tag", ActionNodeType.TEXT_JOIN, "追加标签名称", buildJsonObject {
                put(ActionTextConfigKey.VALUES, JsonArray(listOf(JsonPrimitive("\${vars.tagNames}"), JsonPrimitive("\${loop.item.name}"))))
                put(ActionTextConfigKey.SEPARATOR, JsonPrimitive(","))
                put(ActionTextConfigKey.OUTPUT_KEY, JsonPrimitive("tagNames"))
            }),
            node("next_tag", ActionNodeType.LOOP_NEXT, "继续遍历标签", config(ActionLoopConfigKey.LOOP_ID to "loop_tags")),
            node(
                "show_tags", ActionNodeType.SHOW_TOAST, "显示标签", config(
                    ActionToastConfigKey.MESSAGE to "条目标签：\${vars.tagNames}",
                )
            ),
            node("end", ActionNodeType.FLOW_END, "结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "request_subject"),
            edge("request_subject", ActionControlPortId.SUCCESS, "extract_tags"),
            edge("extract_tags", ActionControlPortId.NEXT, "initialize_tag_names"),
            edge("initialize_tag_names", ActionControlPortId.NEXT, "loop_tags"),
            edge("loop_tags", ActionControlPortId.BODY, "is_first_tag"),
            edge("is_first_tag", ActionControlPortId.TRUE, "set_first_tag"),
            edge("is_first_tag", ActionControlPortId.FALSE, "append_tag"),
            edge("set_first_tag", ActionControlPortId.NEXT, "next_tag"),
            edge("append_tag", ActionControlPortId.NEXT, "next_tag"),
            edge("loop_tags", ActionControlPortId.COMPLETED, "show_tags"),
            edge("show_tags", ActionControlPortId.SUCCESS, "end"),
        ),
    )

    /**
     * 通过内置的 Bilibili WBI 签名节点搜索 Bilibili 番剧后打开首个结果。
     */
    private fun searchBilibiliBangumiWithWebWbi(): ActionWorkflow = workflow(
        id = "search_bilibili_bangumi_and_open",
        name = "测试：纯工作流 WBI 签名并搜索 Bilibili 番剧",
        description = "输入关键词，调用 Bilibili 签名节点生成 WBI 签名，请求番剧搜索接口并打开首个结果。",
        capabilities = setOf(
            ActionCapability.INPUT_DIALOG,
            ActionCapability.NETWORK,
            ActionCapability.NETWORK_LOCAL_COOKIE_ACCESS,
            ActionCapability.OPEN_EXTERNAL_URL,
        ),
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "input_keyword",
                ActionNodeType.UI_INPUT_DIALOG,
                "输入搜索关键词",
                config(
                    ActionInputDialogConfigKey.TITLE to "搜索 Bilibili 番剧",
                    ActionInputDialogConfigKey.SUBTITLE to "请输入番剧名称",
                    ActionInputDialogConfigKey.OUTPUT_KEY to "keyword",
                ),
            ),
            node(
                "sanitize_keyword",
                ActionNodeType.TEXT_REPLACE_REGEX,
                "移除 WBI 禁止字符",
                config(
                    ActionTextConfigKey.TEXT to "\${steps.input_keyword.keyword}",
                    ActionTextConfigKey.PATTERN to "[!'()*]",
                    ActionTextConfigKey.REPLACEMENT to "",
                    ActionTextConfigKey.OUTPUT_KEY to "sanitizedKeyword",
                ),
            ),
            node(
                "init_bilibili_cookie",
                ActionNodeType.HTTP_REQUEST,
                "初始化 Bilibili Cookie",
                config(
                    ActionHttpConfigKey.URL to "https://www.bilibili.com/",
                    ActionHttpConfigKey.METHOD to "HEAD",
                    ActionHttpConfigKey.HEADERS to JsonObject(
                        mapOf("User-Agent" to JsonPrimitive(BILIBILI_WEB_USER_AGENT)),
                    ),
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                ),
            ),
            node(
                "request_bilibili_spi",
                ActionNodeType.HTTP_REQUEST,
                "请求 Bilibili 设备标识",
                config(
                    ActionHttpConfigKey.URL to "https://api.bilibili.com/x/frontend/finger/spi",
                    ActionHttpConfigKey.METHOD to "GET",
                    ActionHttpConfigKey.HEADERS to JsonObject(
                        mapOf(
                            "Referer" to JsonPrimitive("https://www.bilibili.com/"),
                            "User-Agent" to JsonPrimitive(BILIBILI_WEB_USER_AGENT),
                        ),
                    ),
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                ),
            ),
            node(
                "extract_bilibili_buvid4",
                ActionNodeType.JSON_EXTRACT,
                "提取 Bilibili buvid4",
                config(
                    ActionJsonConfigKey.SOURCE to "\${steps.request_bilibili_spi.body}",
                    ActionJsonConfigKey.PATH to "$.data.b_4",
                    ActionJsonConfigKey.OUTPUT_KEY to "buvid4",
                ),
            ),
            node(
                "request_wbi_keys",
                ActionNodeType.HTTP_REQUEST,
                "请求 WBI 实时密钥",
                config(
                    ActionHttpConfigKey.URL to "https://api.bilibili.com/x/web-interface/nav",
                    ActionHttpConfigKey.METHOD to "GET",
                    ActionHttpConfigKey.HEADERS to JsonObject(
                        mapOf(
                            "Cookie" to JsonPrimitive("buvid4=\${vars.buvid4}"),
                            "Referer" to JsonPrimitive("https://www.bilibili.com/"),
                            "User-Agent" to JsonPrimitive(BILIBILI_WEB_USER_AGENT),
                        ),
                    ),
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                ),
            ),
            node(
                "extract_img_url",
                ActionNodeType.JSON_EXTRACT,
                "提取 img key 地址",
                config(
                    ActionJsonConfigKey.SOURCE to "\${steps.request_wbi_keys.body}",
                    ActionJsonConfigKey.PATH to "$.data.wbi_img.img_url",
                    ActionJsonConfigKey.OUTPUT_KEY to "imgUrl",
                ),
            ),
            node(
                "extract_sub_url",
                ActionNodeType.JSON_EXTRACT,
                "提取 sub key 地址",
                config(
                    ActionJsonConfigKey.SOURCE to "\${steps.request_wbi_keys.body}",
                    ActionJsonConfigKey.PATH to "$.data.wbi_img.sub_url",
                    ActionJsonConfigKey.OUTPUT_KEY to "subUrl",
                ),
            ),
            node(
                "extract_img_key",
                ActionNodeType.TEXT_REGEX_MATCH,
                "提取 img key",
                config(
                    ActionTextConfigKey.TEXT to "\${vars.imgUrl}",
                    ActionTextConfigKey.PATTERN to "/([^/]+)\\.png\$",
                    ActionTextConfigKey.OUTPUT_KEY to "imgKeyGroups",
                ),
            ),
            node(
                "extract_sub_key",
                ActionNodeType.TEXT_REGEX_MATCH,
                "提取 sub key",
                config(
                    ActionTextConfigKey.TEXT to "\${vars.subUrl}",
                    ActionTextConfigKey.PATTERN to "/([^/]+)\\.png\$",
                    ActionTextConfigKey.OUTPUT_KEY to "subKeyGroups",
                ),
            ),
            node(
                "bilibili_sign_url",
                ActionNodeType.BILIBILI_SIGN_URL,
                "Bilibili WBI 链接加签",
                config(
                    ActionBilibiliConfigKey.URL to "https://api.bilibili.com/x/web-interface/wbi/search/type?search_type=media_bangumi&keyword=\${vars.sanitizedKeyword}",
                    ActionBilibiliConfigKey.IMG_KEY to "\${vars.imgKeyGroups.1}",
                    ActionBilibiliConfigKey.SUB_KEY to "\${vars.subKeyGroups.1}",
                    ActionBilibiliConfigKey.OUTPUT_KEY to "signedUrl",
                ),
            ),
            node(
                "search_bilibili",
                ActionNodeType.HTTP_REQUEST,
                "请求 Bilibili 番剧搜索接口",
                config(
                    ActionHttpConfigKey.URL to "\${steps.bilibili_sign_url.signedUrl}",
                    ActionHttpConfigKey.METHOD to "GET",
                    ActionHttpConfigKey.HEADERS to JsonObject(
                        mapOf(
                            "Referer" to JsonPrimitive("https://www.bilibili.com/"),
                            "User-Agent" to JsonPrimitive(BILIBILI_WEB_USER_AGENT),
                        ),
                    ),
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                ),
            ),
            node(
                "extract_first_result_url",
                ActionNodeType.JSON_EXTRACT,
                "提取首个结果链接",
                config(
                    ActionJsonConfigKey.SOURCE to "\${steps.search_bilibili.body}",
                    ActionJsonConfigKey.PATH to "$.data.result.0.url",
                    ActionJsonConfigKey.OUTPUT_KEY to "resultUrl",
                ),
            ),
            node(
                "is_result_url_null",
                ActionNodeType.CONDITION_IS_NULL,
                "是否找到结果链接",
                config(
                    ActionControlConfigKey.VALUE to "\${steps.extract_first_result_url.resultUrl}",
                ),
            ),
            node(
                "open_result",
                ActionNodeType.OPEN_EXTERNAL_URL,
                "打开首个搜索结果",
                config(ActionOpenUrlConfigKey.URL to "\${steps.extract_first_result_url.resultUrl}"),
            ),
            node(
                "show_no_result",
                ActionNodeType.SHOW_TOAST,
                "提示未找到结果",
                config(ActionToastConfigKey.MESSAGE to "未找到匹配的 Bilibili 番剧结果"),
            ),
            node("end_after_open", ActionNodeType.FLOW_END, "打开后结束"),
            node("end_after_no_result", ActionNodeType.FLOW_END, "无结果后结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "input_keyword"),
            edge("input_keyword", ActionControlPortId.SUCCESS, "sanitize_keyword"),
            edge("sanitize_keyword", ActionControlPortId.NEXT, "init_bilibili_cookie"),
            edge("init_bilibili_cookie", ActionControlPortId.SUCCESS, "request_bilibili_spi"),
            edge("request_bilibili_spi", ActionControlPortId.SUCCESS, "extract_bilibili_buvid4"),
            edge("extract_bilibili_buvid4", ActionControlPortId.NEXT, "request_wbi_keys"),
            edge("request_wbi_keys", ActionControlPortId.SUCCESS, "extract_img_url"),
            edge("extract_img_url", ActionControlPortId.NEXT, "extract_sub_url"),
            edge("extract_sub_url", ActionControlPortId.NEXT, "extract_img_key"),
            edge("extract_img_key", ActionControlPortId.NEXT, "extract_sub_key"),
            edge("extract_sub_key", ActionControlPortId.NEXT, "bilibili_sign_url"),
            edge("bilibili_sign_url", ActionControlPortId.NEXT, "search_bilibili"),
            edge("search_bilibili", ActionControlPortId.SUCCESS, "extract_first_result_url"),
            edge("extract_first_result_url", ActionControlPortId.NEXT, "is_result_url_null"),
            edge("is_result_url_null", ActionControlPortId.TRUE, "show_no_result"),
            edge("is_result_url_null", ActionControlPortId.FALSE, "open_result"),
            edge("open_result", ActionControlPortId.SUCCESS, "end_after_open"),
            edge("show_no_result", ActionControlPortId.SUCCESS, "end_after_no_result"),
        ),
    )

    /**
     * 搜索 Hanime 视频，提取最高画质播放源并调起视频预览播放。
     */
    private fun searchHanimeVideo(): ActionWorkflow = workflow(
        id = "hanime_search_video",
        name = "测试：Hanime 视频检索与播放",
        description = "搜索 Hanime 视频，提取最高画质播放源并调起视频预览播放。",
        capabilities = setOf(
            ActionCapability.INPUT_DIALOG,
            ActionCapability.NETWORK,
            ActionCapability.NETWORK_LOCAL_COOKIE_ACCESS,
            ActionCapability.NETWORK_COOKIE_SYNC,
            ActionCapability.VIDEO_PREVIEW,
        ),
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "input_keyword",
                ActionNodeType.UI_INPUT_DIALOG,
                "输入关键词",
                config(
                    ActionInputDialogConfigKey.TITLE to "搜索 Hanime 视频",
                    ActionInputDialogConfigKey.SUBTITLE to "请输入视频名称关键词",
                    ActionInputDialogConfigKey.OUTPUT_KEY to "keyword",
                ),
            ),
            node(
                "init_hanime_cookie",
                ActionNodeType.HTTP_REQUEST,
                "初始化 Hanime Cookie",
                config(
                    ActionHttpConfigKey.URL to "https://hanime1.me/",
                    ActionHttpConfigKey.METHOD to "HEAD",
                    ActionHttpConfigKey.HEADERS to JsonObject(
                        mapOf(
                            "User-Agent" to JsonPrimitive(System.userAgent()),
                        ),
                    ),
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                ),
            ),
            node(
                "is_hanime_cookie_forbidden",
                ActionNodeType.HTTP_STATUS,
                "Cookie 初始化是否被拒绝",
                config(
                    ActionControlConfigKey.STATUS_CODE to "\${steps.init_hanime_cookie.statusCode}",
                    ActionControlConfigKey.MIN_STATUS_CODE to 403,
                    ActionControlConfigKey.MAX_STATUS_CODE to 403,
                ),
            ),
            node(
                "sync_hanime_cookie",
                ActionNodeType.SYNC_COOKIE,
                "同步 Hanime Cookie",
                config(
                    ActionSyncCookieConfigKey.URL to "https://hanime1.me/",
                    ActionSyncCookieConfigKey.TITLE to "Hanime 需要验证",
                    ActionSyncCookieConfigKey.USER_AGENT to System.userAgent(),
                ),
            ),
            node(
                "search_hanime",
                ActionNodeType.HTTP_REQUEST,
                "请求 Hanime 搜索接口",
                config(
                    ActionHttpConfigKey.URL to "https://hanime1.me/search?query=\${steps.input_keyword.keyword}",
                    ActionHttpConfigKey.METHOD to "GET",
                    ActionHttpConfigKey.HEADERS to JsonObject(
                        mapOf(
                            "Referer" to JsonPrimitive("https://hanime1.me/"),
                            "User-Agent" to JsonPrimitive(System.userAgent()),
                        ),
                    ),
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                ),
            ),
            node(
                "parse_search_page",
                ActionNodeType.HTML_PARSE,
                "解析搜索结果页面",
                config(
                    ActionHtmlConfigKey.HTML to "\${steps.search_hanime.body.html}",
                    ActionHtmlConfigKey.OUTPUT_KEY to "doc",
                ),
            ),
            node(
                "extract_first_video_element",
                ActionNodeType.HTML_SELECT_FIRST,
                "提取首个视频条目元素",
                config(
                    ActionHtmlConfigKey.SOURCE to "\${steps.parse_search_page.doc}",
                    ActionHtmlConfigKey.SELECTOR to ".horizontal-card > a:not([target=\"_blank\"])",
                    ActionHtmlConfigKey.OUTPUT_KEY to "videoElement",
                ),
            ),
            node(
                "extract_first_video_href",
                ActionNodeType.HTML_ATTR,
                "提取首个视频条目链接",
                config(
                    ActionHtmlConfigKey.SOURCE to "\${steps.extract_first_video_element.videoElement}",
                    ActionHtmlConfigKey.ATTRIBUTE to "href",
                    ActionHtmlConfigKey.OUTPUT_KEY to "videoHref",
                ),
            ),
            node(
                "is_href_empty",
                ActionNodeType.CONDITION_IS_EMPTY,
                "校验搜索结果是否为空",
                config(
                    ActionControlConfigKey.VALUE to "\${steps.extract_first_video_href.videoHref}",
                ),
            ),
            node(
                "build_detail_url",
                ActionNodeType.URL_BUILD,
                "构建详情页完整 URL",
                config(
                    ActionUrlConfigKey.BASE_URL to "\${steps.extract_first_video_href.videoHref}",
                    ActionUrlConfigKey.OUTPUT_KEY to "detailUrl",
                ),
            ),
            node(
                "request_video_detail",
                ActionNodeType.HTTP_REQUEST,
                "请求视频详情页",
                config(
                    ActionHttpConfigKey.URL to "\${steps.build_detail_url.detailUrl}",
                    ActionHttpConfigKey.METHOD to "GET",
                    ActionHttpConfigKey.HEADERS to JsonObject(
                        mapOf(
                            "Referer" to JsonPrimitive("https://hanime1.me/"),
                            "User-Agent" to JsonPrimitive(System.userAgent()),
                        ),
                    ),
                    ActionHttpConfigKey.USE_LOCAL_COOKIE_STORAGE to true,
                ),
            ),
            node(
                "parse_detail_page",
                ActionNodeType.HTML_PARSE,
                "解析详情页面文档",
                config(
                    ActionHtmlConfigKey.HTML to "\${steps.request_video_detail.body.html}",
                    ActionHtmlConfigKey.OUTPUT_KEY to "detailDoc",
                ),
            ),
            node(
                "select_sources",
                ActionNodeType.HTML_SELECT,
                "选择全部播放源标签",
                config(
                    ActionHtmlConfigKey.SOURCE to "\${steps.parse_detail_page.detailDoc}",
                    ActionHtmlConfigKey.SELECTOR to "source",
                    ActionHtmlConfigKey.OUTPUT_KEY to "sourceElements",
                ),
            ),
            node(
                "extract_all_sources",
                ActionNodeType.HTML_MAP,
                "映射播放源地址数组",
                config(
                    ActionHtmlConfigKey.SOURCE to "\${steps.select_sources.sourceElements}",
                    ActionHtmlConfigKey.OPERATION to "attr",
                    ActionHtmlConfigKey.ATTRIBUTE to "src",
                    ActionHtmlConfigKey.OUTPUT_KEY to "sources",
                ),
            ),
            node(
                "get_last_source",
                ActionNodeType.ARRAY_LAST,
                "提取最高画质播放源",
                config(
                    ActionArrayConfigKey.VALUES to "\${steps.extract_all_sources.sources}",
                    ActionArrayConfigKey.OUTPUT_KEY to "lastSourceUrl",
                ),
            ),
            node(
                "is_source_null",
                ActionNodeType.CONDITION_IS_NULL,
                "校验播放源是否为空",
                config(
                    ActionControlConfigKey.VALUE to "\${steps.get_last_source.lastSourceUrl}",
                ),
            ),
            node(
                "preview_video",
                ActionNodeType.VIDEO_PREVIEW,
                "播放预览视频",
                config(
                    ActionVideoPreviewConfigKey.URL to "\${steps.get_last_source.lastSourceUrl}",
                    ActionVideoPreviewConfigKey.HEADERS to buildJsonObject {
                        put("Referer", JsonPrimitive("\${steps.build_detail_url.detailUrl}"))
                        put("User-Agent", JsonPrimitive(System.userAgent()))
                    },
                ),
            ),
            node(
                "show_no_video",
                ActionNodeType.SHOW_TOAST,
                "提示未找到视频",
                config(ActionToastConfigKey.MESSAGE to "未找到匹配的 Hanime 视频"),
            ),
            node(
                "show_no_source",
                ActionNodeType.SHOW_TOAST,
                "提示未找到播放源",
                config(ActionToastConfigKey.MESSAGE to "未找到有效的视频播放源"),
            ),
            node("end_after_preview", ActionNodeType.FLOW_END, "播放后结束"),
            node("end_after_no_video", ActionNodeType.FLOW_END, "无视频后结束"),
            node("end_after_no_source", ActionNodeType.FLOW_END, "无源后结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "input_keyword"),
            edge("input_keyword", ActionControlPortId.SUCCESS, "init_hanime_cookie"),
            edge("init_hanime_cookie", ActionControlPortId.SUCCESS, "is_hanime_cookie_forbidden"),
            edge("is_hanime_cookie_forbidden", ActionControlPortId.TRUE, "sync_hanime_cookie"),
            edge("is_hanime_cookie_forbidden", ActionControlPortId.FALSE, "search_hanime"),
            edge("sync_hanime_cookie", ActionControlPortId.SUCCESS, "search_hanime"),
            edge("sync_hanime_cookie", ActionControlPortId.FAILURE, "search_hanime"),
            edge("search_hanime", ActionControlPortId.SUCCESS, "parse_search_page"),
            edge("parse_search_page", ActionControlPortId.NEXT, "extract_first_video_element"),
            edge("extract_first_video_element", ActionControlPortId.NEXT, "extract_first_video_href"),
            edge("extract_first_video_href", ActionControlPortId.NEXT, "is_href_empty"),
            edge("is_href_empty", ActionControlPortId.TRUE, "show_no_video"),
            edge("is_href_empty", ActionControlPortId.FALSE, "build_detail_url"),
            edge("build_detail_url", ActionControlPortId.NEXT, "request_video_detail"),
            edge("request_video_detail", ActionControlPortId.SUCCESS, "parse_detail_page"),
            edge("parse_detail_page", ActionControlPortId.NEXT, "select_sources"),
            edge("select_sources", ActionControlPortId.NEXT, "extract_all_sources"),
            edge("extract_all_sources", ActionControlPortId.NEXT, "get_last_source"),
            edge("get_last_source", ActionControlPortId.NEXT, "is_source_null"),
            edge("is_source_null", ActionControlPortId.TRUE, "show_no_source"),
            edge("is_source_null", ActionControlPortId.FALSE, "preview_video"),
            edge("preview_video", ActionControlPortId.SUCCESS, "end_after_preview"),
            edge("preview_video", ActionControlPortId.FAILURE, "end_after_preview"),
            edge("show_no_video", ActionControlPortId.SUCCESS, "end_after_no_video"),
            edge("show_no_source", ActionControlPortId.SUCCESS, "end_after_no_source"),
        ),
    )

    /**
     * 搜索 MangaDex 漫画、获取章节高清图片 URL 列表并调起图片预览。
     */
    private fun searchMangaDexAndPreviewImages(): ActionWorkflow = workflow(
        id = "search_mangadex_manga_and_preview",
        name = "测试：MangaDex 漫画搜索与章节图片预览",
        description = "搜索 MangaDex 漫画，获取目标章节并拼接高清图片 URL 列表后调起全屏图片预览。",
        capabilities = setOf(
            ActionCapability.INPUT_DIALOG,
            ActionCapability.NETWORK,
            ActionCapability.IMAGE_PREVIEW,
        ),
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "开始"),
            node(
                "input_keyword",
                ActionNodeType.UI_INPUT_DIALOG,
                "输入漫画关键词",
                config(
                    ActionInputDialogConfigKey.TITLE to "搜索 MangaDex 漫画",
                    ActionInputDialogConfigKey.SUBTITLE to "请输入漫画名称关键词",
                    ActionInputDialogConfigKey.OUTPUT_KEY to "keyword",
                ),
            ),
            node(
                "search_manga",
                ActionNodeType.HTTP_REQUEST,
                "搜索 MangaDex 漫画",
                config(
                    ActionHttpConfigKey.URL to "https://api.mangadex.org/manga?title=\${steps.input_keyword.keyword}",
                    ActionHttpConfigKey.METHOD to "GET",
                ),
            ),
            node(
                "extract_manga_id",
                ActionNodeType.JSON_EXTRACT,
                "提取漫画 ID",
                config(
                    ActionJsonConfigKey.SOURCE to "\${steps.search_manga.body}",
                    ActionJsonConfigKey.PATH to "$.data.0.id",
                    ActionJsonConfigKey.OUTPUT_KEY to "mangaId",
                ),
            ),
            node(
                "is_manga_found",
                ActionNodeType.CONDITION_IS_NULL,
                "是否找到漫画",
                config(
                    ActionControlConfigKey.VALUE to "\${vars.mangaId}",
                ),
            ),
            node(
                "get_chapters",
                ActionNodeType.HTTP_REQUEST,
                "获取漫画章节列表",
                config(
                    ActionHttpConfigKey.URL to "https://api.mangadex.org/manga/\${vars.mangaId}/feed",
                    ActionHttpConfigKey.METHOD to "GET",
                ),
            ),
            node(
                "extract_chapter_id",
                ActionNodeType.JSON_EXTRACT,
                "提取首个章节 ID",
                config(
                    ActionJsonConfigKey.SOURCE to "\${steps.get_chapters.body}",
                    ActionJsonConfigKey.PATH to "$.data.0.id",
                    ActionJsonConfigKey.OUTPUT_KEY to "chapterId",
                ),
            ),
            node(
                "get_chapter_server",
                ActionNodeType.HTTP_REQUEST,
                "获取章节服务器信息",
                config(
                    ActionHttpConfigKey.URL to "https://api.mangadex.org/at-home/server/\${vars.chapterId}",
                    ActionHttpConfigKey.METHOD to "GET",
                ),
            ),
            node(
                "extract_base_url",
                ActionNodeType.JSON_EXTRACT,
                "提取基础 URL",
                config(
                    ActionJsonConfigKey.SOURCE to "\${steps.get_chapter_server.body}",
                    ActionJsonConfigKey.PATH to "$.baseUrl",
                    ActionJsonConfigKey.OUTPUT_KEY to "baseUrl",
                ),
            ),
            node(
                "extract_hash",
                ActionNodeType.JSON_EXTRACT,
                "提取章节 Hash",
                config(
                    ActionJsonConfigKey.SOURCE to "\${steps.get_chapter_server.body}",
                    ActionJsonConfigKey.PATH to "$.chapter.hash",
                    ActionJsonConfigKey.OUTPUT_KEY to "hash",
                ),
            ),
            node(
                "extract_filenames",
                ActionNodeType.JSON_EXTRACT,
                "提取图片文件名列表",
                config(
                    ActionJsonConfigKey.SOURCE to "\${steps.get_chapter_server.body}",
                    ActionJsonConfigKey.PATH to "$.chapter.data",
                    ActionJsonConfigKey.OUTPUT_KEY to "filenames",
                ),
            ),
            node(
                "init_image_list",
                ActionNodeType.SET_VARIABLE,
                "初始化图片 URL 列表",
                config(
                    ActionDataConfigKey.KEY to "imageUrlList",
                    ActionDataConfigKey.VALUE to JsonArray(emptyList()),
                ),
            ),
            node(
                "loop_filenames",
                ActionNodeType.LOOP_FOR_EACH,
                "遍历图片文件名",
                config(
                    ActionLoopConfigKey.ITEMS to "\${vars.filenames}",
                ),
            ),
            node(
                "build_image_url",
                ActionNodeType.TEMPLATE,
                "拼接图片全路径",
                config(
                    ActionDataConfigKey.TEMPLATE to "\${vars.baseUrl}/data/\${vars.hash}/\${loop.item}",
                    ActionDataConfigKey.OUTPUT_KEY to "imageUrl",
                ),
            ),
            node(
                "append_image_url",
                ActionNodeType.ARRAY_APPEND,
                "追加到图片列表",
                config(
                    ActionArrayConfigKey.VALUES to "\${vars.imageUrlList}",
                    ActionArrayConfigKey.VALUE to "\${vars.imageUrl}",
                    ActionArrayConfigKey.OUTPUT_KEY to "imageUrlList",
                ),
            ),
            node(
                "next_filename",
                ActionNodeType.LOOP_NEXT,
                "继续下一张图片",
                config(ActionLoopConfigKey.LOOP_ID to "loop_filenames"),
            ),
            node(
                "preview_images",
                ActionNodeType.IMAGE_PREVIEW,
                "打开漫画图片预览",
                config(
                    ActionImagePreviewConfigKey.INDEX to 0,
                    ActionImagePreviewConfigKey.IMAGES to "\${vars.imageUrlList}",
                ),
            ),
            node(
                "show_no_manga",
                ActionNodeType.SHOW_TOAST,
                "提示未找到漫画",
                config(ActionToastConfigKey.MESSAGE to "未找到匹配的 MangaDex 漫画"),
            ),
            node("end_after_preview", ActionNodeType.FLOW_END, "预览后结束"),
            node("end_after_no_manga", ActionNodeType.FLOW_END, "无漫画后结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "input_keyword"),
            edge("input_keyword", ActionControlPortId.SUCCESS, "search_manga"),
            edge("search_manga", ActionControlPortId.SUCCESS, "extract_manga_id"),
            edge("extract_manga_id", ActionControlPortId.NEXT, "is_manga_found"),
            edge("is_manga_found", ActionControlPortId.TRUE, "show_no_manga"),
            edge("is_manga_found", ActionControlPortId.FALSE, "get_chapters"),
            edge("get_chapters", ActionControlPortId.SUCCESS, "extract_chapter_id"),
            edge("extract_chapter_id", ActionControlPortId.NEXT, "get_chapter_server"),
            edge("get_chapter_server", ActionControlPortId.SUCCESS, "extract_base_url"),
            edge("extract_base_url", ActionControlPortId.NEXT, "extract_hash"),
            edge("extract_hash", ActionControlPortId.NEXT, "extract_filenames"),
            edge("extract_filenames", ActionControlPortId.NEXT, "init_image_list"),
            edge("init_image_list", ActionControlPortId.NEXT, "loop_filenames"),
            edge("loop_filenames", ActionControlPortId.BODY, "build_image_url"),
            edge("build_image_url", ActionControlPortId.NEXT, "append_image_url"),
            edge("append_image_url", ActionControlPortId.NEXT, "next_filename"),
            edge("loop_filenames", ActionControlPortId.COMPLETED, "preview_images"),
            edge("preview_images", ActionControlPortId.SUCCESS, "end_after_preview"),
            edge("show_no_manga", ActionControlPortId.SUCCESS, "end_after_no_manga"),
        ),
    )

    private fun complexWorkflowSample(): ActionWorkflow = workflow(
        id = "complex_dag_search",
        name = "超级DAG复杂综合工作流",
        description = "全功能综合演示：包含输入弹窗 -> Top级并发双分支(URL构造+UUID生成) -> Top级合流 -> 数组遍历(Loop.for_each) -> 条件过滤(Control.if) -> 循环内双分支(名称格式化+Math积分计算) -> 循环内合流 -> Toast提示 -> 全局确认弹窗",
        capabilities = setOf(
            ActionCapability.INPUT_DIALOG,
            ActionCapability.CONFIRM_DIALOG,
        ),
        nodes = listOf(
            node("start", ActionNodeType.FLOW_START, "流程起始点"),
            node(
                "input_keyword",
                ActionNodeType.UI_INPUT_DIALOG,
                "关键词输入",
                config(
                    ActionInputDialogConfigKey.TITLE to "番剧搜索",
                    ActionInputDialogConfigKey.SUBTITLE to "请输入要查询的动画名称",
                    ActionInputDialogConfigKey.OUTPUT_KEY to "keyword",
                ),
            ),
            node(
                "fork_start",
                ActionNodeType.SET_VARIABLE,
                "保存搜索词",
                config(
                    ActionDataConfigKey.KEY to "search_kw",
                    ActionDataConfigKey.VALUE to "\${steps.input_keyword.keyword}",
                ),
            ),
            node(
                "branch_url",
                ActionNodeType.URL_BUILD,
                "分支A: 构造API接口",
                config(
                    ActionUrlConfigKey.BASE_URL to "https://api.bgm.tv/search/subject/\${vars.search_kw}",
                    ActionUrlConfigKey.OUTPUT_KEY to "api_url",
                ),
            ),
            node(
                "branch_meta",
                ActionNodeType.DATA_UUID,
                "分支B: 生成TraceID",
                config(
                    ActionDataConfigKey.OUTPUT_KEY to "req_id",
                ),
            ),
            node(
                "join_meta",
                ActionNodeType.TEMPLATE,
                "Top级分支合流",
                config(
                    ActionDataConfigKey.TEMPLATE to "请求摘要 [TraceID=\${steps.branch_meta.req_id}]: \${steps.branch_url.api_url}",
                    ActionDataConfigKey.OUTPUT_KEY to "summary",
                ),
            ),
            node(
                "create_array",
                ActionNodeType.ARRAY_CREATE,
                "初始化动画列表",
                buildJsonObject {
                    put(
                        ActionArrayConfigKey.VALUES,
                        buildJsonArray {
                            add(JsonPrimitive("孤独摇滚！"))
                            add(JsonPrimitive("轻音少女"))
                            add(JsonPrimitive("命运石之门"))
                            add(JsonPrimitive("攻壳机动队"))
                        },
                    )
                    put(ActionArrayConfigKey.OUTPUT_KEY, JsonPrimitive("items"))
                },
            ),
            node(
                "loop_items",
                ActionNodeType.LOOP_FOR_EACH,
                "遍历动画列表",
                config(
                    ActionLoopConfigKey.ITEMS to "\${steps.create_array.items}",
                    ActionLoopConfigKey.MAX_ITERATIONS to 10,
                ),
            ),
            node(
                "check_condition",
                ActionNodeType.CONDITION_IF,
                "条件过滤 (\${loop.index > 0})",
                config(
                    ActionControlConfigKey.CONDITION to "\${loop.index > 0}",
                ),
            ),
            node(
                "loop_fork_left",
                ActionNodeType.TEMPLATE,
                "循环内分支A: 格式化名称",
                config(
                    ActionDataConfigKey.TEMPLATE to "[No.\${loop.index + 1}] \${loop.item}",
                    ActionDataConfigKey.OUTPUT_KEY to "formatted_name",
                ),
            ),
            node(
                "loop_fork_right",
                ActionNodeType.MATH_ADD,
                "循环内分支B: 计算匹配分",
                config(
                    ActionMathConfigKey.LEFT to "\${loop.index}",
                    ActionMathConfigKey.RIGHT to 90,
                    ActionMathConfigKey.OUTPUT_KEY to "tag_score",
                ),
            ),
            node(
                "loop_join",
                ActionNodeType.TEMPLATE,
                "循环内分支合流",
                config(
                    ActionDataConfigKey.TEMPLATE to "\${steps.loop_fork_left.formatted_name} (匹配度: \${steps.loop_fork_right.tag_score}分)",
                    ActionDataConfigKey.OUTPUT_KEY to "item_desc",
                ),
            ),
            node(
                "show_item_toast",
                ActionNodeType.SHOW_TOAST,
                "显示列表项Toast",
                config(
                    ActionToastConfigKey.MESSAGE to "\${steps.loop_join.item_desc}",
                ),
            ),
            node(
                "loop_next",
                ActionNodeType.LOOP_NEXT,
                "进入下一次迭代",
                config(
                    ActionLoopConfigKey.LOOP_ID to "loop_items",
                ),
            ),
            node(
                "confirm_dialog",
                ActionNodeType.UI_CONFIRM,
                "执行完毕确认",
                config(
                    ActionConfirmConfigKey.TITLE to "工作流执行成功",
                    ActionConfirmConfigKey.MESSAGE to "\${steps.join_meta.summary}",
                ),
            ),
            node("end", ActionNodeType.FLOW_END, "流程结束"),
        ),
        edges = listOf(
            edge("start", ActionControlPortId.NEXT, "input_keyword"),
            edge("input_keyword", ActionControlPortId.SUCCESS, "fork_start"),

            // Top级多路分叉 Fan-Out
            edge("fork_start", ActionControlPortId.NEXT, "branch_url"),
            edge("fork_start", ActionControlPortId.NEXT, "branch_meta"),

            // Top级多路合流 Fan-In
            edge("branch_url", ActionControlPortId.NEXT, "join_meta"),
            edge("branch_meta", ActionControlPortId.NEXT, "join_meta"),

            edge("join_meta", ActionControlPortId.NEXT, "create_array"),
            edge("create_array", ActionControlPortId.NEXT, "loop_items"),

            // 循环体控制
            edge("loop_items", ActionControlPortId.BODY, "check_condition"),

            // 条件判断
            edge("check_condition", ActionControlPortId.TRUE, "loop_fork_left"),
            edge("check_condition", ActionControlPortId.TRUE, "loop_fork_right"),
            edge("check_condition", ActionControlPortId.FALSE, "loop_next"),

            // 循环内多路合流 Fan-In
            edge("loop_fork_left", ActionControlPortId.NEXT, "loop_join"),
            edge("loop_fork_right", ActionControlPortId.NEXT, "loop_join"),

            edge("loop_join", ActionControlPortId.NEXT, "show_item_toast"),
            edge("show_item_toast", ActionControlPortId.SUCCESS, "loop_next"),

            // 循环完成
            edge("loop_items", ActionControlPortId.COMPLETED, "confirm_dialog"),
            edge("confirm_dialog", ActionControlPortId.SUCCESS, "end"),
            edge("confirm_dialog", ActionControlPortId.FAILURE, "end"),
        ),
    )
}
