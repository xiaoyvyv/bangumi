package com.xiaoyv.bangumi.ui.feature.post

import android.graphics.Typeface
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.SpanUtils
import com.google.android.material.chip.Chip
import com.xiaoyv.bangumi.databinding.ActivityPostTopicBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.activity
import com.xiaoyv.common.api.parser.entity.CreatePostEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.SearchCatType
import com.xiaoyv.common.config.bean.PostAttach
import com.xiaoyv.common.config.bean.SearchItem
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonColor
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.kts.showInputDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.getParcelObj
import com.xiaoyv.widget.kts.spi
import com.xiaoyv.widget.kts.toast


/**
 * Class: [BasePostActivity]
 *
 * @author why
 * @since 12/8/23
 */
abstract class BasePostActivity<VM : BasePostViewModel> :
    BaseViewModelActivity<ActivityPostTopicBinding, VM>() {

    private val imagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        it?.let { uri -> viewModel.compressAndUpload(uri) }
    }

    /**
     * 媒体条目搜索结果
     */
    private val searchMedia =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val attach = it.data?.extras?.getParcelObj<PostAttach>(NavKey.KEY_PARCELABLE)
            if (attach != null) {
                onAddSelectMedia(attach)
            }
        }

    abstract val toolbarTitle: String

    open val maxAttachSize = 5

    @CallSuper
    override fun initView() {
        binding.toolbar.title = toolbarTitle
        binding.toolbar.initNavBack(this)
    }

    @CallSuper
    override fun initData() {
        binding.ivAvatar.loadImageAnimate(UserHelper.currentUser.avatar)
    }

    internal fun showTip() {
        SpanUtils.with(null)
            .append("讨论版欢迎")
            .setForegroundColor(getColor(CommonColor.save_collect))
            .setTypeface(Typeface.DEFAULT_BOLD)
            .setFontSize(16.spi)
            .appendLine()
            .appendLine()
            .append("1. 条目相关的感想、个人体验分享。")
            .appendLine()
            .append("2. 制作细节的考究、分析或研究。")
            .appendLine()
            .append("3. 条目相关新闻、动态、制作花絮的讨论。")
            .appendLine()
            .append("4. 其他对用户理解作品有帮助的内容。")
            .appendLine()
            .appendLine()
            .append("讨论版禁止")
            .setForegroundColor(getColor(CommonColor.save_dropped))
            .setTypeface(Typeface.DEFAULT_BOLD)
            .setFontSize(16.spi)
            .appendLine()
            .appendLine()
            .append("与条目本身内容无关，针对评分排名或针对观众的评价，例如：")
            .appendLine()
            .appendLine()
            .append("「XXX」 真的适合在 X 分以上吗？")
            .appendLine()
            .append("「XXX」 排第 X 真的合适吗？")
            .appendLine()
            .appendLine()
            .append("排名与评分相关的内容请前往 评分与排名讨论会 小组讨论。")
            .create()
            .also {
                showConfirmDialog(title = "温馨提示", message = it, cancelText = null)
            }
    }

    @CallSuper
    override fun initListener() {
        binding.etContent.doAfterTextChanged {
            binding.ivAvatar.isVisible = it.toString().isBlank()
        }

        binding.tvPublic.setOnClickListener {
            viewModel.publicSend.value = (viewModel.publicSend.value ?: false).not()
        }

        binding.tvPreview.setOnFastLimitClickListener {
            RouteHelper.jumpPreviewBBCode(binding.etContent.text.toString().trim())
        }

        binding.ivImage.setOnFastLimitClickListener {
            KeyboardUtils.hideSoftInput(this)

            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.tvMedia.setOnFastLimitClickListener {
            if (viewModel.onAttachMediaList.value.orEmpty().size >= maxAttachSize) {
                toast("最多关联 $maxAttachSize 个条目")
                return@setOnFastLimitClickListener
            }

            KeyboardUtils.hideSoftInput(this)

            showInputDialog(title = "搜索关联条目", inputHint = "请输入条目名称") {
                RouteHelper.jumpSearchDetailForSelectMedia(
                    searchMedia, SearchItem(
                        label = "全部",
                        pathType = BgmPathType.TYPE_SEARCH_SUBJECT,
                        id = SearchCatType.TYPE_ANIME,
                        keyword = it,
                        forSelectedMedia = true
                    )
                )
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.clBottom) { _, i ->
            val ime = i.getInsets(WindowInsetsCompat.Type.ime())
            binding.clBottom.updatePadding(bottom = (ime.bottom - ime.top))
            i
        }

        ViewCompat.requestApplyInsets(binding.clBottom)
    }

    @CallSuper
    override fun LifecycleOwner.initViewObserver() {
        viewModel.publicSend.observe(this) {
            binding.tvPublic.text = if (it) "公开" else "仅好友可见"
            binding.tvPublic.setCompoundDrawablesRelativeWithIntrinsicBounds(
                ContextCompat.getDrawable(
                    activity,
                    if (it) CommonDrawable.ic_public else CommonDrawable.ic_public_off
                ),
                null,
                null,
                null
            )
        }

        // 关联条目
        viewModel.onAttachMediaList.observe(this) {
            binding.mediaGroup.removeAllViews()
            it.forEach { entity ->
                binding.mediaGroup.addView(Chip(activity).apply {
                    id = ViewCompat.generateViewId()
                    isCheckable = true
                    isChecked = true
                    isCheckedIconVisible = false
                    isCloseIconVisible = true
                    isEnabled = true
                    text = entity.title
                    setOnCloseIconClickListener {
                        viewModel.removeAttach(entity)
                    }
                })
            }
        }

        // 上传图片成功
        viewModel.onUploadImageResult.observe(this) {
            KeyboardUtils.showSoftInput(binding.etContent)

//            BBCodeHelper.insert(
//                requireActivity(),
//                binding.etContent,
//                Triple("[img]", "$it[/img]", it.orEmpty().length)
//            )
        }

        // 填充编辑数据
        viewModel.onFillEditInfo.observe(this) {
            binding.etTitle.setText(it.title.trim())
            binding.etContent.setText(it.content.trim())
        }

        initViewObserverExt()
    }

    open fun LifecycleOwner.initViewObserverExt() {}

    @CallSuper
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(if (viewModel.isEditMode) "保存" else "发送")
            .setIcon(if (viewModel.isEditMode) CommonDrawable.ic_save else CommonDrawable.ic_send)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                KeyboardUtils.hideSoftInput(activity)

                // 编辑或发布
                if (viewModel.isEditMode) {
                    viewModel.editPost(onCreatePost())
                } else {
                    viewModel.sendPost(onCreatePost())
                }
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * 构建发布参数
     */
    open fun onCreatePost(): CreatePostEntity {
        return CreatePostEntity(
            title = binding.etTitle.text.toString().trim(),
            content = binding.etContent.text.toString().trim(),
            tags = "",
            isPublic = viewModel.publicSend.value ?: false
        )
    }

    /**
     * 添加搜索的条目
     */
    open fun onAddSelectMedia(attach: PostAttach) {
        viewModel.addAttach(attach)
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}