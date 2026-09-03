@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.data.workflow.di

import com.xiaoyv.bangumi.shared.data.workflow.codec.ActionWorkflowCodec
import com.xiaoyv.bangumi.shared.data.workflow.engine.ActionWorkflowValidator
import com.xiaoyv.bangumi.shared.data.workflow.engine.runtime.ActionWorkflowEngine
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeRegistry
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowFileStorage
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowLogger
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionWorkflowPreferencesStore
import com.xiaoyv.bangumi.shared.data.workflow.port.DefaultActionWorkflowFileStorage
import com.xiaoyv.bangumi.shared.data.workflow.port.DefaultActionWorkflowLogger
import com.xiaoyv.bangumi.shared.data.workflow.port.DefaultActionWorkflowPreferencesStore
import com.xiaoyv.bangumi.shared.libnative.System
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * 工作流领域服务的 Koin 模块。
 *
 * 工作流所需的网络端口由 data 模块提供，以便复用应用网络配置和本地 Cookie 存储。
 */
private val workflowModule = module {
    singleOf(::ActionWorkflowValidator)
    singleOf(::ActionWorkflowCodec)
    single<ActionWorkflowLogger> { DefaultActionWorkflowLogger() }
    single { ActionNodeRegistry(get(), get(), get(), get()) }
    single { ActionWorkflowEngine(get(), get(), System::currentTimeMillis) }
    single<ActionWorkflowPreferencesStore> { DefaultActionWorkflowPreferencesStore() }
    single<ActionWorkflowFileStorage> { DefaultActionWorkflowFileStorage() }
}

/**
 * 应用启动时应加载的工作流领域模块集合。
 */
val workflowModules = arrayOf(workflowModule)
