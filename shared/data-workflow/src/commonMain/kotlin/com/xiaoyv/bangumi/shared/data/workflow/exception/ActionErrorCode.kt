package com.xiaoyv.bangumi.shared.data.workflow.exception

/**
 * 工作流引擎、校验器与编解码器使用的统一错误代码（Code）与默认中文描述（Message）集中注册表。
 */
object ActionErrorCode {
    // ---------------------------------------------------------------------------------------------
    // 工作流引擎执行运行时 Error Code 与 Error Message
    // ---------------------------------------------------------------------------------------------
    const val INVALID_WORKFLOW = "invalid_workflow"
    const val INVALID_WORKFLOW_MSG = "工作流格式或节点配置校验失败"
    const val INVALID_WORKFLOW_HINT = "静态校验未通过，请在图形编辑器中修正连线结构或补齐节点缺失的必填配置。"

    const val WORKFLOW_DISABLED = "workflow_disabled"
    const val WORKFLOW_DISABLED_MSG = "工作流已禁用"
    const val WORKFLOW_DISABLED_HINT = "当前工作流已被禁用，请在属性设置中开启启用开关。"

    const val STEP_LIMIT = "step_limit"
    const val STEP_LIMIT_MSG = "工作流执行步骤超过上限"

    const val MISSING_NODE = "missing_node"
    const val MISSING_NODE_MSG = "找不到待执行节点"

    const val UNKNOWN_NODE = "unknown_node"
    const val UNKNOWN_NODE_MSG = "不支持节点类型"

    const val LOOP_EXECUTION_FAILED = "loop_execution_failed"
    const val LOOP_EXECUTION_FAILED_MSG = "循环控制逻辑执行失败"

    const val INVALID_JSON = "invalid_json"
    const val INVALID_JSON_MSG = "损坏的工作流 JSON 格式"

    const val SIDE_EFFECT_CANCELLED = "side_effect_cancelled"
    const val SIDE_EFFECT_CANCELLED_MSG = "用户取消了操作"

    const val SIDE_EFFECT_FAILED = "side_effect_failed"
    const val SIDE_EFFECT_FAILED_MSG = "副作用执行异常"

    const val NODE_EXECUTION_FAILED = "node_execution_failed"
    const val NODE_EXECUTION_FAILED_MSG = "节点执行抛出未捕获异常"

    const val WORKFLOW_ID_INVALID = "workflow_id_invalid"
    const val WORKFLOW_ID_INVALID_MSG = "workflowId 格式不合法，仅支持英文字母、数字、下划线（_）和连字符（-）"
    const val WORKFLOW_ID_INVALID_HINT = "请使用长度大于 0 且仅包含 A-Z、a-z、0-9、_、- 的工作流 ID。"

    const val FILE_ACCESS_DENIED = "file_access_denied"
    const val FILE_ACCESS_DENIED_MSG = "文件操作越界或超出工作流沙箱授权范围"
    const val FILE_ACCESS_DENIED_HINT = "工作流文件操作受限在 homeDir/workflowId/ 目录下，禁止通过 relative path (..) 越界访问。"

    const val FILE_NOT_FOUND = "file_not_found"
    const val FILE_NOT_FOUND_MSG = "指定路径的文件或目录不存在"
    const val FILE_NOT_FOUND_HINT = "请检查文件路径是否正确，或先使用 file.exists 进行存在性校验。"

    const val FILE_NOT_DIRECTORY = "file_not_directory"
    const val FILE_NOT_DIRECTORY_MSG = "指定路径不是目录"
    const val FILE_NOT_DIRECTORY_HINT = "请为 file.list 传入已存在的目录路径。"

    const val FILE_IO_FAILED = "file_io_failed"
    const val FILE_IO_FAILED_MSG = "文件读写或存储操作失败"
    const val FILE_IO_FAILED_HINT = "请检查存储空间、文件读写权限或路径格式。"

    const val FILE_SIZE_EXCEEDED = "file_size_exceeded"
    const val FILE_SIZE_EXCEEDED_MSG = "文件大小超过文本读取上限"
    const val FILE_SIZE_EXCEEDED_HINT = "file.read_text 最多读取 10 MiB，请先拆分文件或改用其他处理方式。"

    const val FILE_ARCHIVE_INVALID = "file_archive_invalid"
    const val FILE_ARCHIVE_INVALID_MSG = "压缩包格式无效、已损坏或包含不安全的文件路径"
    const val FILE_ARCHIVE_INVALID_HINT = "请使用 ZIP 格式压缩包，且压缩包内的路径不能为绝对路径或包含 ..。"

    const val FILE_ARCHIVE_LIMIT_EXCEEDED = "file_archive_limit_exceeded"
    const val FILE_ARCHIVE_LIMIT_EXCEEDED_MSG = "压缩包超过安全处理上限"
    const val FILE_ARCHIVE_LIMIT_EXCEEDED_HINT = "单个压缩包最多 1024 MiB、最多 1,000 个条目，解压后单个文件最多 100 MiB、总计最多 200 MiB。"

    const val FILE_CONTEXT_MISSING_MSG = "文件节点必须由工作流引擎执行"
    const val HTTP_DOWNLOAD_CONTEXT_MISSING_MSG = "HTTP 下载节点必须由工作流引擎执行"
}

/**
 * 静态拓扑与图校验器发起的 Issue Code 与 Error Message 注册表。
 */
object ActionValidationCode {
    const val UNSUPPORTED_FORMAT = "unsupported_format"
    const val UNSUPPORTED_FORMAT_MSG = "工作流格式版本过高"

    const val DUPLICATE_NODE_ID = "duplicate_node_id"
    const val DUPLICATE_NODE_ID_MSG = "存在重复节点 ID"

    const val INVALID_ENTRY = "invalid_entry"
    const val INVALID_ENTRY_MSG = "入口节点不存在"

    const val INVALID_ERROR_NODE = "invalid_error_node"
    const val INVALID_ERROR_NODE_MSG = "全局错误节点不存在"
    const val INVALID_ERROR_NODE_IN_PORT_MSG = "全局错误节点必须具有 in 控制输入端口"

    const val UNKNOWN_NODE = "unknown_node"
    const val UNKNOWN_NODE_MSG = "不支持节点类型"

    const val UNSUPPORTED_NODE_VERSION = "unsupported_node_version"
    const val UNSUPPORTED_NODE_VERSION_MSG = "节点版本过高"

    const val MISSING_CONFIG = "missing_config"
    const val MISSING_CONFIG_MSG = "缺少节点配置"

    const val UNSUPPORTED_DATA_EDGE = "unsupported_data_edge"
    const val UNSUPPORTED_DATA_EDGE_MSG = "当前执行器尚不支持数据连线，请使用变量引用"

    const val MISSING_CAPABILITY = "missing_capability"
    const val MISSING_CAPABILITY_MSG = "工作流未声明所有节点所需权限"

    const val DUPLICATE_EDGE_ID = "duplicate_edge_id"
    const val DUPLICATE_EDGE_ID_MSG = "存在重复连线 ID"

    const val MISSING_EDGE_NODE = "missing_edge_node"
    const val MISSING_EDGE_NODE_MSG = "连线引用了不存在的节点"

    const val SELF_EDGE = "self_edge"
    const val SELF_EDGE_MSG = "节点不能连接自身"

    const val INVALID_SOURCE_PORT = "invalid_source_port"
    const val INVALID_SOURCE_PORT_MSG = "起始端口不存在或不是输出端口"

    const val INVALID_TARGET_PORT = "invalid_target_port"
    const val INVALID_TARGET_PORT_MSG = "目标端口不存在或不是输入端口"

    const val PORT_KIND_MISMATCH = "port_kind_mismatch"
    const val PORT_KIND_MISMATCH_MSG = "连线类型与端口类型不匹配"

    const val TOO_MANY_CONNECTIONS = "too_many_connections"
    const val TOO_MANY_CONNECTIONS_MSG = "输入端口超过最大连接数"

    const val TOO_MANY_BRANCHES = "too_many_branches"
    const val TOO_MANY_BRANCHES_MSG = "输出端口超过最大连接数"

    const val CONTROL_CYCLE = "control_cycle"
    const val CONTROL_CYCLE_MSG = "当前版本不支持普通控制连线形成循环"

    const val LOOP_MISSING_BODY = "loop_missing_body"
    const val LOOP_MISSING_BODY_MSG = "循环节点必须连接 body 出口"

    const val LOOP_MISSING_COMPLETED = "loop_missing_completed"
    const val LOOP_MISSING_COMPLETED_MSG = "循环节点必须连接 completed 出口"

    const val INVALID_LOOP_LIMIT = "invalid_loop_limit"
    const val INVALID_LOOP_LIMIT_MSG = "maxIterations 必须大于 0"

    const val INVALID_REPEAT_COUNT = "invalid_repeat_count"
    const val INVALID_REPEAT_COUNT_MSG = "count 不能小于 0"

    const val LOOP_BODY_NOT_CLOSED = "loop_body_not_closed"
    const val LOOP_BODY_NOT_CLOSED_MSG = "循环体必须能到达同一循环的 next、continue 或 break 节点"

    const val LOOP_BODY_ESCAPE = "loop_body_escape"
    const val LOOP_BODY_ESCAPE_MSG = "循环体存在未经过循环控制节点就离开的路径"

    const val INVALID_LOOP_CONTROL_TARGET = "invalid_loop_control_target"
    const val INVALID_LOOP_CONTROL_TARGET_MSG = "循环控制节点的 loopId 必须指向循环节点"

    const val LOOP_CONTROL_OUT_OF_SCOPE = "loop_control_out_of_scope"
    const val LOOP_CONTROL_OUT_OF_SCOPE_MSG = "循环控制节点必须位于其 loopId 的循环体内"

    const val PARALLEL_MISSING_BRANCH = "parallel_missing_branch"
    const val PARALLEL_MISSING_BRANCH_MSG = "flow.parallel 必须至少连接一条 branches 出口"

    const val PARALLEL_JOIN_MISSING = "parallel_join_missing"
    const val PARALLEL_JOIN_MISSING_MSG = "flow.parallel 的所有分支必须汇入同一个 flow.join"

    const val PARALLEL_JOIN_AMBIGUOUS = "parallel_join_ambiguous"
    const val PARALLEL_JOIN_AMBIGUOUS_MSG = "flow.parallel 分支不能汇入多个 flow.join"

    const val PARALLEL_NESTED = "parallel_nested"
    const val PARALLEL_NESTED_MSG = "flow.parallel 的分支内暂不支持嵌套 flow.parallel"

    const val JOIN_NOT_PAIRED = "join_not_paired"
    const val JOIN_NOT_PAIRED_MSG = "flow.join 必须作为某个 flow.parallel 的共同汇合点"
}
