package com.xiaoyv.common.widget.setting

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaoyv.common.R
import com.xiaoyv.common.databinding.ViewOptionItemBinding
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.widget.binder.BaseQuickBindingAdapter
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.getDpx

/**
 * SearchOptionView
 *
 * @author why
 * @since 11/19/23
 */
class SearchOptionView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : RecyclerView(context, attrs) {
    private val optionAdapter by lazy {
        OptionAdapter(this)
    }

    var titleColor: Int? = null
    var itemWidth: Float? = null
    var onOptionSelectedChange: () -> Unit = {}

    var options: List<Option> = emptyList()
        set(value) {
            field = value
            optionAdapter.submitList(value)
        }

    /**
     * 获取选取的参数项目
     */
    val selectedOptions: List<Option>
        get() = optionAdapter.items.filter { it.selected != null }

    init {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = optionAdapter
        itemAnimator = null

        optionAdapter.addOnItemChildClickListener(R.id.tv_option_value) { adapter, _, position ->
            val option = adapter.getItem(position) ?: return@addOnItemChildClickListener

            val optionItems = option.options.orEmpty().toMutableList()
            optionItems.add(0, OptionItem("Any", ""))
            val items = optionItems.map { it.name }.toTypedArray()

            MaterialAlertDialogBuilder(context)
                .setItems(items) { _, which ->
                    // 第一个选项清空
                    if (which == 0) {
                        option.selected = null
                    } else {
                        option.selected = optionItems[which]
                    }

                    optionAdapter.notifyItemChanged(position)
                    onOptionSelectedChange()
                }
                .create()
                .show()
        }
    }

    fun bindView(target: ImageView) {
        target.setOnClickListener {
            target.isSelected = target.isSelected.not()
            refreshVisible(target)
        }
        refreshVisible(target)
    }

    private fun refreshVisible(target: ImageView) {
        clearAnimation()
        alpha = 0f
        isVisible = target.isSelected
        if (isVisible) {
            target.imageTintList =
                ColorStateList.valueOf(context.getAttrColor(GoogleAttr.colorPrimary))

            animate()
                .alpha(1f)
                .setDuration(300)
                .start()
        } else {
            target.imageTintList =
                ColorStateList.valueOf(context.getAttrColor(GoogleAttr.colorSecondary))
        }
    }

    class OptionAdapter(private val searchOptionView: SearchOptionView) :
        BaseQuickBindingAdapter<Option, ViewOptionItemBinding>() {
        override fun BaseQuickBindingHolder<ViewOptionItemBinding>.converted(item: Option) {
            val selectName = item.selected?.name.orEmpty()
            val titleColor = searchOptionView.titleColor

            binding.tvOptionTitle.text = item.name
            binding.tvOptionValue.text = selectName.ifBlank { "Any" }

            if (selectName.isBlank()) {
                binding.tvOptionValue.setTextColor(context.getAttrColor(GoogleAttr.colorSecondary))
            } else {
                binding.tvOptionValue.setTextColor(context.getAttrColor(GoogleAttr.colorPrimary))
            }

            if (titleColor != null) {
                binding.tvOptionTitle.setTextColor(titleColor)
            }

            refreshWidth(binding)
        }

        private fun refreshWidth(binding: ViewOptionItemBinding) {
            val itemWidth = searchOptionView.itemWidth

            // 默认需要平分屏幕宽度
            if (itemWidth == null) {
                binding.tvOptionValue.updateLayoutParams {
                    val screenWidth = ScreenUtils.getScreenWidth()
                    val offset = binding.tvOptionValue.getDpx(32f + 12f)
                    width = (screenWidth - offset) / 2
                }
            }
            // 自定义宽度
            else {
                binding.tvOptionValue.updateLayoutParams {
                    width = itemWidth.toInt()
                }
            }
        }
    }

    data class Option(
        var name: String,
        var fieldName: String,
        var options: List<OptionItem>? = null,
        var selected: OptionItem? = null,
    )

    data class OptionItem(
        var name: String,
        var value: String,
    )
}