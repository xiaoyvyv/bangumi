package com.xiaoyv.common.widget.emoji.grid

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.blueprint.base.binding.BaseBindingFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.databinding.ViewEmotionBinding
import com.xiaoyv.common.databinding.ViewEmotionPageBinding
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.emoji.BgmEmoji
import com.xiaoyv.widget.kts.adjustScrollSensitivity
import com.xiaoyv.widget.kts.getSerialObj

/**
 * Class: [UiFacePanel]
 *
 * @author why
 * @since 12/28/23
 */
class UiFacePanel @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {
    private val binding = ViewEmotionBinding.inflate(context.inflater, this)
    private var tabLayoutMediator: TabLayoutMediator? = null
    private var pageAdapter: EmojiPageAdapter? = null
    private var block: BaseQuickAdapter.OnItemChildClickListener<UiFaceGridView.EmojiItem>? = null

    /**
     * 填充表情
     */
    fun fillEmojis(
        activity: FragmentActivity,
        block: BaseQuickAdapter.OnItemChildClickListener<UiFaceGridView.EmojiItem>? = null,
    ) {
        if (pageAdapter != null) return

        this.block = block
        pageAdapter = EmojiPageAdapter(activity.supportFragmentManager, activity.lifecycle)

        binding.vpEmoji.adjustScrollSensitivity(ConfigHelper.vpTouchSlop.toFloat())
        binding.vpEmoji.offscreenPageLimit = 3
        binding.vpEmoji.adapter = pageAdapter

        tabLayoutMediator?.detach()
        tabLayoutMediator = TabLayoutMediator(binding.tabLayout, binding.vpEmoji) { tab, position ->
            tab.text = when (position) {
                0 -> "TV 表情"
                1 -> "Face 表情"
                2 -> "Bgm 表情"
                else -> "表情"
            }
        }
        tabLayoutMediator?.attach()
    }

    private fun newInstance(emojis: HashMap<String, Int>): UiFacePage {
        return UiFacePage().apply {
            arguments = bundleOf(NavKey.KEY_SERIALIZABLE to emojis)
        }
    }

    private inner class EmojiPageAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> newInstance(BgmEmoji.tvEmoji).also { it.listener = block }
                1 -> newInstance(BgmEmoji.normalEmoji).also { it.listener = block }
                2 -> newInstance(BgmEmoji.bgmEmoji).also { it.listener = block }
                else -> throw IllegalArgumentException()
            }
        }

        override fun getItemCount(): Int {
            return 3
        }
    }

    class UiFacePage : BaseBindingFragment<ViewEmotionPageBinding>() {
        private var emojis: HashMap<String, Int>? = null
        var listener: BaseQuickAdapter.OnItemChildClickListener<UiFaceGridView.EmojiItem>? = null

        override fun initArgumentsData(arguments: Bundle) {
            emojis = arguments.getSerialObj<HashMap<String, Int>>(NavKey.KEY_SERIALIZABLE)
        }

        override fun initView() {
        }

        override fun initData() {
            binding.pageGrid.fillEmojis(emojis.orEmpty(), listener)
        }
    }
}