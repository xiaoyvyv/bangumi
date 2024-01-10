package com.xiaoyv.common.widget.setting

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.xiaoyv.common.R
import com.xiaoyv.common.databinding.ViewSettingItemBinding
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.showOptionsDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import java.io.Serializable
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
    val binding = ViewSettingItemBinding.inflate(context.inflater, this)

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
    fun bindBoolean(
        property: KMutableProperty0<Boolean>,
        bindTitle: String? = null,
    ) {
        if (bindTitle != null) title = bindTitle
        refreshBoolean(property)

        binding.btnSwitch.setOnCheckedChangeListener { _, isChecked ->
            property.set(isChecked)
            refreshBoolean(property)
        }

        setOnFastLimitClickListener {
            binding.btnSwitch.toggle()
        }
    }

    fun <T : Serializable> bindSerializable(
        activity: FragmentActivity,
        property: KMutableProperty0<T>,
        names: List<String>,
        values: List<T>,
        bindTitle: String? = null,
        confirm: (() -> Unit)? = null,
    ) {
        if (bindTitle != null) title = bindTitle
        setOnFastLimitClickListener {
            activity.showOptionsDialog(
                title = title,
                items = names,
                onItemClick = { _, which ->
                    property.set(values[which])
                    refreshSerializable(names, values, property)
                    confirm?.invoke()
                }
            )
        }
        refreshSerializable(names, values, property)
    }

    private fun refreshBoolean(property: KMutableProperty0<Boolean>) {
        binding.btnSwitch.isChecked = property.get()
        binding.btnSwitch.isVisible = true
        binding.gpText.isVisible = false
    }

    private fun <T : Serializable> refreshSerializable(
        keys: List<String>,
        values: List<T>,
        property: KMutableProperty0<T>,
    ) {
        val valueIndex = values.indexOfFirst { it == property.get() }
        val name = keys.getOrNull(valueIndex).orEmpty().ifBlank { property.get().toString() }

        binding.btnSwitch.isVisible = false
        binding.gpText.isVisible = true

        desc = name
    }
}