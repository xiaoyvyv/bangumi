package com.xiaoyv.bangumi.shared.data.workflow.model

import androidx.compose.runtime.Immutable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * 工作流一次运行期间可供节点读取的结构化上下文。
 *
 * 引擎在开始执行时接收 [input]、[environment] 与 [trigger]，它们代表宿主注入的只读输入；
 * 节点执行过程中产生的值分别写入 [variables] 和 [stepOutputs]，引擎会在每个节点结束后创建新的上下文快照。
 * 节点执行器不得修改该对象，而应通过 [ActionNodeExecutionResult.variableUpdates] 返回待写入的变量。
 *
 * 模板节点可通过 `${input.nameCn}`、`${environment.locale}`、`${trigger.type}`、
 * `${vars.key}` 与 `${steps.nodeId.field}` 读取这些域。未知路径统一解析为 JSON null，避免模板缺少可选字段时中断运行。
 *
 * 上下文仅用于本次执行，不会写入工作流定义或执行日志。宿主不得放入访问令牌、Cookie、密码等敏感信息；
 * 若节点需要受保护能力，应通过工作流声明的 capability 和宿主副作用处理器完成。
 *
 * @param input 触发方传入的任意业务对象，例如条目、人物、用户或通知数据。
 * @param environment 运行平台提供的只读环境数据，例如语言、平台、应用版本与当前时间。
 * @param trigger 触发本次运行的事件数据，例如触发器类型、来源页面和用户操作。
 * @param variables 由已执行节点写入、后续节点可通过 `vars` 域读取的临时变量。
 * @param stepOutputs 已完成节点的结构化输出，键为节点 ID，供后续节点通过 `steps` 域引用。
 */
@Immutable
data class ActionExecutionContext(
    /**
     * 触发方传入的任意业务对象 JSON 数据。
     */
    val input: JsonObject = JsonObject(emptyMap()),
    /**
     * 当前运行环境的只读 JSON 数据。
     */
    val environment: JsonObject = JsonObject(emptyMap()),
    /**
     * 触发此次执行的事件 JSON 数据。
     */
    val trigger: JsonObject = JsonObject(emptyMap()),
    /**
     * 已执行节点写入的运行期临时变量。
     */
    val variables: SerializeMap<String, JsonElement> = persistentMapOf(),
    /**
     * 已完成节点按节点 ID 索引的结构化输出。
     */
    val stepOutputs: SerializeMap<String, JsonObject> = persistentMapOf(),
    /**
     * 最近一层结构化循环注入的只读循环数据。
     *
     * 模板可通过 `loop.item`、`loop.index` 与 `loop.iteration` 读取；引擎在离开循环时恢复外层快照。
     */
    val loop: JsonObject = JsonObject(emptyMap()),
)

/**
 * 模板表达式可读取的运行上下文根命名空间。
 *
 * 这些值属于工作流模板语言的公开契约；编辑器应使用它们提供自动补全，
 * 解析器和迁移逻辑不得在其他位置重复定义相同字面量。
 */
object ActionTemplateRoot {
    /**
     * 指向触发方传入业务对象的模板根路径。
     */
    const val INPUT = "input"

    /**
     * 指向运行环境数据的模板根路径。
     */
    const val ENVIRONMENT = "environment"

    /**
     * 指向触发事件元信息的模板根路径。
     */
    const val TRIGGER = "trigger"

    /**
     * 指向本次运行临时变量的模板根路径。
     */
    const val VARIABLES = "vars"

    /**
     * 指向已完成节点结构化输出的模板根路径。
     */
    const val STEP_OUTPUTS = "steps"

    /**
     * 指向当前最内层循环的运行数据。
     */
    const val LOOP = "loop"
}

/**
 * 工作流节点执行后的出口和结构化输出。
 *
 * @param outputPortId 本次执行命中的输出端口 ID。
 * @param output 本节点产生的结构化输出，会写入运行上下文的 `steps` 域。
 * @param variableUpdates 要合并到运行上下文 `vars` 域的临时变量。
 * @param sideEffect 需要宿主执行的平台副作用；为空表示节点只进行纯计算。
 */
@Immutable
data class ActionNodeExecutionResult(
    /**
     * 本次执行命中的输出端口 ID。
     */
    val outputPortId: String,
    /**
     * 本节点产生的结构化输出。
     */
    val output: JsonObject = JsonObject(emptyMap()),
    /**
     * 要合并到运行上下文的临时变量。
     */
    val variableUpdates: SerializeMap<String, JsonElement> = persistentMapOf(),
    /**
     * 需要宿主执行的平台副作用；为空表示节点只进行纯计算。
     */
    val sideEffect: ActionSideEffect? = null,
)

/**
 * 由宿主 ViewModel/平台层执行的副作用，数据层不依赖导航与 Compose UI。
 */
@Immutable
interface ActionSideEffect

/**
 * 执行引擎向 UI 或测试调用方发出的过程事件。
 */
@Immutable
sealed interface ActionExecutionEvent {
    /**
     * 表示工作流开始运行。
     *
     * @param workflowId 本次运行的工作流 ID。
     */
    @Immutable
    data class Started(
        /**
         * 本次运行的工作流 ID。
         */
        val workflowId: String,
    ) : ActionExecutionEvent

    /**
     * 表示引擎即将执行一个节点。
     *
     * @param nodeId 即将执行的节点 ID。
     */
    @Immutable
    data class NodeStarted(
        /**
         * 即将执行的节点 ID。
         */
        val nodeId: String,
    ) : ActionExecutionEvent

    /**
     * 表示节点已完成，并携带可供后续节点读取的输出。
     *
     * @param nodeId 已完成节点的 ID。
     * @param outputPortId 节点本次命中的输出端口 ID。
     * @param output 节点本次产生的结构化输出。
     */
    @Immutable
    data class NodeCompleted(
        /**
         * 已完成节点的 ID。
         */
        val nodeId: String,
        /**
         * 节点本次命中的输出端口 ID。
         */
        val outputPortId: String,
        /**
         * 节点本次产生的结构化输出。
         */
        val output: JsonObject,
    ) : ActionExecutionEvent

    /**
     * 表示节点请求宿主执行平台相关操作。
     *
     * @param nodeId 请求副作用的节点 ID。
     * @param effect 需要 UI 或平台宿主执行的副作用。
     */
    @Immutable
    data class SideEffectRequested(
        /**
         * 请求副作用的节点 ID。
         */
        val nodeId: String,
        /**
         * 需要 UI 或平台宿主执行的副作用。
         */
        val effect: ActionSideEffect,
    ) : ActionExecutionEvent

    /**
     * 表示工作流已结束，并携带完整执行日志。
     *
     * @param log 本次执行的最终可持久化日志。
     */
    @Immutable
    data class Completed(
        /**
         * 本次执行的最终可持久化日志。
         */
        val log: ActionExecutionLog,
    ) : ActionExecutionEvent

    /**
     * 表示执行过程中出现可供 UI 展示的错误。
     *
     * @param error 错误的结构化描述。
     */
    @Immutable
    data class Failed(
        /**
         * 错误的结构化描述。
         */
        val error: ActionExecutionError,
    ) : ActionExecutionEvent
}

/**
 * 可持久化的单次运行记录。不会保存上下文中的密钥。
 */
@Immutable
@Serializable
data class ActionExecutionLog(
    /**
     * 被执行工作流的稳定 ID。
     */
    val workflowId: String,
    /**
     * 本次执行开始的 Unix 毫秒时间戳。
     */
    val startedAt: Long,
    /**
     * 本次执行结束的 Unix 毫秒时间戳。
     */
    val finishedAt: Long,
    /**
     * 最终运行状态，取值见 [ActionExecutionStatus]。
     */
    val status: String,
    /**
     * 按实际执行顺序记录的节点步骤。
     */
    val steps: SerializeList<ActionExecutionStep> = persistentListOf(),
)

/**
 * 单个节点在一次工作流运行中的执行记录。
 */
@Immutable
@Serializable
data class ActionExecutionStep(
    /**
     * 已执行节点的 ID。
     */
    val nodeId: String,
    /**
     * 节点开始执行的 Unix 毫秒时间戳。
     */
    val startedAt: Long,
    /**
     * 节点结束执行的 Unix 毫秒时间戳。
     */
    val finishedAt: Long,
    /**
     * 节点命中的输出端口 ID；发生异常时可能为空。
     */
    val outputPortId: String? = null,
    /**
     * 节点返回或错误分支产生的结构化输出。
     */
    val output: JsonObject = JsonObject(emptyMap()),
    /**
     * 节点失败时的结构化错误；正常步骤为空。
     */
    val error: ActionExecutionError? = null,
)

/**
 * 可由 UI、日志系统和失败分支共同消费的执行错误。
 */
@Immutable
@Serializable
data class ActionExecutionError(
    /**
     * 机器可读的稳定错误代码。
     */
    val code: String,
    /**
     * 适合展示或记录的错误说明。
     */
    val message: String,
    /**
     * 产生错误的节点 ID；工作流校验错误可能为空。
     */
    val nodeId: String? = null,
    /**
     * 可选的结构化诊断信息，不得包含敏感数据。
     */
    val details: JsonObject = JsonObject(emptyMap()),
)

/**
 * 工作流执行结果的稳定状态值。
 */
object ActionExecutionStatus {
    /**
     * 所有节点按预期完成，或完成了错误处理分支后的最终状态。
     */
    const val SUCCESS = "success"

    /**
     * 引擎无法继续执行时的失败状态。
     */
    const val FAILED = "failed"

    /**
     * 用户或宿主取消副作用后结束的状态。
     */
    const val CANCELLED = "cancelled"
}
