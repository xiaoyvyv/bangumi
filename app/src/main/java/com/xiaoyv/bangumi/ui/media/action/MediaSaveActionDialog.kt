package com.xiaoyv.bangumi.ui.media.action

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SpanUtils
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentMediaActionBinding
import com.xiaoyv.bangumi.databinding.FragmentOverviewTagItemBinding
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaCollectForm
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.parser.impl.parserMediaDetail
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.annotation.ScoreStarType
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.getParcelObj
import com.xiaoyv.widget.kts.toast
import com.xiaoyv.widget.kts.updateWindowParams
import com.xiaoyv.widget.kts.useNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

/**
 * Class: [MediaSaveActionDialog]
 *
 * @author why
 * @since 12/3/23
 */
class MediaSaveActionDialog : DialogFragment() {
    private val info = MutableLiveData<MediaCollectForm?>()
    private val clickMyTags = View.OnClickListener {
        refresh {
            showMyTags = false
        }
    }
    private val clickNormalTags = View.OnClickListener {
        refresh {
            showMyTags = true
        }
    }

    private var onUpdateResult: (MediaDetailEntity) -> Unit = { _ -> }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentMediaActionBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        info.value = arguments?.getParcelObj<MediaCollectForm>(NavKey.KEY_PARCELABLE)
        val mediaType = arguments?.getString(NavKey.KEY_STRING).orEmpty()
        val binding = FragmentMediaActionBinding.bind(view)

        info.observe(viewLifecycleOwner) {
            useNotNull(it) {
                onBindView(binding, this)
            }
        }

        binding.tvScoreClear.setOnFastLimitClickListener {
            refresh {
                score = 0
            }
        }

        binding.btnPublic.setOnFastLimitClickListener {
            refresh {
                privacy = if (privacy == 0) 1 else 0
            }
        }

        fillTags(binding.flexTagNormal, binding.etTag, info.value?.normalTags)
        fillTags(binding.flexTagMy, binding.etTag, info.value?.myTags)

        binding.ivStar.onRatingSeekChangeListener = { rating ->
            binding.tvScoreTip.text = ScoreStarType.string((rating * 2).roundToInt())
        }

        // 评分
        binding.ivStar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                refresh {
                    score = (rating * 2).roundToInt()
                }
            }
        }

        binding.etComment.doAfterTextChanged {
            info.value?.comment = it.toString().trim()
        }

        binding.etTag.doAfterTextChanged {
            info.value?.tags = it.toString().trim()
        }

        binding.btnWish.text = InterestType.string(InterestType.TYPE_WISH, mediaType)
        binding.btnCollect.text = InterestType.string(InterestType.TYPE_COLLECT, mediaType)
        binding.btnDropped.text = InterestType.string(InterestType.TYPE_DROPPED, mediaType)
        binding.btnOnHold.text = InterestType.string(InterestType.TYPE_ON_HOLD, mediaType)
        binding.btnDo.text = InterestType.string(InterestType.TYPE_DO, mediaType)

        binding.gpButtons.addOnButtonCheckedListener { _, i, checked ->
            if (!checked) {
                return@addOnButtonCheckedListener
            }

            info.value?.interest = when (i) {
                R.id.btn_wish -> InterestType.TYPE_WISH
                R.id.btn_collect -> InterestType.TYPE_COLLECT
                R.id.btn_do -> InterestType.TYPE_DO
                R.id.btn_on_hold -> InterestType.TYPE_ON_HOLD
                R.id.btn_dropped -> InterestType.TYPE_DROPPED
                else -> InterestType.TYPE_UNKNOWN
            }

            binding.btnSubmit.isEnabled = true
        }

        binding.btnSubmit.setOnFastLimitClickListener {
            useNotNull(info.value) {
                doSendAction(binding, this)
            }
        }

        binding.ivCancel.setOnFastLimitClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun doSendAction(
        binding: FragmentMediaActionBinding,
        form: MediaCollectForm,
    ) {
        launchUI(
            error = {
                hideLoading(binding)
                toast(it.message)
            },
            block = {
                showLoading(binding)
                val media = withContext(Dispatchers.IO) {
                    val paramMap = mapOf(
                        "referer" to form.referer.ifBlank { "subject" },
                        "interest" to form.interest,
                        "rating" to form.score.toString(),
                        "tags" to form.tags.trim(),
                        "comment" to form.comment.trim(),
                        "privacy" to form.privacy.toString(),
                        "update" to form.update.ifBlank { "保存" },
                    )

                    BgmApiManager.bgmWebApi.updateInterest(
                        map = paramMap,
                        mediaId = form.mediaId,
                        gh = form.gh
                    ).parserMediaDetail()
                }
                onUpdateResult(media)

                hideLoading(binding)

                dismissAllowingStateLoss()
            }
        )
    }

    private var loadingJob: Job? = null
    private fun showLoading(binding: FragmentMediaActionBinding) {
        isCancelable = false
        binding.btnSubmit.isEnabled = false
        loadingJob?.cancel()
        loadingJob = launchUI {
            var count = 1
            while (isActive) {
                delay(500)
                binding.btnSubmit.text = "更新"
                repeat(count % 4) {
                    binding.btnSubmit.append(".")
                }
                count++
            }
        }
    }

    private fun hideLoading(binding: FragmentMediaActionBinding) {
        isCancelable = true
        binding.btnSubmit.isEnabled = true
        loadingJob?.cancel()
        binding.btnSubmit.text = "更新"
    }

    private fun fillTags(
        container: ViewGroup,
        input: EditText,
        tags: List<MediaDetailEntity.MediaTag>?,
    ) {
        for (tag in tags.orEmpty()) {
            val tagItemBinding = FragmentOverviewTagItemBinding.inflate(
                container.context.inflater,
                container,
                true
            )
            tagItemBinding.tvTitleTag.setOnFastLimitClickListener {
                if (!input.text.toString().contains(tag.tagName)) {
                    input.append(" ")
                    input.append(tag.tagName)
                }
            }

            SpanUtils.with(tagItemBinding.tvTitleTag)
                .append(tag.tagName)
                .setBold()
                .setForegroundColor(container.context.getAttrColor(GoogleAttr.colorOnSurface))
                .create()
        }
    }


    private fun onBindView(binding: FragmentMediaActionBinding, form: MediaCollectForm) {
        binding.tvTitleCn.text = form.titleCn
        binding.tvTitleNative.text = form.titleNative
        binding.ivStar.rating = form.score / 2f
        binding.etComment.setText(form.comment)
        binding.etTag.setText(form.tags)
        binding.btnPublic.text = if (form.privacy == 0) "公开" else "私密"
        binding.tvScoreTip.text = ScoreStarType.string(form.score)
        binding.flexTagMy.isVisible = form.showMyTags
        binding.flexTagNormal.isVisible = form.showMyTags.not()
        binding.btnSubmit.isEnabled = form.interest != InterestType.TYPE_UNKNOWN

        when (form.interest) {
            InterestType.TYPE_WISH -> binding.gpButtons.check(R.id.btn_wish)
            InterestType.TYPE_COLLECT -> binding.gpButtons.check(R.id.btn_collect)
            InterestType.TYPE_DO -> binding.gpButtons.check(R.id.btn_do)
            InterestType.TYPE_ON_HOLD -> binding.gpButtons.check(R.id.btn_on_hold)
            InterestType.TYPE_DROPPED -> binding.gpButtons.check(R.id.btn_dropped)
        }

        SpanUtils.with(binding.tvTagSwitch)
            .append("常用")
            .setClickSpan(
                requireContext().getAttrColor(if (form.showMyTags) GoogleAttr.colorOnSurfaceVariant else GoogleAttr.colorPrimary),
                false,
                clickMyTags
            )
            .append(" / ")
            .append("我的")
            .setClickSpan(
                requireContext().getAttrColor(if (form.showMyTags) GoogleAttr.colorPrimary else GoogleAttr.colorOnSurfaceVariant),
                false,
                clickNormalTags
            )
            .create()
    }

    private fun refresh(block: MediaCollectForm.() -> Unit) {
        useNotNull(info.value) {
            block(this)
            info.value = this
        }
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
            collectForm: MediaCollectForm?,
            @MediaType mediaType: String,
            onUpdateResultListener: (MediaDetailEntity) -> Unit = {},
        ) {
            MediaSaveActionDialog()
                .apply {
                    onUpdateResult = onUpdateResultListener
                    arguments = bundleOf(
                        NavKey.KEY_PARCELABLE to collectForm?.copy(),
                        NavKey.KEY_STRING to mediaType
                    )
                }
                .show(fragmentManager, "MediaSaveActionDialog")
        }
    }
}