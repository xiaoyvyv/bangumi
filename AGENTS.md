### 项目介绍

这是一款 KMP 平台的 Bangumi.Tv 的第三方开源项目，使用了 MVI 的架构；并且使用了模块化设计，每一个页面对应一个单独的模块，页面之间互不影响。

### 编码规则

* 边距尽量用 `ContentMarginHalf` 和 `ContentMargin` 如果觉得局限再增加；
* 样式尽量用 `MaterialTheme` 包括颜色形状大小文本等等，并且直接引用，减少局部变量；
* 如果要编写可复用的公共组件放 `shared` 模块的 `…/ui/view` 下；
* 如果需要编写页面参考项目的其它页面的代码，保持结构一致性，使用MVI架构，VM以及Repo 也需要保持一致；
* 所有状态类和可序列化的类标记不变性注解，如果是需要在UI中使用的数据模型类，统一以 `Compose` 为前缀；
* 文本需要抽取到 `shared/core-resource/src/androidMain/res/values/strings.xml`；
* Api 使用的 Ktorfit，定义在 `shared/data/src/commonMain/kotlin/com/xiaoyv/bangumi/shared/data/api` 内，请求和响应在
  `shared/data/src/commonMain/kotlin/com/xiaoyv/bangumi/shared/data/model`；
* Api 的请求类型有枚举的，需要在 `shared/core/src/commonMain/kotlin/com/xiaoyv/bangumi/shared/core/types` 下定义，如果和项目主业务数据，则尽量创建子文件夹归纳，尽量用 google
  的注解定义类型的风格，少用枚举，然后需要在对应的模型或参数标记该注解，各个方法默认值都需要引用这个，不能写魔法值；
* 代码注释使用 `KDoc`，不要用一行 `/** comment */` 的单行注释，指定参数说明 `@param`，未特殊说明默认中文注释；
* UI 设计尽量能复用，且按结构拆分多个 Composable 块；
