package com.xiaoyv.bangumi.shared.core.bmo

expect object BmoImageCompositor {
    fun composite(
        layers: List<Pair<BmoResolvedItem, ByteArray>>,
        canvasWidth: Int,
        canvasHeight: Int
    ): ByteArray?
}
