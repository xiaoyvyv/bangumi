@file:Suppress("INVISIBLE_REFERENCE")

package com.xiaoyv.bangumi.shared.resource

import com.xiaoyv.bangumi.core_resource.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.getResourceItemByEnvironment
import org.jetbrains.compose.resources.getSystemResourceEnvironment

private const val MD: String = "composeResources/com.xiaoyv.bangumi.core_resource.resources/"

@OptIn(InternalResourceApi::class)
private fun DrawableResource.relativePath(): String {
    val environment = getSystemResourceEnvironment()
    return this.getResourceItemByEnvironment(environment).path.substringAfter(MD)
}

fun DrawableResource.toComposeUri() = Res.getUri(relativePath())
