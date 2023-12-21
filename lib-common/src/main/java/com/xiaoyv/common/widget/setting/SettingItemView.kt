package com.xiaoyv.common.widget.setting

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import com.xiaoyv.common.R
import com.xiaoyv.common.databinding.ViewSettingItemBinding
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import kotlin.reflect.KMutableProperty0

/**
 * Class: [SettingItemView]
 *
 * @author why
 * @since 12/17/23
 */
class SettingItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {
    private val binding = ViewSettingItemBinding.inflate(context.inflater, this)

    var title: String = ""
        set(value) {
            field = value
            binding.tvTitle.text = value
        }

    var desc: String = ""
        set(value) {
            field = value
            binding.tvDesc.text = value
        }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView)
        title = typedArray.getString(R.styleable.SettingItemView_android_text).orEmpty()
        desc = typedArray.getString(R.styleable.SettingItemView_subtitle).orEmpty()
        typedArray.recycle()
    }

    /**
     * 绑定 Boolean 类型开关
     */
    inline fun bindBoolean(
        activity: FragmentActivity,
        property: KMutableProperty0<Boolean>,
        bindTitle: String? = null,
        crossinline dialogTip: (Boolean) -> String? = { null },
        crossinline onChange: (Boolean) -> Unit = {},
    ) {
        if (bindTitle != null) title = bindTitle
        setOnFastLimitClickListener {
            val tip = dialogTip(property.get())
            if (tip == null) {
                val value = property.get().not()
                property.set(value)
                refresh(property)
                onChange(value)
            } else {
                activity.showConfirmDialog(
                    message = tip,
                    onConfirmClick = {
                        val value = property.get().not()
                        property.set(value)
                        refresh(property)
                        onChange(value)
                    }
                )
            }
        }

        refresh(property)
    }

    fun refresh(property: KMutableProperty0<Boolean>) {
        desc = if (property.get()) "开启" else "关闭"
    }
}