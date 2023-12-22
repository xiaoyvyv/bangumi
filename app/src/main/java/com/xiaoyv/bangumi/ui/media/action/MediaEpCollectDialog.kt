package com.xiaoyv.bangumi.ui.media.action

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SnackbarUtils
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentMediaActionEpBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.api.parser.impl.parserMediaChapters
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.EpCollectType
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.hideSnackBar
import com.xiaoyv.common.kts.showSnackBar
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.getParcelObj
import com.xiaoyv.widget.kts.updateWindowParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [MediaEpCollectDialog]
 *
 * @author why
 * @since 12/18/23
 */
class MediaEpCollectDialog : DialogFragment() {
    var onUpdateResult: (List<MediaChapterEntity>) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentMediaActionEpBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val entity = arguments?.getParcelObj<MediaChapterEntity>(NavKey.KEY_PARCELABLE) ?: return
        val mediaType = arguments?.getString(NavKey.KEY_STRING).orEmpty()
        initView(FragmentMediaActionEpBinding.bind(view), entity, mediaType)
    }

    private fun initView(
        binding: FragmentMediaActionEpBinding,
        entity: MediaChapterEntity,
        mediaType: String,
    ) {
        binding.tvTitle.text = entity.titleNative.ifBlank { entity.titleCn }
        binding.ivCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
        binding.btnWish.text = InterestType.string(InterestType.TYPE_WISH, mediaType)
        binding.btnDropped.text = InterestType.string(InterestType.TYPE_DROPPED, mediaType)
        binding.btnCollect.text = InterestType.string(InterestType.TYPE_COLLECT, mediaType)

        // 设置状态
        when (entity.collectType) {
            InterestType.TYPE_WISH -> binding.gpButtons.check(R.id.btn_wish)
            InterestType.TYPE_COLLECT -> binding.gpButtons.check(R.id.btn_collect)
            InterestType.TYPE_DROPPED -> binding.gpButtons.check(R.id.btn_dropped)
        }

        binding.tvTitleCn.isVisible = entity.titleCn.isNotBlank()
        binding.tvTitleCn.text = buildString {
            append("中文名：")
            append(entity.titleCn)
        }
        binding.tvDesc.text = entity.time
        binding.tvComment.text = String.format("讨论：%d，点击查看", entity.commentCount)

        binding.tvComment.setOnFastLimitClickListener {
            jumpDetail(entity)
        }

        binding.tvDesc.setOnFastLimitClickListener {
            jumpDetail(entity)
        }

        binding.tvTitleCn.setOnFastLimitClickListener {
            jumpDetail(entity)
        }

        binding.gpButtons.addOnButtonCheckedListener { _, i, checked ->
            if (!checked) {
                return@addOnButtonCheckedListener
            }

            val epCollectType = when (i) {
                R.id.btn_wish -> {
                    entity.collectType = InterestType.TYPE_WISH
                    EpCollectType.TYPE_QUEUE
                }

                R.id.btn_dropped -> {
                    entity.collectType = InterestType.TYPE_DROPPED
                    EpCollectType.TYPE_DROP
                }

                // 看过
                R.id.btn_collect -> {
                    entity.collectType = InterestType.TYPE_COLLECT
                    EpCollectType.TYPE_WATCHED
                }

                else -> {
                    entity.collectType = InterestType.TYPE_UNKNOWN
                    EpCollectType.TYPE_REMOVE
                }
            }

            saveEpCollectStatus(entity, epCollectType)
        }
    }

    /**
     * 刷新章节进度
     */
    private fun saveEpCollectStatus(
        entity: MediaChapterEntity,
        @EpCollectType epCollectType: String,
    ) {
        launchUI(
            error = {
                it.printStackTrace()

                isCancelable = true
                showSnackBar(it.errorMsg, error = true)
            },
            block = {
                // 提示加载中
                isCancelable = false
                showSnackBar("正在为你保存进度...", SnackbarUtils.LENGTH_INDEFINITE)

                // 保存进度
                val referer = BgmApiManager.buildReferer(BgmPathType.TYPE_EP, entity.mediaId)
                val newChapters = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.postEpCollect(
                        referer = referer,
                        epId = entity.id,
                        epCollectType = epCollectType,
                        gh = UserHelper.formHash
                    ).parserMediaChapters(entity.mediaId)
                }
                onUpdateResult(newChapters)

                UserHelper.notifyActionChange(BgmPathType.TYPE_EP)

                // 关闭
                isCancelable = true
                hideSnackBar()
                dismissAllowingStateLoss()
            }
        )
    }

    private fun jumpDetail(entity: MediaChapterEntity) {
        dismissAllowingStateLoss()
        RouteHelper.jumpTopicDetail(entity.id, TopicType.TYPE_EP)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog ?: return
        val window = dialog.window ?: return

        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.setDimAmount(0.25f)
        window.updateWindowParams {
            width = ScreenUtils.getScreenWidth() - 32.dpi
            gravity = Gravity.CENTER
        }
    }

    companion object {

        fun show(
            fragmentManager: FragmentManager,
            chapterEntity: MediaChapterEntity,
            @MediaType mediaType: String,
            onUpdateResultListener: (List<MediaChapterEntity>) -> Unit = {},
        ) {
            MediaEpCollectDialog().apply {
                onUpdateResult = onUpdateResultListener
                arguments = bundleOf(
                    NavKey.KEY_PARCELABLE to chapterEntity,
                    NavKey.KEY_STRING to mediaType
                )
            }.show(fragmentManager, "MediaEpCollectDialog")
        }
    }
}