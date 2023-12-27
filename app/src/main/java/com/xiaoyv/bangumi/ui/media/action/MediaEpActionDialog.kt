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
import com.xiaoyv.common.api.request.EpCollectParam
import com.xiaoyv.common.api.response.api.ApiEpisodeEntity
import com.xiaoyv.common.api.response.api.ApiUserEpEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.EpCollectType
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.hideSnackBar
import com.xiaoyv.common.kts.onStartConfig
import com.xiaoyv.common.kts.showSnackBar
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.getParcelObj
import com.xiaoyv.widget.kts.updateWindowParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [MediaEpActionDialog]
 *
 * @author why
 * @since 12/18/23
 */
class MediaEpActionDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentMediaActionEpBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val entity = arguments?.getParcelObj<ApiUserEpEntity>(NavKey.KEY_PARCELABLE) ?: return
        val mediaType = arguments?.getString(NavKey.KEY_STRING).orEmpty()
        initView(FragmentMediaActionEpBinding.bind(view), entity, mediaType)
    }

    private fun initView(
        binding: FragmentMediaActionEpBinding,
        userEp: ApiUserEpEntity,
        mediaType: String,
    ) {
        val episode = userEp.episode ?: ApiEpisodeEntity()
        binding.tvTitle.text = episode.name.orEmpty().ifBlank { episode.nameCn }
        binding.ivCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
        binding.btnWish.text = InterestType.string(InterestType.TYPE_WISH, mediaType)
        binding.btnDropped.text = InterestType.string(InterestType.TYPE_DROPPED, mediaType)
        binding.btnCollect.text = InterestType.string(InterestType.TYPE_COLLECT, mediaType)

        // 设置状态
        when (userEp.type) {
            EpCollectType.TYPE_WISH -> binding.gpButtons.check(R.id.btn_wish)
            EpCollectType.TYPE_COLLECT -> binding.gpButtons.check(R.id.btn_collect)
            EpCollectType.TYPE_DROPPED -> binding.gpButtons.check(R.id.btn_dropped)
            EpCollectType.TYPE_NONE -> Unit
        }

        binding.tvTitleCn.isVisible = episode.nameCn.orEmpty().isNotBlank()
        binding.tvTitleCn.text = buildString {
            append("中文名：")
            append(episode.nameCn)
        }
        binding.tvDesc.text = episode.airdate
        binding.tvComment.text = String.format("讨论：%d，点击查看", episode.comment)

        binding.tvComment.setOnFastLimitClickListener {
            jumpDetail(episode)
        }

        binding.tvDesc.setOnFastLimitClickListener {
            jumpDetail(episode)
        }

        binding.tvTitleCn.setOnFastLimitClickListener {
            jumpDetail(episode)
        }

        binding.gpButtons.addOnButtonCheckedListener { _, i, checked ->
            if (!checked) {
                return@addOnButtonCheckedListener
            }

            when (i) {
                // 想看
                R.id.btn_wish -> userEp.type = EpCollectType.TYPE_WISH
                // 抛弃
                R.id.btn_dropped -> userEp.type = EpCollectType.TYPE_DROPPED
                // 看过
                R.id.btn_collect -> userEp.type = EpCollectType.TYPE_COLLECT
                // 撤销
                else -> userEp.type = EpCollectType.TYPE_NONE
            }

            saveEpCollectStatus(userEp)
        }
    }

    /**
     * 刷新章节进度
     */
    private fun saveEpCollectStatus(userEp: ApiUserEpEntity) {
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
                withContext(Dispatchers.IO) {
                    BgmApiManager.bgmJsonApi
                        .putEpState(userEp.id, EpCollectParam(userEp.type))
                        .apply { require(isSuccessful) { message() } }
                }

                UserHelper.notifyActionChange(BgmPathType.TYPE_EP)

                // 关闭
                isCancelable = true
                hideSnackBar()
                dismissAllowingStateLoss()
            }
        )
    }

    private fun jumpDetail(entity: ApiEpisodeEntity) {
        dismissAllowingStateLoss()
        RouteHelper.jumpTopicDetail(entity.id.toString(), TopicType.TYPE_EP)
    }

    override fun onStart() {
        super.onStart()
        onStartConfig()
    }

    companion object {

        fun show(
            fragmentManager: FragmentManager,
            epEntity: ApiUserEpEntity,
            @MediaType mediaType: String,
        ) {
            MediaEpActionDialog().apply {
                arguments = bundleOf(
                    NavKey.KEY_PARCELABLE to epEntity,
                    NavKey.KEY_STRING to mediaType
                )
            }.show(fragmentManager, "MediaEpCollectDialog")
        }
    }
}