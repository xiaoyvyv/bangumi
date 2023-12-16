package com.xiaoyv.common.widget.setting

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.xiaoyv.common.databinding.ViewSettingItemBinding
import com.xiaoyv.common.kts.inflater

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
}