package com.xiaoyv.bangumi.special.magnet

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.databinding.ActivityMagnetBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.response.anime.AnimeMagnetEntity
import com.xiaoyv.common.kts.copyText
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.magnetHash
import com.xiaoyv.common.kts.showOptionsDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat

/**
 * Class: [MagnetActivity]
 *
 * @author why
 * @since 1/1/24
 */
class MagnetActivity : BaseListActivity<AnimeMagnetEntity.Resource, MagnetViewModel>() {
    private lateinit var filterBinding: ActivityMagnetBinding

    override val toolbarTitle: String
        get() = "动画资源搜索"

    override val isOnlyOnePage: Boolean
        get() = true

    override val loadingBias: Float
        get() = 0.3f

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.keyword = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun injectFilter(container: FrameLayout) {
        filterBinding = ActivityMagnetBinding.inflate(layoutInflater, container, true)

        filterBinding.etName.setText(viewModel.keyword)
        filterBinding.etName.setOnEditorActionListener { _, _, _ ->
            KeyboardUtils.hideSoftInput(this)
            viewModel.keyword = filterBinding.etName.text.toString().trim()
            viewModel.refresh()
            true
        }

        runCatching { filterBinding.etName.setSelection(filterBinding.etName.length()) }

        filterBinding.rvOptions.onOptionSelectedChange = {
            viewModel.fillParam(filterBinding.rvOptions.selectedOptions)
            viewModel.refresh()
        }

        filterBinding.ivAdvance.setOnFastLimitClickListener {
            MagnetApiDialog.show(supportFragmentManager) {
                viewModel.queryTypes()
                viewModel.refresh()
            }
        }
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.addOnItemChildClickListener(R.id.item_magnet) { adapter, _, position ->
            val resource = adapter.getItem(position) ?: return@addOnItemChildClickListener
            onMagnetItemClick(resource)
        }
    }

    override fun onWindowFirstFocus() {
        if (viewModel.keyword.isBlank()) {
            KeyboardUtils.showSoftInput(filterBinding.etName)
        }
    }

    override fun LifecycleOwner.initViewObserverExt() {
        viewModel.onOptionLiveData.observe(this) {
            debugLog { it.toJson(true) }

            filterBinding.rvOptions.options = it
            filterBinding.rvOptions.isVisible = true
        }
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<AnimeMagnetEntity.Resource, *> {
        return MagnetAdapter { viewModel.keyword }
    }

    companion object {

        /**
         * 弹窗
         */
        @JvmStatic
        fun FragmentActivity.onMagnetItemClick(resource: AnimeMagnetEntity.Resource) {
            showOptionsDialog(
                title = "资源详情",
                items = listOf(
                    "复制磁链",
                    "复制完整磁链",
                    "复制发布来源",
                    "打开方式",
                    "磁链资源预览"
                ),
                onItemClick = { _, which ->
                    when (which) {
                        0 -> copyText(resource.magnet.orEmpty())
                        1 -> copyText(resource.magnet.orEmpty())
                        2 -> copyText(resource.pageUrl.orEmpty())
                        3 -> {
                            runCatching {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setData(Uri.parse(resource.magnet.orEmpty()))
                                ActivityUtils.startActivity(
                                    Intent.createChooser(
                                        intent,
                                        "打开方式"
                                    )
                                )
                            }.onFailure {
                                showToastCompat(it.errorMsg)
                            }
                        }
                        // 磁力预览
                        4 -> {
                            val magnetHash = resource.magnet.orEmpty().magnetHash()
                            RouteHelper.jumpWeb(
                                url = "https://beta.magnet.pics/m/$magnetHash",
                                fitToolbar = true,
                                smallToolbar = true
                            )
                        }
                    }
                }
            )
        }
    }
}