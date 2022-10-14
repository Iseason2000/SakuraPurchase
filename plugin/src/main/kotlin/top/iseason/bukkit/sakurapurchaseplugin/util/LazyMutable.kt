package top.iseason.bukkit.sakurapurchaseplugin.util

import kotlin.reflect.KProperty

interface LazyMutable<T> {
    var value: T
    fun isInitialized(): Boolean
}

public inline operator fun <T> LazyMutable<T>.getValue(thisRef: Any?, property: KProperty<*>): T {
    return value
}

public inline operator fun <T> LazyMutable<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this.value = value
}

public fun <T> lazyMutable(initializer: () -> T): LazyMutable<T> = SynchronizedLazyMutableImpl(initializer)
public fun <T> lazyMutable(initializer: () -> T, onChange: (newValue: T) -> Unit): LazyMutable<T> =
    SynchronizedLazyMutableImpl(initializer, onChange)

private object UNINITIALIZED_VALUE

private class SynchronizedLazyMutableImpl<T>(
    initializer: () -> T,
    private val onChange: ((newValue: T) -> Unit)? = null,
    lock: Any? = null
) : LazyMutable<T> {
    private var initializer: (() -> T)? = initializer
    @Volatile
    private var _value: Any? = UNINITIALIZED_VALUE

    // final field is required to enable safe publication of constructed instance
    private val lock = lock ?: this

    override var value: T
        get() {
            val _v1 = _value
            if (_v1 !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST")
                return _v1 as T
            }

            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== UNINITIALIZED_VALUE) {
                    @Suppress("UNCHECKED_CAST") (_v2 as T)
                } else {
                    val typedValue = initializer!!()
                    _value = typedValue
                    initializer = null
                    typedValue
                }
            }
        }
        set(value) {
            synchronized(lock) {
                _value = value
            }
            onChange?.invoke(value)
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy value not initialized yet."
}
