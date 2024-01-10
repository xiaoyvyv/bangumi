@file:Suppress("UNCHECKED_CAST")

package com.xiaoyv.common.kts

import java.io.Serializable

internal object UninitializedValue

class SynchronizedLazyImpl<out T>(initializer: () -> T, lock: Any? = null) : Lazy<T>, Serializable {

    private var initializer: (() -> T)? = initializer

    @Volatile
    private var _value: Any? = UninitializedValue

    // final field is required to enable safe publication of constructed instance
    private val lock = lock ?: this

    override val value: T
        get() {
            val any1 = _value
            if (any1 !== UninitializedValue) {
                return any1 as T
            }

            return synchronized(lock) {
                val any = _value
                if (any !== UninitializedValue) {
                    (any as T)
                } else {
                    val typedValue = initializer!!()
                    _value = typedValue
                    initializer = null
                    typedValue
                }
            }
        }

    override fun isInitialized(): Boolean = _value !== UninitializedValue

    override fun toString(): String =
        if (isInitialized()) value.toString() else "Lazy value not initialized yet."

    private fun writeReplace(): Any = InitializedLazyImpl(value)
}

class InitializedLazyImpl<out T>(override val value: T) : Lazy<T>, Serializable {
    override fun isInitialized(): Boolean = true
    override fun toString(): String = value.toString()
}