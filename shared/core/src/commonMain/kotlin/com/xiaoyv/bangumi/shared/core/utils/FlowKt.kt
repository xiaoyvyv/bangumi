package com.xiaoyv.bangumi.shared.core.utils

import kotlinx.coroutines.flow.MutableStateFlow


inline fun <reified T> mutableStateFlowOf(v: T) = MutableStateFlow(v)