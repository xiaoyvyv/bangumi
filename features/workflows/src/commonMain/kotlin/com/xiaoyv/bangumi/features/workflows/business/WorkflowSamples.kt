package com.xiaoyv.bangumi.features.workflows.business

import com.xiaoyv.bangumi.features.workflows.business.samples.BusinessSamples
import com.xiaoyv.bangumi.features.workflows.business.samples.DataSamples
import com.xiaoyv.bangumi.features.workflows.business.samples.ErrorSamples
import com.xiaoyv.bangumi.features.workflows.business.samples.FlowSamples
import com.xiaoyv.bangumi.features.workflows.business.samples.HtmlSamples
import com.xiaoyv.bangumi.features.workflows.business.samples.IoSamples
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionWorkflow
import kotlinx.collections.immutable.toPersistentList

/**
 * 内置节点的可运行回归样例门面。
 *
 * 聚合 [ErrorSamples]、[BusinessSamples]、[FlowSamples]、[DataSamples]、[HtmlSamples] 以及 [IoSamples]，
 * 对外部页面和测试提供统一且稳定的查询接口。
 */
object WorkflowSamples {
    /**
     * 所有当前内置节点的测试工作流列表。
     */
    val all: List<ActionWorkflow> = buildList {
        // 错误与异常故障测试样例
        addAll(ErrorSamples.all)

        // 复合业务实操样例
        addAll(BusinessSamples.all)

        // 流程与控制节点样例
        addAll(FlowSamples.all)

        // 基础数据与运算转换样例
        addAll(DataSamples.all)

        // HTML 文档解析与 DOM 提取样例
        addAll(HtmlSamples.all)

        // 存储、网络与 UI 副作用样例
        addAll(IoSamples.all)
    }.toPersistentList()

    /**
     * 按稳定 ID 查询测试工作流。
     */
    fun find(id: String): ActionWorkflow? = all.firstOrNull { it.id == id }
}
