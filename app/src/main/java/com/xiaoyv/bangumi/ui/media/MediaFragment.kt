package com.xiaoyv.bangumi.ui.media

import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.FragmentMediaBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.media.option.MediaOptionFragment
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.config.bean.MediaOptionConfig
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.CommonString
import kotlinx.coroutines.delay

/**
 * Class: [MediaFragment]
 *
 * @author why
 * @since 11/24/23
 */
class MediaFragment : BaseViewModelFragment<FragmentMediaBinding, MediaViewModel>() {

    private val vpAdapter by lazy {
        MediaAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
    }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tabLayout, binding.vp2) { tab, position ->
            tab.text = vpAdapter.bottomTabs[position].title
        }
    }

    /**
     * 获取 VP 当前页面的媒体类型
     */
    private val currentMediaType: String
        get() = vpAdapter.getCurrentMediaType(binding.vp2.currentItem)

    /**
     * 获取 VP 当前页面的媒体类型名称
     */
    private val currentMediaTypeName: String
        get() = vpAdapter.getCurrentMediaTypeName(binding.vp2.currentItem)

    private val optionAdapter by lazy {
        object : FragmentStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle) {
            override fun getItemCount(): Int {
                return vpAdapter.bottomTabs.size
            }

            override fun createFragment(position: Int): Fragment {
                return MediaOptionFragment.newInstance(vpAdapter.bottomTabs[position].type)
            }
        }
    }

    override fun initView() {
        binding.drawLayout.setScrimColor(hostActivity.getColor(com.xiaoyv.widget.R.color.ui_black_20))
    }

    override fun initData() {
        binding.vp2.adapter = vpAdapter
        binding.vp2.offscreenPageLimit = vpAdapter.itemCount

        binding.flOptions.isUserInputEnabled = false
        binding.flOptions.adapter = optionAdapter
        binding.flOptions.offscreenPageLimit = optionAdapter.itemCount

        tabLayoutMediator.attach()
    }

    override fun initListener() {
        binding.toolbar.menu.apply {
            add(getString(CommonString.common_filter))
                .setIcon(CommonDrawable.ic_filter)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener {
                    binding.drawLayout.openDrawer(GravityCompat.START)
                    true
                }

            add(getString(CommonString.common_search))
                .setIcon(CommonDrawable.ic_search)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener {
                    RouteHelper.jumpSearch()
                    true
                }
        }

        binding.vp2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.flOptions.setCurrentItem(position, false)

                // 刷新标题
                launchUI {
                    delay(100)
                    viewModel.currentSelectedOptionItem.value =
                        viewModel.currentSelectedOptionItem.value
                }
            }
        })
    }

    override fun LifecycleOwner.initViewObserver() {
        // 监听条件改变标题
        regisOptionSelectedChange(this) {
            val items = it?.get(currentMediaType).orEmpty()
                .sortedBy { item -> item.pathIndex }
                .toMutableList()

            val pinYin = items.find { item -> item.isSortPinYin }
            items.remove(pinYin)

            val sortOption = items.find { item -> item.isSortOption }
            items.remove(sortOption)

            binding.toolbar.title = if (items.isEmpty()) currentMediaTypeName else {
                "$currentMediaTypeName/" + items
                    .map { item -> item.title }
                    .joinToString("/")
            }

            binding.drawLayout.closeDrawer(GravityCompat.START)
        }
    }

    /**
     * 注册条件点击监听
     */
    fun regisOptionSelectedChange(
        lifecycleOwner: LifecycleOwner,
        block: Observer<Map<String, List<MediaOptionConfig.Config.Option.Item>>?>
    ) {
        viewModel.currentSelectedOptionItem.observe(lifecycleOwner, block)
    }

    /**
     * 选项选中
     */
    fun onOptionSelected(pair: Pair<String, ArrayList<MediaOptionConfig.Config.Option.Item>>) {
        val mediaType = pair.first
        val items = pair.second
        val listMap = requireNotNull(viewModel.currentSelectedOptionItem.value)
        listMap[mediaType] = items
        viewModel.currentSelectedOptionItem.value = listMap
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator.detach()
    }

    companion object {
        fun newInstance(): MediaFragment {
            return MediaFragment()
        }
    }
}