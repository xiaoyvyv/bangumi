# data-workflow

`data-workflow` 是 Bangumi Multiplatform 项目中的声明式工作流编排与执行引擎。模块基于有向无环图（DAG）模型，将网络请求、数据转换、流程分支、UI 交互及系统动作抽象为结构化协议，支持运行期调度与状态追踪。

---

## 目录

1. [架构与分包设计](#1-架构与分包设计)
   1. [分包结构](#11-分包结构)
   2. [模块职责与依赖约束](#12-模块职责与依赖约束)
2. [执行模型与调度架构](#2-执行模型与调度架构)
   1. [调度机制与生命周期](#21-调度机制与生命周期)
   2. [分支与合流 (Fork & Join)](#22-分支与合流-fork--join)
   3. [平台副作用与挂起执行](#23-平台副作用与挂起执行)
3. [模板与表达式引擎](#3-模板与表达式引擎)
   1. [命名空间访问](#31-命名空间访问)
   2. [运算符优先级与语法规则](#32-运算符优先级与语法规则)
   3. [类型转换与真值判定](#33-类型转换与真值判定)
4. [内置节点参考规范](#4-内置节点参考规范)
   1. [流程控制与分支 (`flow.*`)](#41-流程控制与分支-flow)
   2. [逻辑判断 (`control.*`)](#42-逻辑判断-control)
   3. [上下文与变量操作 (`data.*`)](#43-上下文与变量操作-data)
   4. [JSON 对象操作 (`object.*`)](#44-json-对象操作-object)
   5. [数组集合操作 (`array.*`)](#45-数组集合操作-array)
   6. [文本与正则处理 (`text.*`)](#46-文本与正则处理-text)
   7. [数值与数学计算 (`math.*`)](#47-数值与数学计算-math)
   8. [日期与时间 (`date.*`)](#48-日期与时间-date)
   9. [URL 解析与构建 (`url.*`)](#49-url-解析与构建-url)
   10. [结构化数据解析 (`json.*`, `xml.*`, `csv.*`)](#410-结构化数据解析-json-xml-csv)
   11. [编解码与摘要哈希 (`codec.*`, `crypto.*`)](#411-编解码与摘要哈希-codec-crypto)
   12. [HTML 解析与抽取 (`html.*`)](#412-html-解析与抽取-html)
   13. [网络与下载 (`http.*`)](#413-网络与下载-http)
   14. [本地持久化存储 (`storage.*`)](#414-本地持久化存储-storage)
   15. [文件系统沙箱 (`file.*`)](#415-文件系统沙箱-file)
   16. [客户端动作与 UI 交互 (`action.*`, `ui.*`, `system.*`, `image.*`, `video.*`)](#416-客户端动作与-ui-交互-action-ui-system-image-video)
5. [错误处理与诊断体系](#5-错误处理与诊断体系)
   1. [异常模型与继承层次](#51-异常模型与继承层次)
   2. [错误码分类规范](#52-错误码分类规范)
   3. [结构化错误输出协议](#53-结构化错误输出协议)
   4. [诊断追踪与日志监听](#54-诊断追踪与日志监听)
6. [接入与扩展开发](#6-接入与扩展开发)
   1. [依赖注入装配](#61-依赖注入装配)
   2. [执行与事件消费](#62-执行与事件消费)
   3. [自定义节点开发规范](#63-自定义节点开发规范)
   4. [测试验证](#64-测试验证)

---

## 1. 架构与分包设计

### 1.1 分包结构

`data-workflow` 采用分层领域架构，核心代码组织如下：

```text
com.xiaoyv.bangumi.shared.data.workflow/
  ├── model/                       # 领域数据模型
  │   ├── definition/              # 工作流拓扑定义 (ActionWorkflow, ActionNode, ActionEdge, ActionTrigger)
  │   ├── spec/                    # 规格常量与键名 (ActionNodeType, ActionCapability, ActionNodeKeys)
  │   ├── execution/               # 运行时状态 (ActionExecutionContext, ActionExecutionEvent, ActionNodeExecutionResult)
  │   └── log/                     # 日志与错误描述 (ActionExecutionLog, ActionExecutionStep, ActionExecutionError)
  ├── node/                        # 节点抽象与实现
  │   ├── core/                    # 注册中心与元信息 (ActionNodeRegistry, ActionNodeDefinition, ActionNodeSpec)
  │   ├── resolver/                # 表达式语法解析与插值 (ActionTemplateResolver, JsonPath)
  │   ├── effect/                  # 平台副作用接口定义 (ActionSideEffect, ActionSideEffectHandler)
  │   └── builtin/                 # 内置节点实现 (按 control, data, parse, io, extension 模块组织)
  ├── engine/                      # 运行时调度内核
  │   ├── ActionWorkflowValidator  # 工作流静态拓扑校验器
  │   ├── ActionSideEffectDispatcher # 副作用分发调度
  │   └── runtime/                 # 调度执行实现
  │       ├── ActionWorkflowEngine # 引擎主入口与事件流构造
  │       ├── WorkflowRuntimeGraph # 依赖分析与就绪队列计算
  │       ├── ActionParallelExecutor # 并发域执行与上下文归并
  │       ├── LoopExecutionController # 循环迭代栈管理
  │       ├── ActionWorkflowFailureRouter # failure 出口解析
  │       └── ActionExecutionEventEmitter # 终态事件与日志生成
  ├── exception/                   # 异常体系与诊断追踪 (ActionWorkflowException, ActionWorkflowTraceLogger)
  ├── port/                        # 外部基础设施抽象接口 (HttpClientProvider)
  ├── codec/                       # 格式序列化与协议版本迁移
  └── di/                          # Koin 依赖注入配置
```

### 1.2 模块职责与依赖约束

1. **单向依赖控制**：`engine.runtime` 仅依赖领域模型、节点注册中心及内部运行期组件，禁止反向依赖 UI 表现层与具体业务仓库。
2. **副作用抽象隔离**：平台相关能力（如窗口弹窗、剪贴板读写、系统浏览器调起）通过 `ActionSideEffect` 声明，由宿主环境实现并注入 `ActionSideEffectHandler`，保证引擎核心纯粹性。
3. **并发作用域限定**：`flow.parallel` 至其对应的 `flow.join` 构成封闭的局部并发域，由 `ActionParallelExecutor` 执行多路调度，工作流全局拓扑依然由 `ActionWorkflowEngine` 统一调度。

---

## 2. 执行模型与调度架构

### 2.1 调度机制与生命周期

引擎调度基于就绪队列状态机驱动。整体执行模型如下：

```text
       ┌────────────────────────┐
       │   入参校验与静态图检查   │ (ActionWorkflowValidator)
       └───────────┬────────────┘
                   ▼
       ┌────────────────────────┐
       │     初始化执行上下文     │ (ActionExecutionContext)
       └───────────┬────────────┘
                   ▼
  ┌───────────▶ 就绪队列提取节点 ◀───────────┐
  │            └───────────┬────────────┘           │
  │                        ▼                        │
  │            ┌────────────────────────┐           │
  │            │  配置插值与表达式求值   │           │
  │            └───────────┬────────────┘           │
  │                        ▼                        │
  │            ┌────────────────────────┐           │
  │            │       执行节点行为      │           │
  │            └───────────┬────────────┘           │
  │                        ▼                        │
  │            ┌────────────────────────┐           │
  │            │ 更新变量表与步骤历史记录 │           │
  │            └───────────┬────────────┘           │
  │                        ▼                        │
  │            ┌────────────────────────┐           │
  │            │ 下游控制边计算与入队   │───────────┘
  │            └───────────┬────────────┘
  │                        ▼
  │               队列为空或遇终止节点
  │                        ▼
  │            ┌────────────────────────┐
  └────────────│ 构造终态日志并派发事件 │
               └────────────────────────┘
```

1. **响应式事件流**：引擎执行入口返回 `Flow<ActionExecutionEvent>`，外部调用方通过订阅该流获取 `NodeStarted`、`NodeCompleted`、`SideEffectRequested`、`Completed` 及 `Failed` 等生命周期事件。
2. **就绪状态判定**：仅当节点的所有必要前置依赖分支均已完成或被满足时，节点才进入就绪队列。

### 2.2 分支与合流 (Fork & Join)

- **分支 (Fan-Out)**：节点的单一输出端口支持连接多条出边。上游端口触发后，所有关联的出边目标节点按就绪状态调度执行。
- **合流 (Fan-In)**：节点的输入端口支持连接多条入边。当节点存在多个活跃前置路径时，调度器等待所有处于激活状态的前置分支执行完毕（`pendingPredecessors == 0`
  ）后，该合流节点仅被触发一次。未被激活的条件分支自动跳过，不阻塞合流节点的唤醒。

### 2.3 平台副作用与挂起执行

当节点执行需要与客户端环境交互（如二次确认、文本输入、对话框展示）时，执行器生成 `ActionSideEffect` 并挂起：

1. 引擎触发 `ActionExecutionEvent.SideEffectRequested` 并调用 `ActionSideEffectHandler.handle(effect)`。
2. 宿主端（如 Compose UI）消费该事件并渲染对应界面。
3. 用户完成交互后，Handler 返回响应结果，协程恢复并继续后续节点流程。

---

## 3. 模板与表达式引擎

工作流节点配置项中支持使用 `${...}` 占位符引用动态变量或内联表达式，由 `ActionTemplateResolver` 负责语法解析与求值。

### 3.1 命名空间访问

表达式支持通过点号（`.`）与下标（`[...]`）访问以下 6 个命名空间：

| 命名空间              | 作用说明                             | 访问示例                                   |
|:------------------|:---------------------------------|:---------------------------------------|
| **`input`**       | 外部触发工作流时传入的只读业务输入数据              | `${input.subjectId}`                   |
| **`environment`** | 宿主平台只读环境上下文（语言、平台版本等）            | `${environment.platform}`              |
| **`trigger`**     | 触发源元数据（触发类型、来源组件等）               | `${trigger.name}`                      |
| **`vars`**        | 工作流全局运行时变量字典                     | `${vars.pageIndex}`、`${vars.items[0]}` |
| **`steps`**       | 已执行节点的输出结果集合（以节点 ID 为索引）         | `${steps.http_get.body.data}`          |
| **`loop`**        | 当前所在最内层循环帧数据（含 `item` 与 `index`） | `${loop.item.id}`、`${loop.index}`      |

### 3.2 运算符优先级与语法规则

表达式解析器按以下优先级顺序（从低到高）求值：

| 优先级    | 运算类型         | 运算符 / 语法                     | 示例                                                |
|:-------|:-------------|:-----------------------------|:--------------------------------------------------|
| **1**  | 条件选择         | `? :` (三元运算符)                | `${vars.score >= 60 ? "及格" : "未及格"}`              |
| **2**  | 空值兜底         | `?:` (Elvis 运算符)             | `${vars.name ?: "默认值"}`                           |
| **3**  | 逻辑或          | `\|\|`                       | `${vars.a \|\| vars.b}`                           |
| **4**  | 逻辑与          | `&&`                         | `${vars.isReady && vars.hasMore}`                 |
| **5**  | 等值判断         | `==`, `!=`                   | `${vars.status == 200}`                           |
| **6**  | 比较运算         | `>`, `>=`, `<`, `<=`         | `${vars.count > 0}`                               |
| **7**  | 加法 / 减法      | `+`, `-` (二元加减与字符串拼接)        | `${vars.offset + 10}`、`"Page: " + vars.page`      |
| **8**  | 乘法 / 除法 / 取模 | `*`, `/`, `%`                | `${vars.width * vars.height}`、`${loop.index % 2}` |
| **9**  | 一元运算         | `!`, `-` (逻辑非 / 取负)          | `${!vars.enabled}`、`-${vars.delta}`               |
| **10** | 成员访问         | `.length`, `.trim` 等通用属性     | `${vars.title.length}`                            |
| **11** | 属性与下标访问      | `.property`, `[index]`       | `${steps.req.body.list[0]}`                       |
| **12** | 括号与字面量       | `(...)`, 字符串, 数字, 布尔, `null` | `${(vars.a + vars.b) * 2}`、`'text'`、`123`         |

### 3.3 类型转换与真值判定

1. **数值强转**：字符串数值参与数学运算或数值比较时自动转换为浮点数处理；整数运算结果自动规整为整型。
2. **字符串连接**：二元 `+` 运算中若任一操作数为字符串，另一操作数自动转为字符串后连接。
3. **真值判定 (Truthiness)**：在逻辑运算符与条件节点中，以下值判定为 `false`，其余值均判定为 `true`：
   - `null` / `JsonNull`
   - 布尔值 `false`
   - 数字 `0` 或 `0.0`
   - 空字符串 `""`
   - 空数组 `[]`
   - 空对象 `{}`

---

## 4. 内置节点参考规范

### 4.1 流程控制与分支 (`flow.*`)

| 节点类型                  | 功能说明                  | 主要配置键                            | 输出端口                       |
|:----------------------|:----------------------|:---------------------------------|:---------------------------|
| **`flow.start`**      | 工作流执行起点               | 无                                | `next`                     |
| **`flow.end`**        | 工作流正常终止点              | 无                                | 无                          |
| **`flow.delay`**      | 阻塞挂起指定时长              | `delayMillis` (Long)             | `next`                     |
| **`flow.stop`**       | 中断并退出工作流              | `message` (String, 可选)           | 无                          |
| **`flow.assert`**     | 条件断言，表达式为 false 时抛出异常 | `condition` (Boolean/String)     | `next`                     |
| **`flow.switch`**     | 多分支条件路由               | `cases` (Map<String, String>)    | 匹配的 branch key 或 `default` |
| **`flow.log`**        | 打印调试日志信息              | `message`, `level`               | `next`                     |
| **`flow.debug`**      | 调试断点信息输出              | `message`                        | `next`                     |
| **`flow.try`**        | 异常捕获保护作用域起点           | 无                                | `try`                      |
| **`flow.catch`**      | 异常处理分支入口              | 无                                | `catch`                    |
| **`flow.finally`**    | 最终执行收尾分支入口            | 无                                | `finally`                  |
| **`flow.call`**       | 调用子工作流                | `workflowId`, `outputKey`        | `next`                     |
| **`flow.return`**     | 子工作流返回数据              | `output`                         | 无                          |
| **`flow.parallel`**   | 并行分支起点                | 无                                | `branches`                 |
| **`flow.join`**       | 并行分支汇合点               | `values`                         | `next`                     |
| **`flow.retry`**      | 失败重试执行器               | `retryCount`, `retryDelayMillis` | `next`                     |
| **`flow.timeout`**    | 超时控制作用域               | `timeoutMillis`                  | `next`                     |
| **`flow.rate_limit`** | 限流等待控制                | `delayMillis`                    | `next`                     |

### 4.2 逻辑判断 (`control.*`)

| 节点类型                                 | 功能说明                     | 必需配置键                        | 输出数据                                        |
|:-------------------------------------|:-------------------------|:-----------------------------|:--------------------------------------------|
| **`control.if`**                     | 条件分支路由                   | `condition`                  | 沿 `matched` (true) 或 `default` (false) 端口输出 |
| **`control.equals`**                 | 相等比较 (`left == right`)   | `left`, `right`, `outputKey` | 布尔值                                         |
| **`control.not_equals`**             | 不等比较 (`left != right`)   | `left`, `right`, `outputKey` | 布尔值                                         |
| **`control.greater_than`**           | 大于比较 (`left > right`)    | `left`, `right`, `outputKey` | 布尔值                                         |
| **`control.greater_than_or_equals`** | 大于等于比较 (`left >= right`) | `left`, `right`, `outputKey` | 布尔值                                         |
| **`control.less_than`**              | 小于比较 (`left < right`)    | `left`, `right`, `outputKey` | 布尔值                                         |
| **`control.less_than_or_equals`**    | 小于等于比较 (`left <= right`) | `left`, `right`, `outputKey` | 布尔值                                         |
| **`control.and`**                    | 逻辑与 (`left && right`)    | `left`, `right`, `outputKey` | 布尔值                                         |
| **`control.or`**                     | 逻辑或 (`left \|\| right`)  | `left`, `right`, `outputKey` | 布尔值                                         |
| **`control.not`**                    | 逻辑非 (`!value`)           | `value`, `outputKey`         | 布尔值                                         |
| **`control.is_null`**                | 空值判断 (`value == null`)   | `value`, `outputKey`         | 布尔值                                         |
| **`control.is_empty`**               | 空集合或空字符串判断               | `value`, `outputKey`         | 布尔值                                         |

### 4.3 上下文与变量操作 (`data.*`)

| 节点类型                  | 功能说明          | 配置参数                                 | 写入目标               |
|:----------------------|:--------------|:-------------------------------------|:-------------------|
| **`data.set_var`**    | 设置单个变量        | `key`, `value`                       | `vars.<key>`       |
| **`data.get_var`**    | 读取单个变量        | `key`, `outputKey`                   | `vars.<outputKey>` |
| **`data.remove_var`** | 删除单个变量        | `key`                                | 移除 `vars.<key>`    |
| **`data.merge_vars`** | 合并字典至全局变量表    | `values`                             | 写入 `vars`          |
| **`data.clear_vars`** | 清空全局变量表       | 无                                    | 清空 `vars`          |
| **`data.map_fields`** | 对对象字段进行提取与重命名 | `object`, `assignments`, `outputKey` | `vars.<outputKey>` |
| **`data.template`**   | 文本模版渲染        | `template`, `outputKey`              | `vars.<outputKey>` |
| **`data.to_number`**  | 强制转换为数值类型     | `value`, `outputKey`                 | `vars.<outputKey>` |
| **`data.to_string`**  | 强制转换为字符串类型    | `value`, `outputKey`                 | `vars.<outputKey>` |
| **`data.to_boolean`** | 强制转换为布尔类型     | `value`, `outputKey`                 | `vars.<outputKey>` |
| **`data.type_of`**    | 获取数据类型描述      | `value`, `outputKey`                 | `vars.<outputKey>` |
| **`data.uuid`**       | 生成 UUID 字符串   | `outputKey`                          | `vars.<outputKey>` |

### 4.4 JSON 对象操作 (`object.*`)

| 节点类型                      | 功能说明                       | 必需配置键                                 | 输出数据    |
|:--------------------------|:---------------------------|:--------------------------------------|:--------|
| **`object.get`**          | 读取深层路径属性                   | `object`, `path`, `outputKey`         | 属性值     |
| **`object.set`**          | 设置对象键值对                    | `object`, `key`, `value`, `outputKey` | 新对象     |
| **`object.remove`**       | 删除对象指定键                    | `object`, `key`, `outputKey`          | 新对象     |
| **`object.omit`**         | 剔除指定键列表                    | `object`, `keys`, `outputKey`         | 新对象     |
| **`object.pick`**         | 仅保留指定键列表                   | `object`, `keys`, `outputKey`         | 新对象     |
| **`object.merge`**        | 合并多个对象                     | `objects`, `outputKey`                | 合并后的新对象 |
| **`object.keys`**         | 获取所有键名列表                   | `object`, `outputKey`                 | 字符串数组   |
| **`object.values`**       | 获取所有属性值列表                  | `object`, `outputKey`                 | 元素数组    |
| **`object.entries`**      | 转换为键值对数组 (`[[k, v], ...]`) | `object`, `outputKey`                 | 二维数组    |
| **`object.from_entries`** | 键值对数组还原为对象                 | `entries`, `outputKey`                | 对象      |
| **`object.has_key`**      | 判断是否包含指定键                  | `object`, `key`, `outputKey`          | 布尔值     |
| **`object.is_empty`**     | 判断是否为空对象 (`{}`)            | `object`, `outputKey`                 | 布尔值     |

### 4.5 数组集合操作 (`array.*`)

| 节点类型                        | 功能说明          | 必需配置键                                           | 输出数据         |
|:----------------------------|:--------------|:------------------------------------------------|:-------------|
| **`array.length`**          | 获取数组长度        | `values`, `outputKey`                           | 整数           |
| **`array.create`**          | 创建数组          | `values`, `outputKey`                           | 数组           |
| **`array.append`**          | 尾部添加元素        | `values`, `value`, `outputKey`                  | 新数组          |
| **`array.insert_at`**       | 指定位置插入元素      | `values`, `index`, `value`, `outputKey`         | 新数组          |
| **`array.remove_at`**       | 删除指定下标元素      | `values`, `index`, `outputKey`                  | 新数组          |
| **`array.filter`**          | 按条件过滤元素       | `values`, `operator`, `expected`, `outputKey`   | 过滤后的新数组      |
| **`array.map`**             | 提取元素指定字段生成新数组 | `values`, `fieldPath`, `outputKey`              | 新数组          |
| **`array.flat_map`**        | 提取字段并扁平化      | `values`, `fieldPath`, `outputKey`              | 一维新数组        |
| **`array.concat`**          | 拼接多个数组        | `values`, `outputKey`                           | 拼接后的新数组      |
| **`array.zip`**             | 双数组打包为元组数组    | `values`, `otherValues`, `outputKey`            | 二维元组数组       |
| **`array.take`**            | 获取前 N 个元素     | `values`, `count`, `outputKey`                  | 截取后的新数组      |
| **`array.drop`**            | 跳过前 N 个元素     | `values`, `count`, `outputKey`                  | 截取后的新数组      |
| **`array.contains`**        | 检查是否包含目标元素    | `values`, `value`, `outputKey`                  | 布尔值          |
| **`array.find`**            | 查找首个满足条件的元素   | `values`, `fieldPath`, `expected`, `outputKey`  | 元素或 `null`   |
| **`array.distinct`**        | 元素去重          | `values`, `outputKey`                           | 去重后的新数组      |
| **`array.sort`**            | 数组排序          | `values`, `outputKey`                           | 排序后的新数组      |
| **`array.reverse`**         | 反转数组          | `values`, `outputKey`                           | 反转后的新数组      |
| **`array.slice`**           | 切片截取区间元素      | `values`, `startIndex`, `endIndex`, `outputKey` | 截取后的新数组      |
| **`array.flatten`**         | 嵌套数组扁平化       | `values`, `outputKey`                           | 一维数组         |
| **`array.group_by`**        | 按指定字段分组       | `values`, `fieldPath`, `outputKey`              | 键值映射字典       |
| **`array.first`**           | 获取首个元素        | `values`, `outputKey`                           | 元素或 `null`   |
| **`array.last`**            | 获取末尾元素        | `values`, `outputKey`                           | 元素或 `null`   |
| **`array.sum`**             | 数值求和          | `values`, `outputKey`                           | 数值           |
| **`array.avg`**             | 数值求平均值        | `values`, `outputKey`                           | 数值           |
| **`array.min`** / **`max`** | 查找最小值 / 最大值   | `values`, `outputKey`                           | 数值           |
| **`array.chunk`**           | 数组按大小分块       | `values`, `size`, `outputKey`                   | 二维数组         |
| **`array.shuffle`**         | 随机打乱顺序        | `values`, `outputKey`                           | 新数组          |
| **`array.sample`**          | 随机采样单个元素      | `values`, `outputKey`                           | 元素或 `null`   |
| **`array.index_of`**        | 查找元素首个下标      | `values`, `value`, `outputKey`                  | 索引值（未找到为 -1） |
| **`array.intersection`**    | 两个数组求交集       | `values`, `otherValues`, `outputKey`            | 交集数组         |
| **`array.difference`**      | 两个数组求差集       | `values`, `otherValues`, `outputKey`            | 差集数组         |

### 4.6 文本与正则处理 (`text.*`)

| 节点类型                        | 功能说明        | 配置参数                                             | 输出数据        |
|:----------------------------|:------------|:-------------------------------------------------|:------------|
| **`text.length`**           | 计算字符长度      | `text`, `outputKey`                              | 整数          |
| **`text.trim`**             | 去除首尾空白字符    | `text`, `outputKey`                              | 字符串         |
| **`text.lowercase`**        | 转为小写字母      | `text`, `outputKey`                              | 字符串         |
| **`text.uppercase`**        | 转为大写字母      | `text`, `outputKey`                              | 字符串         |
| **`text.capitalize`**       | 首字母大写       | `text`, `outputKey`                              | 字符串         |
| **`text.repeat`**           | 重复拼接 N 次    | `text`, `count`, `outputKey`                     | 字符串         |
| **`text.reverse`**          | 反转字符顺序      | `text`, `outputKey`                              | 字符串         |
| **`text.index_of`**         | 查找子串首个下标    | `text`, `pattern`, `outputKey`                   | 整数（未找到为 -1） |
| **`text.template`**         | 变量插值渲染      | `template`, `object`, `outputKey`                | 渲染后的字符串     |
| **`text.split`**            | 按分隔符切分为数组   | `text`, `delimiter`, `outputKey`                 | 字符串数组       |
| **`text.regex_match`**      | 正则匹配与分组捕获   | `text`, `pattern`, `outputKey`                   | 匹配结果对象      |
| **`text.match_all`**        | 正则全局匹配列表    | `text`, `pattern`, `outputKey`                   | 匹配项数组       |
| **`text.substring`**        | 按照下标截取子串    | `text`, `startIndex`, `endIndex`, `outputKey`    | 字符串         |
| **`text.substring_before`** | 截取分隔符之前的文本  | `text`, `delimiter`, `outputKey`                 | 字符串         |
| **`text.substring_after`**  | 截取分隔符之后的文本  | `text`, `delimiter`, `outputKey`                 | 字符串         |
| **`text.replace`**          | 静态字符替换      | `text`, `pattern`, `replacement`, `outputKey`    | 字符串         |
| **`text.replace_regex`**    | 正则表达式替换     | `text`, `pattern`, `replacement`, `outputKey`    | 字符串         |
| **`text.join`**             | 使用指定连接符拼接数组 | `values`, `separator`, `outputKey`               | 字符串         |
| **`text.pad`**              | 文本填充对齐      | `text`, `padLength`, `padCharacter`, `outputKey` | 字符串         |
| **`text.format_number`**    | 数值格式化小数位    | `value`, `fractionDigits`, `outputKey`           | 字符串         |
| **`text.contains`**         | 检查是否包含子串    | `text`, `pattern`, `outputKey`                   | 布尔值         |
| **`text.starts_with`**      | 检查是否以前缀开头   | `text`, `pattern`, `outputKey`                   | 布尔值         |
| **`text.ends_with`**        | 检查是否以后缀结尾   | `text`, `pattern`, `outputKey`                   | 布尔值         |
| **`text.slugify`**          | 生成 URL 别名格式 | `text`, `outputKey`                              | 字符串         |
| **`text.truncate`**         | 超长截断与省略号补充  | `text`, `limit`, `outputKey`                     | 截断字符串       |

### 4.7 数值与数学计算 (`math.*`)

| 节点类型                       | 功能说明                     | 配置参数                               | 输出数据           |
|:---------------------------|:-------------------------|:-----------------------------------|:---------------|
| **`math.add`**             | 加法运算 (`left + right`)    | `left`, `right`, `outputKey`       | 数值             |
| **`math.subtract`**        | 减法运算 (`left - right`)    | `left`, `right`, `outputKey`       | 数值             |
| **`math.multiply`**        | 乘法运算 (`left * right`)    | `left`, `right`, `outputKey`       | 数值             |
| **`math.divide`**          | 除法运算 (`left / right`)    | `left`, `right`, `outputKey`       | 数值（除数为 0 抛出异常） |
| **`math.modulo`**          | 取模运算 (`left % right`)    | `left`, `right`, `outputKey`       | 数值（模数为 0 抛出异常） |
| **`math.min`** / **`max`** | 取两数中较小 / 较大值             | `left`, `right`, `outputKey`       | 数值             |
| **`math.pow`**             | 幂运算 ($left^{right}$)     | `left`, `right`, `outputKey`       | 数值             |
| **`math.sqrt`**            | 平方根运算 ($\sqrt{x}$)       | `value`, `outputKey`               | 数值（负数开方抛出异常）   |
| **`math.sum`**             | 列表元素累加求和                 | `values`, `outputKey`              | 数值             |
| **`math.avg`**             | 列表元素计算平均值                | `values`, `outputKey`              | 数值             |
| **`math.log`**             | 计算自然对数 $\ln(x)$          | `value`, `outputKey`               | 数值             |
| **`math.exp`**             | 计算自然指数 $e^x$             | `value`, `outputKey`               | 数值             |
| **`math.negate`**          | 数值取反 (`-value`)          | `value`, `outputKey`               | 数值             |
| **`math.round`**           | 四舍五入保留指定小数位              | `value`, `decimals`, `outputKey`   | 数值             |
| **`math.floor`**           | 向下取整                     | `value`, `outputKey`               | 整数             |
| **`math.ceil`**            | 向上取整                     | `value`, `outputKey`               | 整数             |
| **`math.abs`**             | 计算绝对值                    | `value`, `outputKey`               | 数值             |
| **`math.random`**          | 生成指定区间 `[min, max)` 伪随机数 | `min`, `max`, `outputKey`          | 数值             |
| **`math.clamp`**           | 数值区间截断约束                 | `value`, `min`, `max`, `outputKey` | 数值             |

### 4.8 日期与时间 (`date.*`)

| 节点类型                     | 功能说明                 | 必需配置键                                                  | 输出数据                 |
|:-------------------------|:---------------------|:-------------------------------------------------------|:---------------------|
| **`date.now`**           | 获取系统当前时间戳 (毫秒)       | `outputKey`                                            | Long 毫秒值             |
| **`date.format`**        | 时间戳格式化为 ISO 8601 字符串 | `timestamp`, `outputKey`                               | 格式化日期字符串             |
| **`date.parse`**         | 日期字符串解析为时间戳          | `text`, `outputKey`                                    | Long 毫秒值             |
| **`date.add`**           | 增加指定时间跨度             | `timestamp`, `count`, `unit`, `outputKey`              | Long 毫秒值             |
| **`date.subtract`**      | 减少指定时间跨度             | `timestamp`, `count`, `unit`, `outputKey`              | Long 毫秒值             |
| **`date.diff`**          | 计算两时间戳差值             | `timestampLeft`, `timestampRight`, `unit`, `outputKey` | 数值                   |
| **`date.relative_time`** | 转换为相对时间描述            | `timestamp`, `outputKey`                               | 相对时间字符串 (如 `"5分钟前"`) |
| **`date.get_component`** | 提取时间分量 (年月日时分秒)      | `timestamp`, `outputKey`                               | 时间分量字典               |

### 4.9 URL 解析与构建 (`url.*`)

| 节点类型                      | 功能说明                    | 必需配置键                                     | 输出数据           |
|:--------------------------|:------------------------|:------------------------------------------|:---------------|
| **`url.parse`**           | 解析 URL 结构 (协议、域名、路径、参数) | `url`, `outputKey`                        | URL 组成对象       |
| **`url.build`**           | 由基准路径与参数对象构造完整 URL      | `baseUrl`, `queryParameters`, `outputKey` | 完整 URL 字符串     |
| **`url.set_query_param`** | 设置或覆盖 URL 查询参数          | `url`, `key`, `value`, `outputKey`        | 新 URL 字符串      |
| **`url.get_query_param`** | 读取 URL 指定查询参数值          | `url`, `key`, `outputKey`                 | 参数值字符串或 `null` |

### 4.10 结构化数据解析 (`json.*`, `xml.*`, `csv.*`)

| 节点类型                 | 功能说明                  | 必需配置键                         | 输出数据                             |
|:---------------------|:----------------------|:------------------------------|:---------------------------------|
| **`json.parse`**     | JSON 文本反序列化为对象或数组     | `text`, `outputKey`           | JsonElement 结构                   |
| **`json.stringify`** | 对象序列化为 JSON 文本        | `value`, `outputKey`          | 字符串                              |
| **`json.extract`**   | 按 JSONPath 表达式提取节点    | `source`, `path`, `outputKey` | 匹配到的节点值                          |
| **`json.validate`**  | 校验文本是否为合法 JSON        | `text`, `outputKey`           | 布尔值                              |
| **`xml.parse`**      | XML/RSS 文本解析为 JSON 结构 | `text`, `outputKey`           | JsonObject 结构                    |
| **`xml.stringify`**  | JSON 结构序列化为 XML 文本    | `data`, `outputKey`           | XML 字符串                          |
| **`csv.parse`**      | CSV 表格解析为对象记录数组       | `text`, `outputKey`           | 字典列表 `List<Map<String, String>>` |
| **`csv.stringify`**  | 记录数组转换为 CSV 文本        | `items`, `outputKey`          | CSV 格式文本                         |

### 4.11 编解码与摘要哈希 (`codec.*`, `crypto.*`)

| 节点类型                                         | 功能说明                                | 必需配置键                                      | 输出数据          |
|:---------------------------------------------|:------------------------------------|:-------------------------------------------|:--------------|
| **`codec.base64_encode`** / **`decode`**     | 标准 Base64 编解码                       | `text`, `outputKey`                        | 字符串           |
| **`codec.base64_url_encode`** / **`decode`** | URL 安全的 Base64 编解码                  | `text`, `outputKey`                        | 字符串           |
| **`codec.hex_encode`** / **`decode`**        | 十六进制 (Hex) 编解码                      | `text`, `outputKey`                        | 字符串           |
| **`codec.url_encode`** / **`decode`**        | URL 百分号编解码                          | `text`, `outputKey`                        | 字符串           |
| **`codec.html_escape`** / **`unescape`**     | HTML 字符实体转义与反转义                     | `text`, `outputKey`                        | 字符串           |
| **`crypto.hash`**                            | 计算哈希摘要 (支持 SHA-256, SHA-512, MD5 等) | `text`, `algorithm`, `outputKey`           | 哈希十六进制字符串     |
| **`crypto.hmac`**                            | 计算 HMAC 签名                          | `text`, `secret`, `algorithm`, `outputKey` | 签名十六进制字符串     |
| **`crypto.encrypt`** / **`decrypt`**         | AES 对称加密与解密                         | `text`, `key`, `algorithm`, `outputKey`    | 密文 / 明文字符串    |
| **`crypto.random_bytes`**                    | 生成指定长度伪随机字节串                        | `length`, `outputKey`                      | 十六进制随机字符串     |
| **`crypto.uuid`**                            | 生成标准 UUID V4 标识                     | `outputKey`                                | 36 位 UUID 字符串 |

### 4.12 HTML 解析与抽取 (`html.*`)

HTML 解析节点基于 **Ksoup** 引擎构建，负责 DOM 树的解析、选择器检索、层次遍历与数据抽取。

#### 数据契约与行为规范

- **输入类型**：支持标准 HTML 文档、HTML 片段字符串或 HTML 字符串数组。
- **输出格式**：
   - 元素定位（如 `html.parse`、`html.select_first`、`html.parent` 等）：输出为 HTML 字符串（未匹配时为 `null`）。
   - 元素集合（如 `html.select`、`html.children` 等）：输出为 HTML 字符串数组 `List<String>`（未匹配时为空数组 `[]`）。
   - 属性与内容提取（如 `html.text`、`html.attr` 等）：输入为单元素时输出对应标量值；输入为元素数组时输出对应的标量值数组。
- **选择器规范**：支持标准 CSS 选择器语法，语法非法时抛出异常。

#### 节点规格详情

| 节点类型                     | 功能说明                              | 配置参数                                             | 输出数据                             |
|:-------------------------|:----------------------------------|:-------------------------------------------------|:---------------------------------|
| **`html.parse`**         | 解析 HTML 源码并输出规范化文档字符串             | `html` / `source` (String), `outputKey`          | Document 完整 HTML 字符串             |
| **`html.remove`**        | 根据 CSS 选择器剔除匹配标签                  | `source`, `selector` (String), `outputKey`       | 剔除后的 HTML 字符串                    |
| **`html.select`**        | 执行 CSS 选择器匹配，返回所有匹配项的 HTML 源码列表   | `source`, `selector`, `outputKey`                | 匹配元素的 HTML 字符串数组 `List<String>`  |
| **`html.select_first`**  | 执行 CSS 选择器匹配，返回首个匹配项的 HTML 源码     | `source`, `selector`, `outputKey`                | 首个匹配项 HTML 字符串；未匹配为 `null`       |
| **`html.parent`**        | 获取当前元素的直接父级元素 HTML                | `source`, `outputKey`                            | 父级 HTML 字符串；无父级为 `null`          |
| **`html.children`**      | 获取当前元素的全部直接子元素 HTML 列表            | `source`, `outputKey`                            | 子元素 HTML 字符串数组                   |
| **`html.first`**         | 获取元素集合中的首个元素                      | `source`, `outputKey`                            | 首个元素 HTML；空集合为 `null`            |
| **`html.last`**          | 获取元素集合中的末尾元素                      | `source`, `outputKey`                            | 末尾元素 HTML；空集合为 `null`            |
| **`html.get`**           | 按 0 起始索引获取集合中指定位置元素               | `source`, `index` (Int), `outputKey`             | 目标元素 HTML；越界为 `null`             |
| **`html.size`**          | 统计元素集合中包含的节点数量                    | `source`, `outputKey`                            | 元素数量 (Int)                       |
| **`html.attr`**          | 提取指定属性名称的值                        | `source`, `attribute` (String), `outputKey`      | 属性值字符串（多元素时为字符串数组）               |
| **`html.tag`**           | 提取元素的小写标签名称                       | `source`, `outputKey`                            | 标签名字符串（多元素时为字符串数组）               |
| **`html.text`**          | 提取节点及其子树的纯文本内容                    | `source`, `outputKey`                            | 纯文本字符串（多元素时为字符串数组）               |
| **`html.data`**          | 提取 `<script>` 或 `<style>` 标签的内部数据 | `source`, `outputKey`                            | 原始字符数据字符串                        |
| **`html.value`**         | 提取表单输入控件的值                        | `source`, `outputKey`                            | 控件取值字符串                          |
| **`html.id`**            | 提取元素的 `id` 属性                     | `source`, `outputKey`                            | ID 字符串（未声明为空字符串）                 |
| **`html.html`**          | 提取元素的内部 HTML (`innerHTML`)        | `source`, `outputKey`                            | 内部 HTML 字符串                      |
| **`html.outer_html`**    | 提取包含标签自身的完整 HTML (`outerHTML`)    | `source`, `outputKey`                            | 完整 HTML 字符串                      |
| **`html.has_class`**     | 校验元素是否包含指定 CSS 类名                 | `source`, `className` (String), `outputKey`      | 布尔值 `true` / `false`             |
| **`html.map`**           | 对集合内每个元素执行指定提取操作并收集为数组            | `source`, `operation`, `attribute?`, `outputKey` | 结果数组 `JsonArray<String>`         |
| **`html.table_to_json`** | 将 `<table>` 元素解析为结构化字典列表          | `html`, `selector?` (默认 `"table"`), `outputKey`  | 字典数组 `List<Map<String, String>>` |

### 4.13 网络与下载 (`http.*`)

| 节点类型                | 功能说明             | 配置参数                                                                                                               | 输出结构说明                                                                  |
|:--------------------|:-----------------|:-------------------------------------------------------------------------------------------------------------------|:------------------------------------------------------------------------|
| **`http.request`**  | 发起 HTTP/HTTPS 请求 | `url`, `method` (GET/POST/PUT/DELETE), `headers?`, `query?`, `body?`, `timeoutMillis?`, `retryCount?`, `outputKey` | 包含 `statusCode` (Int), `isSuccess` (Boolean), `body` (解析后对象), `headers` |
| **`http.download`** | 下载远程网络文件并写入沙箱目录  | 同 `http.request`，附加 `path` (保存目录), `fileName?` (可选文件名), `outputKey`                                                | 包含 `statusCode`, `isSuccess`, `filePath` (沙箱相对路径), `fileName`           |

### 4.14 本地持久化存储 (`storage.*`)

| 节点类型                             | 功能说明                | 配置参数                        | 访问目标             |
|:---------------------------------|:--------------------|:----------------------------|:-----------------|
| **`storage.preferences_get`**    | 读取 Preferences 存储数据 | `key`, `outputKey`          | 读取本地 Preferences |
| **`storage.preferences_set`**    | 写入 Preferences 键值对  | `key`, `value`, `outputKey` | 写入本地 Preferences |
| **`storage.preferences_delete`** | 删除指定 Preferences 键  | `key`                       | 移除指定 Key         |
| **`storage.preferences_has`**    | 检查指定键是否存在           | `key`, `outputKey`          | 返回布尔值            |
| **`storage.preferences_clear`**  | 清空当前工作流的本地存储        | 无                           | 清空 Preferences   |

### 4.15 文件系统沙箱 (`file.*`)

所有文件系统节点均运行于沙箱隔离环境中，仅允许访问工作流独占的 `workflowId/` 目录。路径越界或包含 `..` 穿越行为将被拒绝并抛出 `file_access_denied` 错误。

| 节点类型                             | 功能说明                          | 必需配置键                                  |
|:---------------------------------|:------------------------------|:---------------------------------------|
| **`file.read_text`**             | 读取沙箱内 UTF-8 文本文件              | `path`, `outputKey`                    |
| **`file.write_text`**            | 写入 UTF-8 文本（支持 `append` 追加模式） | `path`, `text`, `append?`, `outputKey` |
| **`file.create`**                | 创建空文件（不覆盖已有文件）                | `path`, `outputKey`                    |
| **`file.get_working_directory`** | 获取当前工作流沙箱目录的绝对路径              | `outputKey`                            |
| **`file.delete`**                | 删除沙箱内的文件或空目录                  | `path`, `outputKey`                    |
| **`file.exists`**                | 检查指定路径的文件或目录是否存在              | `path`, `outputKey`                    |
| **`file.mkdir`**                 | 递归创建目录                        | `path`, `outputKey`                    |
| **`file.list`**                  | 列举指定目录下的全部子项名称                | `path`, `outputKey`                    |
| **`file.copy`**                  | 复制文件或目录                       | `fromPath`, `toPath`, `outputKey`      |
| **`file.move`**                  | 移动或重命名文件或目录                   | `fromPath`, `toPath`, `outputKey`      |
| **`file.compress_zip`**          | 将文件或目录列表压缩为 ZIP 包             | `paths`, `toPath`, `outputKey`         |
| **`file.extract_zip`**           | 安全解压 ZIP 包（带包大小与解压条目限制）       | `fromPath`, `toPath`, `outputKey`      |

### 4.16 客户端动作与 UI 交互 (`action.*`, `ui.*`, `system.*`, `image.*`, `video.*`)

#### 客户端系统动作节点 (`action.*`)

| 节点类型                           | 功能说明                            | 配置参数                 | 触发机制             | 所需能力                                     |
|:-------------------------------|:--------------------------------|:---------------------|:-----------------|:-----------------------------------------|
| **`action.open_internal_web`** | 在应用内置 WebView 容器中打开网页           | `url` (必填), `title?` | 派发 UI 路由副作用      | `NETWORK`                                |
| **`action.open_external_url`** | 唤起系统默认浏览器打开 URL                 | `url` (必填)           | 派发系统浏览器调用副作用     | 无                                        |
| **`action.open_external_app`** | 唤起第三方 App 协议或 URI Scheme        | `uri` (必填)           | 派发系统协议调用副作用      | 无                                        |
| **`action.sync_cookie`**       | 打开内置 WebView 交互登录并同步 Cookie 至本地 | `url` (必填), `title?` | 同步写入本地 CookieJar | `NETWORK`, `NETWORK_LOCAL_COOKIE_ACCESS` |
| **`action.show_toast`**        | 弹出系统 Toast 轻量提示                 | `message` (必填)       | 派发 UI Toast 副作用  | 无                                        |
| **`action.write_clipboard`**   | 写入文本到系统剪贴板                      | `text` (必填)          | 派发系统剪贴板写入副作用     | `CLIPBOARD_WRITE`                        |
| **`action.read_clipboard`**    | 读取系统剪贴板文本                       | `outputKey`          | 派发系统剪贴板读取副作用     | `CLIPBOARD_READ`                         |

#### 用户界面交互对话框节点 (`ui.*`)

| 节点类型                      | 功能说明               | 配置参数                                                                                      | 控制流与输出行为                             |
|:--------------------------|:-------------------|:------------------------------------------------------------------------------------------|:-------------------------------------|
| **`ui.confirm`**          | 弹出二次确认弹窗，挂起等待用户操作  | `message` (必填), `title?`                                                                  | 确认沿 `success` 端口继续；取消沿 `cancel` 端口继续 |
| **`ui.input_dialog`**     | 弹出单行文本输入框，挂起等待用户输入 | `title?`, `defaultValue?`, `outputKey`                                                    | `output.<outputKey>` 接收用户输入的字符串      |
| **`ui.select_dialog`**    | 弹出单选列表对话框供用户选择     | `options` (Array<Object>), `title?`, `outputKey`                                          | `output.<outputKey>` 接收选中的选项值        |
| **`ui.progress_dialog`**  | 显示全局进度弹窗           | `title?`, `message?`, `mode` (`determinate`/`indeterminate`), `progress?`, `maxProgress?` | 派发弹窗创建副作用，流程非阻塞推进                    |
| **`ui.progress_update`**  | 更新现有进度弹窗显示内容与数值    | `message?`, `progress?`                                                                   | 派发更新副作用，流程非阻塞推进                      |
| **`ui.progress_dismiss`** | 关闭并销毁当前全局进度弹窗      | 无                                                                                         | 派发销毁副作用，流程非阻塞推进                      |

#### 硬件与多媒体节点 (`system.*`, `image.*`, `video.*`)

| 节点类型                      | 功能说明          | 配置参数                                      | 触发机制      |
|:--------------------------|:--------------|:------------------------------------------|:----------|
| **`system.share`**        | 唤起系统原生分享面板    | `text` (必填)                               | 派发系统分享副作用 |
| **`system.notification`** | 发送系统通知栏消息     | `content` (必填), `title?`                  | 派发系统通知副作用 |
| **`system.vibrate`**      | 触发设备触觉震动      | 无                                         | 派发硬件震动副作用 |
| **`image.preview`**       | 唤起应用全屏大图预览画廊  | `images` (Array<String>), `index?` (默认 0) | 派发画廊查看副作用 |
| **`video.preview`**       | 唤起应用内置播放器全屏播放 | `url` (必填), `headers?`                    | 派发视频播放副作用 |

---

## 5. 错误处理与诊断体系

### 5.1 异常模型与继承层次

引擎通过结构化异常体系管理静态拓扑错误与运行期故障：

```text
               ActionWorkflowException (领域异常抽象基类)
                          │
         ┌────────────────┴────────────────┐
         ▼                                 ▼
ActionValidationException         ActionNodeExecutionException
 (拓扑结构与静态配置校验不通过)         (节点运行期抛出未捕获异常)
```

所有抛出异常均携带结构化诊断字段：

- `code`: 机器可读的错误标识码（如 `invalid_workflow`、`node_execution_failed`）。
- `message`: 人类可读的错误描述信息。
- `workflowId` / `workflowName`: 发生异常的工作流标识与名称。
- `nodeId` / `nodeType` / `nodeLabel`: 触发异常的节点标识、类型与展示名称。
- `configKey`: 触发校验失败的具体配置键（可选）。
- `details`: 发生异常时的输入与配置快照（`JsonObject`）。
- `hint`: 排查指导建议字符串。

### 5.2 错误码分类规范

错误码统一收归于 `ActionErrorCode.kt`：

1. **运行时错误 (`ActionErrorCode`)**：包含 `INVALID_WORKFLOW`、`WORKFLOW_DISABLED`、`STEP_LIMIT`、`MISSING_NODE`、`UNKNOWN_NODE`、`LOOP_EXECUTION_FAILED`、`SIDE_EFFECT_CANCELLED`、
   `SIDE_EFFECT_FAILED`、`NODE_EXECUTION_FAILED` 等。
2. **静态校验错误 (`ActionValidationCode`)**：包含 `UNSUPPORTED_FORMAT`、`DUPLICATE_NODE_ID`、`INVALID_ENTRY`、`MISSING_CONFIG`、`CONTROL_CYCLE`、`JOIN_WITHOUT_PARALLEL` 等 26 项拓扑与参数规则。

### 5.3 结构化错误输出协议

当工作流执行失败或进入 `failure` 端口时，引擎通过 `toExecutionError()` 导出标准 JSON 对象供下游消费与持久化存储：

```json
{
   "error": {
      "code": "node_execution_failed",
      "message": "节点 [divide_node] 执行抛出未捕获异常",
      "nodeId": "divide_node",
      "nodeType": "math.divide",
      "nodeLabel": "除法计算",
      "workflowId": "wf_sample",
      "configKey": "right",
      "details": {
         "config": {
            "left": 100,
            "right": 0
         }
      },
      "hint": "除数不能为 0，请在除法前通过 control.if 校验。"
   }
}
```

### 5.4 诊断追踪与日志监听

- **控制台跟踪输出 (`ActionWorkflowTraceLogger`)**：在开发与调试模式下，引擎在控制台输出带节点元数据与建议的错误报告。
- **全局日志监听器 (`ActionWorkflowLogListener`)**：通过 `ActionWorkflowTraceLogger.addListener { tag, priority, message -> ... }` 接入外部日志系统或上报服务。

---

## 6. 接入与扩展开发

### 6.1 依赖注入装配

宿主模块需提供支持 Cookie 存储与配置管理的 HTTP Client，通过专用 Qualifier 注册后加载工作流模块：

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

### 6.2 执行与事件消费

外部业务层通过注入 `ActionWorkflowEngine` 启动执行，并消费 Flow 收集各阶段事件：

```kotlin
val workflowEngine: ActionWorkflowEngine = get()

workflowEngine.execute(
    workflow = workflow,
    initialContext = ActionExecutionContext(
       input = inputPayload,
       environment = environmentData,
       trigger = triggerMetadata,
    ),
   sideEffectHandler = sideEffectHandler,
).collect { event ->
    when (event) {
       is ActionExecutionEvent.Started -> println("工作流启动: ${event.workflowId}")
       is ActionExecutionEvent.NodeStarted -> println("节点启动: ${event.nodeId}")
       is ActionExecutionEvent.NodeCompleted -> println("节点完成: ${event.nodeId}")
       is ActionExecutionEvent.SideEffectRequested -> sideEffectHandler.handle(event.effect)
       is ActionExecutionEvent.Completed -> println("执行完成，状态: ${event.log.status}")
       is ActionExecutionEvent.Failed -> println("执行失败: ${event.error.message}")
    }
}
```

### 6.3 自定义节点开发规范

扩展新节点时遵循以下开发步骤：

1. **声明节点标识**：在 `ActionNodeType` 中添加唯一常量（如 `const val CUSTOM_ACTION = "custom.action"`）。
2. **定义配置键**：在 `ActionNodeKeys.kt` 中声明配置参数名与默认值常量，避免硬编码。
3. **编写规格定义**：使用 `ActionNodeSpec` 声明输入端口、输出端口、必需配置参数及依赖权限。
4. **实现执行逻辑**：继承 `ActionNodeExecutor`，从 `ActionExecutionContext` 读取参数，计算并返回 `ActionNodeExecutionResult`。
5. **注册与迁移**：在注册中心注册节点定义。当节点协议发生破坏性变更时，递增版本号并编写 `ActionNodeMigrator`。
6. **编写单元测试**：在测试目录对应功能测试套件中添加规格与边界覆盖测试。

### 6.4 测试验证

模块提供完备的单元测试集，覆盖全部节点行为、拓扑分支与表达式计算：

```bash
# 运行 data-workflow 模块单测
./gradlew :shared:data-workflow:jvmTest

# 验证 workflows 业务样例模块编译
./gradlew :features:workflows:compileKotlinJvm
```
