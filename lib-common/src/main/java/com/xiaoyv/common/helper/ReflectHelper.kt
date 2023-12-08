@file:Suppress("UNCHECKED_CAST")

package com.xiaoyv.common.helper

import java.lang.reflect.Method

object ReflectHelper {

    @JvmStatic
    fun <T> invokeStaticMethod(
        kClass: Class<*>,
        name: String
    ): T? = invokeStaticMethod<T>(kClass, name, arrayOf(), arrayOf())

    @JvmStatic
    fun <T> invokeStaticMethod(
        kClass: Class<*>,
        name: String,
        paramTypes: Array<Class<*>>,
        paramValue: Array<Any>
    ): T? {
        if (paramTypes.size != paramValue.size) return null
        return getMethod(kClass, name, paramTypes)?.run {
            isAccessible = true
            invoke(kClass, *paramValue) as? T
        }
    }


    /**
     * 调用普通方法
     */
    @JvmStatic
    fun <T> invokeMethod(
        obj: Any,
        name: String,
        paramTypes: Array<Class<*>>,
        paramValue: Array<Any>
    ): T? {
        if (paramTypes.size != paramValue.size) return null
        return getMethod(obj.javaClass, name, paramTypes)?.run {
            isAccessible = true
            invoke(obj, *paramValue) as? T
        }
    }


    @JvmStatic
    private fun getMethod(
        kClass: Class<*>,
        name: String,
        paramTypes: Array<Class<*>>
    ): Method? {
        return try {
            kClass.getDeclaredMethod(name, *paramTypes)
        } catch (e: NoSuchMethodException) {
            // 没有父类，说明这个类没有定义这个方法
            val parent = kClass.superclass ?: return null
            // 如果还有父类，就从父类去找这个方法
            getMethod(parent, name, paramTypes)
        }
    }
}
