package com.xiaoyv.bangumi.ui.media

import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.FragmentMediaBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.common.api.parser.entity.BrowserEntity

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

    private val optionAdapter by lazy {
        MediaOptionAdapter()
    }

    private val tabLayoutMediator by lazy {
        TabLayoutMediator(binding.tabLayout, binding.vp2) { tab, position ->
            tab.text = vpAdapter.bottomTabs[position].title
        }
    }

    override fun initView() {
        binding.vp2.adapter = vpAdapter
        binding.vp2.offscreenPageLimit = vpAdapter.itemCount

        binding.rvOptions.adapter = optionAdapter

        tabLayoutMediator.attach()

        binding.drawLayout.setScrimColor(hostActivity.getColor(com.xiaoyv.widget.R.color.ui_black_20))
    }

    override fun initData() {

    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onOptionsItemLiveData.observe(this) {
            optionAdapter.submitList(it.orEmpty())
        }
    }

    fun refreshOptions(options: List<BrowserEntity.Option>?) {
        viewModel.onOptionsItemLiveData.value = options.orEmpty().flatMap {
            val title = it.title
            val items = it.items
            val list = arrayListOf<Any>()
            list.add(title)
            list.addAll(items)
            list
        }
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