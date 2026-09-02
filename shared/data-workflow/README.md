# data-workflow

`data-workflow` 是 Bangumi Multiplatform 项目中用于行为编排与自动化处理的核心工作流引擎模块。

它基于 **DAG (有向无环图) 事件驱动架构**，将复杂的业务逻辑（如多接口并发请求、数据提取与清洗、条件分支判断、UI 弹窗交互及系统操作）抽象为可序列化的 JSON 工作流协议。无论是在 App
内执行跨数据源番剧比对，还是批处理条目信息，均可通过可视化节点图驱动。

---

## 应用场景

通过节点编排，可以在 App 内组合出以下典型功能：

- **多数据源并发聚合**：向 Bilibili、MangaDex 等多个第三方 API 发起请求，自动提取关联番剧元数据并计算匹配度。
- **网页数据提取与 Cookie 同步**：借助内置 WebView 与 HTML 选择器，解析网页中的标签与文本内容，并完成账号 Cookie 的同步。
- **数据清洗与自动化批处理**：遍历追番列表，对 CSV / XML / JSON 结构化数据进行筛选（如过滤高分作品），自动弹出系统通知或调起画廊预览。
- **响应式 UI 交互与快捷流**：在流程中挂起并唤起自定义输入框、二次确认框、剪贴板读写与震动反馈，将用户交互结果作为后续节点的输入。

---

## 目录

- [设计目标](#设计目标)
- [模块边界](#模块边界)
- [架构设计与核心机制](#架构设计与核心机制)
- [快速接入](#快速接入)
- [上下文与模板](#上下文与模板)
- [控制流与错误处理](#控制流与错误处理)
- [内置节点参考手册 (140+ Nodes)](#内置节点参考手册-140-nodes)
    - [1. 流程控制节点 (Flow)](#1-流程控制节点-flow)
    - [2. 逻辑判断节点 (Control)](#2-逻辑判断节点-control)
    - [3. 变量与数据节点 (Data)](#3-变量与数据节点-data)
    - [4. 网页与 HTML 解析节点 (HTML)](#4-网页与-html-解析节点-html)
    - [5. JSON 对象处理节点 (Object)](#5-json-对象处理节点-object)
    - [6. 数组集合操作节点 (Array)](#6-数组集合操作节点-array)
    - [7. 文本与正则处理节点 (Text)](#7-文本与正则处理节点-text)
    - [8. 算术数学计算节点 (Math)](#8-算术数学计算节点-math)
    - [9. 日期与时间节点 (Date)](#9-日期与时间节点-date)
    - [10. URL 格式化与操作节点 (URL)](#10-url-格式化与操作节点-url)
    - [11. 结构化数据解析节点 (JSON / XML / CSV)](#11-结构化数据解析节点-json--xml--csv)
    - [12. 编解码与哈希安全节点 (Codec / Crypto)](#12-编解码与哈希安全节点-codec--crypto)
    - [13. 网络 HTTP 请求节点 (HTTP)](#13-网络-http-请求节点-http)
    - [14. 本地存储节点 (Storage)](#14-本地存储节点-storage)
    - [15. 系统与 UI 交互节点 (Action)](#15-系统与-ui-交互节点-action)
- [节点扩展规范](#节点扩展规范)
- [安全与权限规范](#安全与权限规范)
- [测试与验证](#测试与验证)

---

## 设计目标

- **JSON 协议为唯一事实来源**：画布渲染、列表展示与运行日志均由 `ActionWorkflow` 导出，支持标准的导入、导出与版本迁移。
- **模块化节点声明**：节点类型、端口规则与配置键由 `ActionNodeDefinition` 统一描述，扩充新节点类型时无需重构核心引擎。
- **统一结构化上下文**：输入数据、环境变量、临时变量、步骤输出与循环状态统一使用 JSON 组织，支持全路径占位符插值。
- **基础设施与 UI 隔离**：网络基础设施由外部注入，导航、弹窗、剪贴板等系统操作通过 `ActionSideEffect` 交由宿主解耦实现。
- **状态感知与异常拦截**：通过 `Flow<ActionExecutionEvent>` 实时推送执行状态，捕获节点内部异常并支持错误路由，避免进程崩溃。
- **凭据安全防护**：工作流不持久化 Token 或私有凭据，高风险平台能力须显式声明并由宿主授权。

---

## 模块边界

```text
features / shared:data
  ├─ 提供 HttpClient、仓库、具体触发器与 UI 副作用处理器
  └─ 依赖 data-workflow

data-workflow
  ├─ model      工作流 JSON 协议与执行事件
  ├─ node       节点规格、内置节点和注册中心
  ├─ engine     校验、图算法执行、循环和错误路由
  ├─ port       可替换的基础设施边界
  ├─ codec      导入、导出和格式迁移
  └─ di         Koin 装配入口
```

`data-workflow` 不直接持久化用户工作流；数据存储由 `shared:data` 中的仓库负责。它也不包含 Compose UI 或页面导航逻辑，所有交互事件均向上抛出由宿主消费。

---

## 架构设计与核心机制

`data-workflow` 采用响应式事件驱动与 DAG 有向无环图调度相结合的系统架构，保证图计算与 UI 渲染解耦。

```text
  ┌────────────────────────────────────────────────────────────────────────┐
  │                           UI & 业务接入层                                │
  │ (WorkflowsScreen / WorkflowSideEffectHost / ActionWorkflowRepository)  │
  └───────────────────────────────────┬────────────────────────────────────┘
                                      │  传递 ActionWorkflow / 消费 Flow<ActionExecutionEvent>
                                      ▼
  ┌────────────────────────────────────────────────────────────────────────┐
  │                        data-workflow 领域引擎                           │
  │                                                                        │
  │  ┌───────────────────────┐              ┌───────────────────────────┐  │
  │  │ ActionWorkflowEngine  │─────────────▶│  LoopExecutionController  │  │
  │  │  (DAG 就绪队列状态机)   │              │     (循环帧栈与迭代器)     │  │
  │  └───────────┬───────────┘              └───────────────────────────┘  │
  │              │                                                         │
  │              ├──────────────────────────┐                              │
  │              ▼                          ▼                              │
  │  ┌───────────────────────┐  ┌───────────────────────────┐              │
  │  │ ActionNodeRegistry    │  │ ActionTemplateResolver    │              │
  │  │  (140+ 节点定义与执行) │  │  (上下文占位符插值解析)    │              │
  │  └───────────────────────┘  └───────────────────────────┘              │
  └───────────────────────────────────┬────────────────────────────────────┘
                                      │  发出 ActionSideEffectRequested / 挂起等待结果
                                      ▼
  ┌────────────────────────────────────────────────────────────────────────┐
  │                         ActionSideEffectHandler                        │
  │            (系统 Toast / 确认框 / 输入框 / 剪贴板 / Web 弹窗)             │
  └────────────────────────────────────────────────────────────────────────┘
```

### 1. 响应式事件通道 (Flow-Based Event Engine)

- 引擎暴露 `Flow<ActionExecutionEvent>` 接口，将节点启动 (`NodeStarted`)、节点完成 (`NodeCompleted`)、副作用请求 (`SideEffectRequested`) 及运行日志 (`Completed`) 作为冷流推送到 UI 层。
- 引擎内部基于就绪队列 (`readyQueue`) 进行动态图调度，使界面渲染、日志追踪与底层节点图计算解耦。

### 2. UI 交互与异步挂起 (Side Effects & Suspend Execution)

- 引擎遵循“计算与副作用分离”原则。对于弹窗交互（如 `ui.input_dialog`、`ui.confirm`）、浏览器打开、剪贴板读写等平台操作，节点执行器构建 `ActionSideEffect` 抛出。
- `sideEffectHandler.handle(effect)` 为 `suspend` 函数。遇到 UI 交互时，引擎在当前节点挂起；Compose UI 响应并返回数据后，协程恢复 (**Resume**)，带入输入结果继续执行后续节点。

### 3. DAG 有向图与 Fork-Join 分支合流

- **多路分叉 (Fan-Out)**：节点出边连接多个目标节点时，控制边激活集 (`activatedEdges`) 会同时捕获所有 downstream 分支，将它们并列压入就绪队列。
- **多路合流 (Fan-In/Join)**：合流节点在有多条活跃入边时，调度器通过 `pendingPredecessors` 校验上游节点的完成状态，确保合流节点在所有活跃分支执行完成后仅运行一次。
- **循环波次隔离 (Loop Wave Reset)**：对于 `loop.for_each` 等循环结构，每次进入新的 `BODY` 迭代时，引擎通过拓扑寻路清空循环体内部节点的 `executedNodes` 标记，保障循环体内部的 Join
  节点在多轮迭代中具备独立的波次合流能力。

---

## 快速接入

宿主模块应提供带 Cookie、网络配置等应用能力的专用 HTTP Client，并使用 `actionWorkflowHttpClientQualifier` 注册。随后加载 `workflowModules`：

```kotlin
val workflowInfrastructureModule = module {
    single(actionWorkflowHttpClientQualifier) {
        createHttpClient(
            config = get<PreferenceStore>().settings.network,
            cookieStorage = get<ApiCookiesStorage>(),
            enableJsonContentNegotiation = false,
        )
    }
}

startKoin {
    modules(workflowInfrastructureModule, *workflowModules)
}
```

执行时由宿主提供入口上下文和副作用处理器：

```kotlin
workflowEngine.execute(
    workflow = workflow,
    initialContext = ActionExecutionContext(
        input = subjectJson,
        environment = environmentJson,
        trigger = triggerJson,
    ),
    sideEffectHandler = actionSideEffectHandler,
).collect { event ->
    // 更新运行日志、处理 ActionSideEffectRequested、展示执行状态。
}
```

---

## 上下文与模板

`ActionExecutionContext` 为一次运行创建不可变快照，模板支持以下根命名空间（占位符格式 `${vars.xxx}`）：

| 路径            | 来源                | 示例                                  |
|:--------------|:------------------|:------------------------------------|
| `input`       | 触发方传入的业务数据        | `${input.name}`                     |
| `environment` | 平台环境              | `${environment.locale}`             |
| `trigger`     | 本次触发事件            | `${trigger.type}`                   |
| `vars`        | 已执行节点写入的临时变量      | `${vars.message}`                   |
| `steps`       | 已完成节点输出，按节点 ID 索引 | `${steps.request.body}`             |
| `loop`        | 当前最内层循环数据         | `${loop.item.name}`、`${loop.index}` |

未知路径解析为 JSON `null`，以便可选字段缺失时流程仍能用条件节点自行处理。

---

## 控制流与错误处理

端口由 `ActionNodeSpec` 声明。常用控制端口为：

- `in`：输入端口（允许接收多条来自不同分支的入边以实现合流 Join）。
- `next`：普通顺序执行端口（允许连接多个下游目标节点以实现分叉 Fan-Out）。
- `success` / `failure`：副作用或可失败操作的结果分支。
- `true` / `false`、`matched` / `default`：条件和匹配分支。

### 专业工作流引擎与 DAG 分支/合流架构

`ActionWorkflowEngine` 采用基于就绪队列（Ready Queue）与依赖边激活集（Activated Edges）的现代化有向图（DAG）调度架构，全面支持复杂业务流程的分叉与汇入：

1. **多路分叉 (Forking / Fan-Out)**：
    - 节点的单个输出端口（如 `next` 或条件端口）支持连接多个下游目标节点。
    - 当上游节点执行完成并激活输出端口时，所有关联的控制边均会被标记为激活，触发多条分支并行/顺序调度。

2. **多路汇入与合流 (Merging / Fan-In / Join)**：
    - 下游节点的输入端口（如 `in`）支持接收多条来自不同 upstream 分支的控制边。
    - 引擎调度器会自动分析图中节点的依赖关系。当某个合流节点有多个活跃的 upstream 分支时，调度器会等待**所有已被激活的 upstream 前置分支节点全部执行完毕**（`pendingPredecessors == 0`
      ）后，再将合流节点压入就绪队列，确保合流节点**仅被触发执行一次**。
    - 对于条件分支（如 `control.if`），只有被选中的分支端口（如 `true`）所指向的控制边会被激活；未被激活的条件分支路径自动跳过，不阻塞 downstream 合流节点的唤醒。

3. **分支数据快照与变量共享 (Context Accumulation)**：
    - 执行过程中，各个分叉分支节点输出的 JSON 结构数据（通过 `outputKey`）及设置的变量（`vars`）均会增量合并至 `ActionExecutionContext` 中。
    - 汇入节点（Join Node）可使用模板表达式（如 `${steps.node_left.result}` 与 `${steps.node_right.result}`）自由引用并整合多条分支的数据。

---

## 内置节点参考手册 (140+ Nodes)

### 1. 流程控制节点 (Flow)

流程控制节点用于指挥工作流的执行走向、延迟、并发及异常捕获。

| 节点类型 (Type)           | 作用描述             | 必需配置键                            | 常用输出端口                       |
|:----------------------|:-----------------|:---------------------------------|:-----------------------------|
| **`flow.start`**      | 工作流起始点           | 无                                | `next`                       |
| **`flow.end`**        | 工作流正常结束点         | 无                                | 无 (流程终止)                     |
| **`flow.delay`**      | 延时等待指定时间         | `delayMillis` (毫秒)               | `next`                       |
| **`flow.stop`**       | 强制打断并终止流程        | `message` (可选原因)                 | 无                            |
| **`flow.assert`**     | 条件断言校验 (失败则抛出异常) | `condition`                      | `next`                       |
| **`flow.switch`**     | 多路多条件分支路由        | `cases` (分支对象)                   | 匹配到的 `branchKey` 或 `default` |
| **`flow.log`**        | 打印日志信息           | `message`, `level`               | `next`                       |
| **`flow.debug`**      | 流程调试断点输出         | `message`                        | `next`                       |
| **`flow.try`**        | 捕获异常起始节点         | 无                                | `try`                        |
| **`flow.catch`**      | 捕获异常入口           | 无                                | `catch`                      |
| **`flow.finally`**    | 最终必执行收尾节点        | 无                                | `finally`                    |
| **`flow.call`**       | 调用执行子工作流         | `workflowId`, `outputKey`        | `next`                       |
| **`flow.return`**     | 子工作流返回结果         | `output`                         | 无                            |
| **`flow.parallel`**   | 并行多线程分支起点        | 无                                | `branches`                   |
| **`flow.join`**       | 等待多线程分支完全接合      | `values`                         | `next`                       |
| **`flow.retry`**      | 失败自动重试机制         | `retryCount`, `retryDelayMillis` | `next`                       |
| **`flow.timeout`**    | 超时控制挂起           | `timeoutMillis`                  | `next`                       |
| **`flow.rate_limit`** | 限流控制保护           | `delayMillis`                    | `next`                       |

---

### 2. 逻辑判断节点 (Control)

用于数值、布尔值或文本条件的逻辑运算，返回 `true`/`false`。

| 节点类型 (Type)                          | 说明                               | 必需配置键                        | 输出端口                  |
|:-------------------------------------|:---------------------------------|:-----------------------------|:----------------------|
| **`control.if`**                     | 条件判断 (真走 `matched`，假走 `default`) | `condition`                  | `matched` / `default` |
| **`control.equals`**                 | 判断两值相等                           | `left`, `right`, `outputKey` | `next`                |
| **`control.not_equals`**             | 判断两值不相等                          | `left`, `right`, `outputKey` | `next`                |
| **`control.greater_than`**           | 大于判断 (`left > right`)            | `left`, `right`, `outputKey` | `next`                |
| **`control.greater_than_or_equals`** | 大于等于判断 (`left >= right`)         | `left`, `right`, `outputKey` | `next`                |
| **`control.less_than`**              | 小于判断 (`left < right`)            | `left`, `right`, `outputKey` | `next`                |
| **`control.less_than_or_equals`**    | 小于等于判断 (`left <= right`)         | `left`, `right`, `outputKey` | `next`                |
| **`control.and`**                    | 逻辑与 (两值均为 true)                  | `left`, `right`, `outputKey` | `next`                |
| **`control.or`**                     | 逻辑或 (有一值为 true 即可)               | `left`, `right`, `outputKey` | `next`                |
| **`control.not`**                    | 逻辑非 (布尔值取反)                      | `value`, `outputKey`         | `next`                |
| **`control.is_null`**                | 判断值是否为 null                      | `value`, `outputKey`         | `next`                |
| **`control.is_empty`**               | 判断文本或数组是否为空                      | `value`, `outputKey`         | `next`                |

---

### 3. 变量与数据节点 (Data)

用于对上下文中的全局变量进行保存、读取、转换与建模。

| 节点类型 (Type)           | 作用描述             | 必需配置键                                | 写入变量               |
|:----------------------|:-----------------|:-------------------------------------|:-------------------|
| **`data.set_var`**    | 设置单个变量           | `key`, `value`                       | `vars.<key>`       |
| **`data.get_var`**    | 读取单个变量           | `key`, `outputKey`                   | `vars.<outputKey>` |
| **`data.remove_var`** | 删除指定变量           | `key`                                | 移除 `vars.<key>`    |
| **`data.merge_vars`** | 批量合并对象字典到全局变量    | `values`                             | 合并至 `vars`         |
| **`data.clear_vars`** | 清空所有全局上下文变量      | 无                                    | 清空 `vars`          |
| **`data.map_fields`** | 对 JSON 对象字段映射重命名 | `object`, `assignments`, `outputKey` | `vars.<outputKey>` |
| **`data.template`**   | 变量文本插值模板         | `template`, `outputKey`              | `vars.<outputKey>` |
| **`data.to_number`**  | 强制转换为数字类型        | `value`, `outputKey`                 | `vars.<outputKey>` |
| **`data.to_string`**  | 强制转换为文本类型        | `value`, `outputKey`                 | `vars.<outputKey>` |
| **`data.to_boolean`** | 强制转换为布尔类型        | `value`, `outputKey`                 | `vars.<outputKey>` |
| **`data.type_of`**    | 检测变量数据类型         | `value`, `outputKey`                 | `vars.<outputKey>` |
| **`data.uuid`**       | 生成全局唯一 UUID 字符串  | `outputKey`                          | `vars.<outputKey>` |

---

### 4. 网页与 HTML 解析节点 (HTML)

通过 CSS 选择器解析网页 HTML 内容（基于 Ksoup HTML 引擎）。

| 节点类型 (Type)             | 说明                               | 必需配置键                           | 输出数据                   |
|:------------------------|:---------------------------------|:--------------------------------|:-----------------------|
| **`html.query`**        | CSS 选择器提取单项文本/属性                 | `html`, `selector`, `outputKey` | `"文本内容"`               |
| **`html.query_all`**    | CSS 选择器提取多项数组                    | `html`, `selector`, `outputKey` | `["条目1", "条目2"]`       |
| **`html.title`**        | 快捷获取网页 `<title>`                 | `html`, `outputKey`             | `"网页标题"`               |
| **`html.text`**         | 剔除标签提取纯文本                        | `html`, `outputKey`             | `"纯文本"`                |
| **`html.meta_content`** | 提取 `<meta name="xxx">` 的 content | `html`, `name`, `outputKey`     | `"Meta 内容"`            |
| **`html.links`**        | 提取网页内部所有超链接列表                    | `html`, `outputKey`             | `["https://...", ...]` |

---

### 5. JSON 对象处理节点 (Object)

针对字典对象 `{ "key": "value" }` 的高效操作节点。

| 节点类型 (Type)               | 说明                         | 必需配置键                                 |
|:--------------------------|:---------------------------|:--------------------------------------|
| **`object.get`**          | JSONPath 或点号读取深度属性         | `object`, `path`, `outputKey`         |
| **`object.set`**          | 设置/覆盖对象属性键值对               | `object`, `key`, `value`, `outputKey` |
| **`object.remove`**       | 删除指定属性键                    | `object`, `key`, `outputKey`          |
| **`object.omit`**         | 批量剔除指定 Key 列表              | `object`, `keys`, `outputKey`         |
| **`object.pick`**         | 仅挑选保留指定 Key 列表             | `object`, `keys`, `outputKey`         |
| **`object.merge`**        | 多个 JSON 对象列表深度合并           | `objects`, `outputKey`                |
| **`object.keys`**         | 获取对象的所有 Key 列表             | `object`, `outputKey`                 |
| **`object.values`**       | 获取对象的所有属性值列表               | `object`, `outputKey`                 |
| **`object.entries`**      | 转为 `[[key, val]]` 元组数组     | `object`, `outputKey`                 |
| **`object.from_entries`** | `[[key, val]]` 还原为 JSON 对象 | `entries`, `outputKey`                |
| **`object.has_key`**      | 校验对象是否包含指定 Key             | `object`, `key`, `outputKey`          |
| **`object.is_empty`**     | 判断对象是否为空对象 (`{}`)          | `object`, `outputKey`                 |

---

### 6. 数组集合操作节点 (Array)

包含 31 种数组遍历、变换、筛选与统计功能。

| 节点类型 (Type)                 | 作用说明                             | 必需配置键                                           |
|:----------------------------|:---------------------------------|:------------------------------------------------|
| **`array.length`**          | 获取数组元素长度                         | `values`, `outputKey`                           |
| **`array.create`**          | 快速创建空数组或预设数组                     | `values`, `outputKey`                           |
| **`array.append`**          | 数组尾部追加新元素                        | `values`, `value`, `outputKey`                  |
| **`array.insert_at`**       | 指定下标位置插入元素                       | `values`, `index`, `value`, `outputKey`         |
| **`array.remove_at`**       | 删除指定下标的元素                        | `values`, `index`, `outputKey`                  |
| **`array.filter`**          | 按照条件筛选数组 (`equals`, `contains`等) | `values`, `operator`, `expected`, `outputKey`   |
| **`array.map`**             | 提取数组内部项属性生成新数组                   | `values`, `fieldPath`, `outputKey`              |
| **`array.flat_map`**        | 映射后自动扁平化拉平                       | `values`, `fieldPath`, `outputKey`              |
| **`array.concat`**          | 连接拼接多个数组                         | `values`, `outputKey`                           |
| **`array.zip`**             | 两个数组交错打包 `[[a1,b1], [a2,b2]]`    | `values`, `otherValues`, `outputKey`            |
| **`array.take`**            | 提取数组的前 N 个元素                     | `values`, `count`, `outputKey`                  |
| **`array.drop`**            | 跳过数组的前 N 个元素                     | `values`, `count`, `outputKey`                  |
| **`array.contains`**        | 校验数组是否包含目标值                      | `values`, `value`, `outputKey`                  |
| **`array.find`**            | 查找首个满足条件的元素                      | `values`, `fieldPath`, `expected`, `outputKey`  |
| **`array.distinct`**        | 数组元素去重                           | `values`, `outputKey`                           |
| **`array.sort`**            | 数组元素排序 (升序/降序)                   | `values`, `outputKey`                           |
| **`array.reverse`**         | 反转数组项顺序                          | `values`, `outputKey`                           |
| **`array.slice`**           | 数组范围切片截取                         | `values`, `startIndex`, `endIndex`, `outputKey` |
| **`array.flatten`**         | 多维嵌套数组拉平为一维                      | `values`, `outputKey`                           |
| **`array.group_by`**        | 按对象的特定字段分组为 Map                  | `values`, `fieldPath`, `outputKey`              |
| **`array.first`**           | 获取数组第一个元素                        | `values`, `outputKey`                           |
| **`array.last`**            | 获取数组最后一个元素                       | `values`, `outputKey`                           |
| **`array.sum`**             | 数值数组求和                           | `values`, `outputKey`                           |
| **`array.avg`**             | 数值数组求平均值                         | `values`, `outputKey`                           |
| **`array.min`** / **`max`** | 数值数组求最小值 / 最大值                   | `values`, `outputKey`                           |
| **`array.chunk`**           | 将大数组按固定大小分块                      | `values`, `size`, `outputKey`                   |
| **`array.shuffle`**         | 随机打乱数组                           | `values`, `outputKey`                           |
| **`array.sample`**          | 从数组中随机抽取一个样本                     | `values`, `outputKey`                           |
| **`array.index_of`**        | 查找元素在数组中的下标                      | `values`, `value`, `outputKey`                  |
| **`array.intersection`**    | 两个数组求交集                          | `values`, `otherValues`, `outputKey`            |
| **`array.difference`**      | 两个数组求差集                          | `values`, `otherValues`, `outputKey`            |

---

### 7. 文本与正则处理节点 (Text)

文本格式化、正则表达式查找与替换节点。

| 节点类型 (Type)                 | 作用描述                | 必需配置键                                            |
|:----------------------------|:--------------------|:-------------------------------------------------|
| **`text.length`**           | 统计文本字符长度            | `text`, `outputKey`                              |
| **`text.trim`**             | 去除文本首尾空白字符          | `text`, `outputKey`                              |
| **`text.lowercase`**        | 转小写字母               | `text`, `outputKey`                              |
| **`text.uppercase`**        | 转大写字母               | `text`, `outputKey`                              |
| **`text.capitalize`**       | 首字母转大写              | `text`, `outputKey`                              |
| **`text.repeat`**           | 重复拼接文本 N 次          | `text`, `count`, `outputKey`                     |
| **`text.reverse`**          | 反转文本字符串             | `text`, `outputKey`                              |
| **`text.index_of`**         | 查找子串下标              | `text`, `pattern`, `outputKey`                   |
| **`text.template`**         | 基于对象的 `${var}` 模版渲染 | `template`, `object`, `outputKey`                |
| **`text.split`**            | 分割文本为字符串数组          | `text`, `delimiter`, `outputKey`                 |
| **`text.regex_match`**      | 正则表达式匹配校验与分组提取      | `text`, `pattern`, `outputKey`                   |
| **`text.match_all`**        | 正则全文多次匹配查找列表        | `text`, `pattern`, `outputKey`                   |
| **`text.substring`**        | 子串截取                | `text`, `startIndex`, `endIndex`, `outputKey`    |
| **`text.substring_before`** | 截取指定分隔符之前的文本        | `text`, `delimiter`, `outputKey`                 |
| **`text.substring_after`**  | 截取指定分隔符之后的文本        | `text`, `delimiter`, `outputKey`                 |
| **`text.replace`**          | 静态普通文本替换            | `text`, `pattern`, `replacement`, `outputKey`    |
| **`text.replace_regex`**    | 正则表达式替换             | `text`, `pattern`, `replacement`, `outputKey`    |
| **`text.join`**             | 用指定分隔符连接字符串数组       | `values`, `separator`, `outputKey`               |
| **`text.pad`**              | 头部/尾部补充对齐填充         | `text`, `padLength`, `padCharacter`, `outputKey` |
| **`text.format_number`**    | 格式化保留小数位数           | `value`, `fractionDigits`, `outputKey`           |
| **`text.contains`**         | 校验文本是否包含子串          | `text`, `pattern`, `outputKey`                   |
| **`text.starts_with`**      | 校验文本前缀              | `text`, `pattern`, `outputKey`                   |
| **`text.ends_with`**        | 校验文本后缀              | `text`, `pattern`, `outputKey`                   |
| **`text.slugify`**          | 生成 URL Slug 字符串     | `text`, `outputKey`                              |
| **`text.truncate`**         | 超长文本截断加上 `...`      | `text`, `limit`, `outputKey`                     |

---

### 8. 算术数学计算节点 (Math)

全功能算术运算与数学函数节点。

| 节点类型 (Type)            | 算法逻辑                     | 配置参数                               |
|:-----------------------|:-------------------------|:-----------------------------------|
| **`math.add`**         | 加法运算 (`left + right`)    | `left`, `right`, `outputKey`       |
| **`math.subtract`**    | 减法运算 (`left - right`)    | `left`, `right`, `outputKey`       |
| **`math.multiply`**    | 乘法运算 (`left * right`)    | `left`, `right`, `outputKey`       |
| **`math.divide`**      | 除法运算 (`left / right`)    | `left`, `right`, `outputKey`       |
| **`math.modulo`**      | 取模/求余运算 (`left % right`) | `left`, `right`, `outputKey`       |
| **`math.min` / `max`** | 比较两数求较小/较大者              | `left`, `right`, `outputKey`       |
| **`math.pow`**         | 幂运算 ($left^{right}$)     | `left`, `right`, `outputKey`       |
| **`math.sqrt`**        | 非负数求平方根 ($\sqrt{x}$)     | `value`, `outputKey`               |
| **`math.sum`**         | 多数值集合列表累加求和              | `values`, `outputKey`              |
| **`math.avg`**         | 多数值集合列表求平均值              | `values`, `outputKey`              |
| **`math.log`**         | 计算自然对数 $\ln(x)$          | `value`, `outputKey`               |
| **`math.exp`**         | 计算指数 $e^x$               | `value`, `outputKey`               |
| **`math.negate`**      | 数值取负数 (`-value`)         | `value`, `outputKey`               |
| **`math.round`**       | 四舍五入到指定小数位               | `value`, `decimals`, `outputKey`   |
| **`math.floor`**       | 向下取整                     | `value`, `outputKey`               |
| **`math.ceil`**        | 向上取整                     | `value`, `outputKey`               |
| **`math.abs`**         | 求绝对值 ($                  | x                                  |$) | `value`, `outputKey` |
| **`math.random`**      | 生成指定 `[min, max)` 范围内随机数 | `min`, `max`, `outputKey`          |
| **`math.clamp`**       | 数值限幅约束在 `[min, max]` 区间  | `value`, `min`, `max`, `outputKey` |

---

### 9. 日期与时间节点 (Date)

基于标准 Unix 毫秒时间戳的日期时间解析与计算。

| 节点类型 (Type)              | 说明                  | 必需配置键                                                  | 输出示例                                |
|:-------------------------|:--------------------|:-------------------------------------------------------|:------------------------------------|
| **`date.now`**           | 获取系统当前时刻时间戳 (毫秒)    | `outputKey`                                            | `1700000000000`                     |
| **`date.format`**        | 时间戳转 ISO 标准日期字符串    | `timestamp`, `outputKey`                               | `"2026-09-01T04:15:00Z"`            |
| **`date.parse`**         | 日期字符串解析为毫秒时间戳       | `text`, `outputKey`                                    | `1700000000000`                     |
| **`date.add`**           | 增加指定时间 (天/小时/分钟/毫秒) | `timestamp`, `count`, `unit`, `outputKey`              | 新毫秒时间戳                              |
| **`date.subtract`**      | 减少指定时间              | `timestamp`, `count`, `unit`, `outputKey`              | 新毫秒时间戳                              |
| **`date.diff`**          | 计算两个时间戳之间的差值        | `timestampLeft`, `timestampRight`, `unit`, `outputKey` | 差值数字                                |
| **`date.relative_time`** | 人性化相对时间             | `timestamp`, `outputKey`                               | `"5分钟前"`, `"刚刚"`                    |
| **`date.get_component`** | 提取年月日时分秒独立分量对象      | `timestamp`, `outputKey`                               | `{"year":2026, "month":9, "day":1}` |

---

### 10. URL 格式化与操作节点 (URL)

标准 HTTP/HTTPS 地址解析与动态构建节点。

| 节点类型 (Type)               | 说明                                                        | 必需配置键                                     |
|:--------------------------|:----------------------------------------------------------|:------------------------------------------|
| **`url.parse`**           | 将 URL 拆解为 protocol, host, port, path 和 queryParameters 对象 | `url`, `outputKey`                        |
| **`url.build`**           | 由 baseUrl 与 queryParameters 字典自动组装标准 URL                  | `baseUrl`, `queryParameters`, `outputKey` |
| **`url.set_query_param`** | 在给定 URL 动态追加/替换/删除 Query 查询参数                             | `url`, `key`, `value`, `outputKey`        |
| **`url.get_query_param`** | 读取给定 URL 中指定 Key 的 Query 查询参数值                            | `url`, `key`, `outputKey`                 |

---

### 11. 结构化数据解析节点 (JSON / XML / CSV)

处理常见的结构化文本数据格式。

| 节点类型 (Type)          | 格式说明                         | 必需配置键                         |
|:---------------------|:-----------------------------|:------------------------------|
| **`json.parse`**     | 将 JSON 字符串解析为 JSON 对象/数组     | `text`, `outputKey`           |
| **`json.stringify`** | 将 JSON 对象/数组序列化为字符串          | `value`, `outputKey`          |
| **`json.extract`**   | 通过 JSONPath 直接提取 JSON 节点     | `source`, `path`, `outputKey` |
| **`json.validate`**  | 校验字符串是否为合法的 JSON 格式          | `text`, `outputKey`           |
| **`xml.parse`**      | 将 XML/RSS 文本解析为 JSON 对象结构    | `text`, `outputKey`           |
| **`xml.stringify`**  | 将 JSON 对象递归生成 XML 字符串        | `data`, `outputKey`           |
| **`csv.parse`**      | 将 CSV 表格文本解析为对象数组 `[{}, {}]` | `text`, `outputKey`           |
| **`csv.stringify`**  | 将对象数组 `[{}, {}]` 导出为 CSV 文本  | `items`, `outputKey`          |

---

### 12. 编解码与哈希安全节点 (Codec / Crypto)

安全哈希加密与数据编码解码节点。

#### 12.1 编码解码 (Codec)

- **`codec.base64_encode` / `decode`**：标准 Base64 字符串编解码。
- **`codec.base64_url_encode` / `decode`**：URL 安全的 Base64 编解码。
- **`codec.hex_encode` / `decode`**：十六进制 Hex 编解码。
- **`codec.url_encode` / `decode`**：标准 URL Percent-Encoding 编解码。
- **`codec.html_escape` / `unescape`**：HTML 转义（如将 `<` 转换为 `&lt;`）。

#### 12.2 密码学与哈希 (Crypto)

- **`crypto.hash`**：计算摘要哈希（支持 `algorithm`: `"SHA-256"`, `"SHA-512"`, `"MD5"`, `"SHA-1"`, `"SM3"`, `"CRC32"` 等）。
- **`crypto.hmac`**：密钥 HMAC 签名计算（`algorithm`, `secret`, `text`）。
- **`crypto.encrypt` / `decrypt`**：AES 加密/解密（`algorithm`: `"AES"`, `key`, `text`）。
- **`crypto.random_bytes`**：生成指定长度的安全伪随机字节串。
- **`crypto.uuid`**：生成随机 UUID V4 唯一标识字符串。

---

### 13. 网络 HTTP 请求节点 (HTTP)

通用的网络 HTTP API 发送节点 `http.request`。

```json
{
  "type": "http.request",
  "config": {
    "url": "https://api.bgm.tv/v0/subjects/${vars.subjectId}",
    "method": "GET",
    "headers": {
      "User-Agent": "Xiaoyv/Bangumi-Client"
    },
    "timeoutMillis": 5000,
    "retryCount": 2,
    "outputKey": "apiResult"
  }
}
```

**输出格式**：包含 `statusCode` (如 200), `isSuccess` (true), `body` (自动解析后的响应体)。

---

### 14. 本地存储节点 (Storage)

在客户端本地 Preferences 进行数据的持久化存储与读取。

- **`storage.preferences_get`**：按 Key 读取本地存储值（`key`, `outputKey`）。
- **`storage.preferences_set`**：按 Key 保存数据到本地（`key`, `value`, `outputKey`）。
- **`storage.preferences_delete`**：删除本地指定 Key（`key`）。
- **`storage.preferences_has`**：校验本地是否存在 Key（`key`）。
- **`storage.preferences_clear`**：清空本地存储。

---

### 15. 系统与 UI 交互节点 (Action)

与 Android/KMP 客户端系统进行底层交互的动作节点。

| 节点类型 (Type)                    | 动作类型              | 参数键         |
|:-------------------------------|:------------------|:------------|
| **`action.open_external_url`** | 唤起系统浏览器打开 URL     | `url`       |
| **`action.open_external_app`** | 唤起第三方 App 协议      | `uri`       |
| **`action.open_internal_web`** | 在应用内 WebView 打开网页 | `url`       |
| **`action.show_toast`**        | 弹出系统 Toast 提示     | `message`   |
| **`action.write_clipboard`**   | 复制文本到系统剪贴板        | `text`      |
| **`action.read_clipboard`**    | 读取系统剪贴板文本         | `outputKey` |
| **`ui.confirm`**               | 弹出二次确认弹窗对话框       | `message`   |
| **`system.share`**             | 唤起系统原生的分享面板       | `text`      |
| **`system.notification`**      | 发送系统通知栏消息         | `content`   |
| **`system.vibrate`**           | 触发设备触觉震动反馈        | 无           |

---

## 节点扩展规范

扩充新节点类型时需遵循以下开发规范：

1. **类型常量声明**：在 `ActionNodeType` 中定义唯一的稳定节点类型标识符（如 `plugin.example_action`）。
2. **配置键定义**：在 `ActionNodeMetadata.kt` 中声明配置键、默认值及相关常量，避免魔法硬编码。
3. **节点规格定义**：使用 `ActionNodeSpec` 声明输入与输出端口、必需配置键、依赖能力及版本号。
4. **无状态执行器**：节点执行器基于 `ActionNodeExecutor` 实现，通过读取 `ActionExecutionContext` 计算并返回 `ActionNodeExecutionResult`。
5. **版本迁移机制**：当配置协议变更时，提升 `latestVersion` 并提供相对应的 `ActionNodeMigrator` 迁移逻辑。
6. **自动化测试集**：在 `BuiltInActionNodeTest` 中添加单节点覆盖测试，并在 `WorkflowSamples` 中注册可视化运行样例。

---

## 安全与权限规范

- **能力声明约束**：工作流通过 `requiredCapabilities` 声明所需能力，宿主在执行前进行权限校验与授权过滤。
- **凭据脱敏防护**：工作流配置中不保存 Token、密码或密钥等敏感凭据，仅保留引用名，真实凭据由宿主独立注入。
- **输入合法性校验**：外部 URL、应用协议及请求路径需通过宿主校验策略进行安全白名单校验。

---

## 测试与验证

模块提供了自动化的单元测试集，覆盖全部内置节点的规格校验、模板解析、数据转换及副作用抛出：

```bash
./gradlew :shared:data-workflow:jvmTest --no-daemon
./gradlew :features:workflows:compileKotlinJvm --no-daemon
```

单元测试通过 Fake/Mock 组件隔绝对外网络请求与本地存储访问，确保测试运行环境完全隔离与可重复。
