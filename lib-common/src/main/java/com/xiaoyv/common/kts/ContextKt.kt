package com.xiaoyv.common.kts

import android.content.Context
import android.view.LayoutInflater

/**
 * @author why
 * @since 11/25/23
 */
val Context.inflater: LayoutInflater get() = LayoutInflater.from(this)