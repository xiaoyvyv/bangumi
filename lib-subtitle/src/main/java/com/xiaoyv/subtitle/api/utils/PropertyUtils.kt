package com.xiaoyv.subtitle.api.utils

import android.util.Log
import java.lang.Exception

object PropertyUtils {
    @JvmStatic
    @Throws(Exception::class)
    fun setProperty(obj: Any, property: String, value: Any) {
        val field = obj.javaClass.getDeclaredField(property).apply {
            isAccessible = true
        }
        field.set(obj, value)
    }

    @JvmStatic
    fun getPropertyDescriptor(obj: Any, property: String): PropertyDescriptor? {
        runCatching {
            val field = obj.javaClass.getDeclaredField(property).apply {
                isAccessible = true
            }
            return PropertyDescriptor(field.type)
        }
        return null
    }

    data class PropertyDescriptor(var type: Class<*>)
}