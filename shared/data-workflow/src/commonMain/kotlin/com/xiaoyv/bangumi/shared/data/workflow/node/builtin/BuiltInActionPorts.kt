package com.xiaoyv.bangumi.shared.data.workflow.node.builtin

import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionPortConnectionLimit
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionPortDirection
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionPortSpec
import kotlinx.collections.immutable.persistentListOf

internal val inPort = ActionPortSpec(id = ActionControlPortId.IN, direction = ActionPortDirection.INPUT)
internal val nextPort = ActionPortSpec(id = ActionControlPortId.NEXT, direction = ActionPortDirection.OUTPUT)
internal val successPort = ActionPortSpec(id = ActionControlPortId.SUCCESS, direction = ActionPortDirection.OUTPUT)
internal val failurePort = ActionPortSpec(id = ActionControlPortId.FAILURE, direction = ActionPortDirection.OUTPUT)
internal val bodyPort = ActionPortSpec(id = ActionControlPortId.BODY, direction = ActionPortDirection.OUTPUT)
internal val completedPort = ActionPortSpec(id = ActionControlPortId.COMPLETED, direction = ActionPortDirection.OUTPUT)
internal val loopControlInPort = ActionPortSpec(id = ActionControlPortId.IN, direction = ActionPortDirection.INPUT, maxConnections = ActionPortConnectionLimit.UNLIMITED)
internal val matchedPort = ActionPortSpec(id = ActionControlPortId.MATCHED, direction = ActionPortDirection.OUTPUT)
internal val defaultPort = ActionPortSpec(id = ActionControlPortId.DEFAULT, direction = ActionPortDirection.OUTPUT)
internal val tryPort = ActionPortSpec(id = ActionControlPortId.TRY, direction = ActionPortDirection.OUTPUT)
internal val catchPort = ActionPortSpec(id = ActionControlPortId.CATCH, direction = ActionPortDirection.OUTPUT)
internal val finallyPort = ActionPortSpec(id = ActionControlPortId.FINALLY, direction = ActionPortDirection.OUTPUT)
internal val branchesPort = ActionPortSpec(id = ActionControlPortId.BRANCHES, direction = ActionPortDirection.OUTPUT)
internal val truePort = ActionPortSpec(id = ActionControlPortId.TRUE, direction = ActionPortDirection.OUTPUT)
internal val falsePort = ActionPortSpec(id = ActionControlPortId.FALSE, direction = ActionPortDirection.OUTPUT)
internal val conditionPorts = persistentListOf(truePort, falsePort)
