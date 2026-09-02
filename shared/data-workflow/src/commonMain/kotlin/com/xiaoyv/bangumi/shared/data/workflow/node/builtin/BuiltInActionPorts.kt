package com.xiaoyv.bangumi.shared.data.workflow.node.builtin

import com.xiaoyv.bangumi.shared.data.workflow.model.ActionControlPortId
import com.xiaoyv.bangumi.shared.data.workflow.node.ActionPortDirection
import com.xiaoyv.bangumi.shared.data.workflow.node.ActionPortSpec

internal val inPort = ActionPortSpec(id = ActionControlPortId.IN, direction = ActionPortDirection.INPUT)
internal val nextPort = ActionPortSpec(id = ActionControlPortId.NEXT, direction = ActionPortDirection.OUTPUT)
internal val successPort = ActionPortSpec(id = ActionControlPortId.SUCCESS, direction = ActionPortDirection.OUTPUT)
internal val failurePort = ActionPortSpec(id = ActionControlPortId.FAILURE, direction = ActionPortDirection.OUTPUT)
