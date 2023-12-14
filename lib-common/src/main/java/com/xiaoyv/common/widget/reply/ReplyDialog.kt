@file:Suppress("DEPRECATION")

package com.xiaoyv.common.widget.reply

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ScreenUtils
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.CommentFormEntity
import com.xiaoyv.common.api.parser.entity.CommentTreeEntity
import com.xiaoyv.common.api.parser.impl.parserReplyParam
import com.xiaoyv.common.api.response.ReplyResultEntity
import com.xiaoyv.common.databinding.ViewReplyDialogBinding
import com.xiaoyv.common.helper.BBCode
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.getParcelObj
import com.xiaoyv.widget.kts.toast
import com.xiaoyv.widget.kts.updateWindowParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


/**
 * Class: [ReplyDialog]
 *
 * @author why
 * @since 12/1/23
 */
class ReplyDialog : DialogFragment() {
    var onReplySuccess: (ReplyResultEntity) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ViewReplyDialogBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = ViewReplyDialogBinding.bind(view)
        val replyJs = arguments?.getString(NavKey.KEY_STRING).orEmpty()
        val replyParam = replyJs.parserReplyParam()
        val replyForm = arguments?.getParcelObj<CommentFormEntity>(NavKey.KEY_PARCELABLE)
        val commentEntity = arguments?.getParcelObj<CommentTreeEntity>(NavKey.KEY_PARCELABLE_SECOND)

        debugLog { "ReplyJs - Form: ${replyParam.toJson(true)}" }
        debugLog { "ReplyJs - Form: ${replyForm.toJson(true)}" }
        debugLog { "ReplyJs - Target: ${commentEntity.toJson(true)}" }

        binding.edReply.hint = String.format("回复 %s：", commentEntity?.userName)

        binding.edReply.doAfterTextChanged {
            binding.btnSend.isVisible = it.toString().trim().isNotBlank()
        }

        binding.menuBold.setOnFastLimitClickListener {
            BBCode.insert(binding.edReply, BBCode.menuBold)
        }

        binding.menuItalic.setOnFastLimitClickListener {
            BBCode.insert(binding.edReply, BBCode.menuItalic)
        }

        binding.menuUnderline.setOnFastLimitClickListener {
            BBCode.insert(binding.edReply, BBCode.menuUnderline)
        }

        binding.menuStrikethrough.setOnFastLimitClickListener {
            BBCode.insert(binding.edReply, BBCode.menuStrikethrough)
        }

        binding.menuSize.setOnFastLimitClickListener {
            BBCode.insert(binding.edReply, BBCode.menuFontSize)
        }

        binding.menuMask.setOnFastLimitClickListener {
            BBCode.insert(binding.edReply, BBCode.menuMask)
        }

        binding.menuColor.setOnFastLimitClickListener {
            BBCode.insert(binding.edReply, BBCode.menuFontColor)
        }

        binding.menuLink.setOnFastLimitClickListener {
            BBCode.insert(binding.edReply, BBCode.menuUrl)
        }

        binding.menuImage.setOnFastLimitClickListener {
            BBCode.insert(binding.edReply, BBCode.menuImage)
        }

        binding.menuQuote.setOnFastLimitClickListener {
            BBCode.insert(binding.edReply, BBCode.menuQuote)
        }

        binding.menuCode.setOnFastLimitClickListener {
            BBCode.insert(binding.edReply, BBCode.menuCode)
        }

        binding.btnSend.setOnFastLimitClickListener {
            val input = binding.edReply.text.toString().trim()
            if (input.isBlank() || replyForm == null) return@setOnFastLimitClickListener

            // 拼接引用回复
            val replyQuote = commentEntity?.replyQuote.orEmpty()
            val targetContent = replyQuote + input

            sendReply(binding, targetContent, replyForm, replyParam)
        }
    }

    private fun sendReply(
        binding: ViewReplyDialogBinding,
        input: String,
        replyForm: CommentFormEntity,
        replyParam: CommentFormEntity.CommentParam,
    ) {
        launchUI(
            error = {
                hideLoading(binding)

                it.printStackTrace()

                toast(it.errorMsg)
            },
            block = {
                showLoading(binding)

                val replyResult: ReplyResultEntity = withContext(Dispatchers.IO) {
                    val stringMap = mutableMapOf(
                        "topic_id" to replyParam.topicId.toString(),
                        "related" to replyParam.postId.toString(),
                        "sub_reply_uid" to replyParam.subReplyUid.toString(),
                        "post_uid" to replyParam.postUid.toString(),
                        "related_photo" to "0",
                        "content" to input,
                    )

                    // 隐藏表单
                    stringMap.putAll(replyForm.inputs)

                    // 发布结果
                    BgmApiManager.bgmWebApi.postReply(
                        submitAction = replyForm.action,
                        param = stringMap
                    )
                }

                hideLoading(binding)

                onReplySuccess(replyResult)

                dismissAllowingStateLoss()
            }
        )
    }

    private fun showLoading(binding: ViewReplyDialogBinding) {
        binding.groupLoading.isVisible = true
        binding.pbProgress.show()
    }

    private fun hideLoading(binding: ViewReplyDialogBinding) {
        binding.groupLoading.isVisible = false
        binding.pbProgress.hide()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog ?: return
        val window = dialog.window ?: return

        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.setDimAmount(0f)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        window.updateWindowParams {
            width = ScreenUtils.getScreenWidth()
            gravity = Gravity.BOTTOM
        }
    }

    override fun onResume() {
        super.onResume()
        val binding = ViewReplyDialogBinding.bind(requireView())
        launchUI {
            delay(100)
            KeyboardUtils.showSoftInput(binding.edReply)
        }
    }

    companion object {
        fun show(
            fragmentManager: FragmentManager,
            replyForm: CommentFormEntity,
            replyJs: String? = null,
            targetComment: CommentTreeEntity?,
            onReplyListener: (ReplyResultEntity) -> Unit = {},
        ) {
            ReplyDialog()
                .apply {
                    onReplySuccess = onReplyListener
                    arguments = bundleOf(
                        NavKey.KEY_STRING to replyJs,
                        NavKey.KEY_PARCELABLE to replyForm,
                        NavKey.KEY_PARCELABLE_SECOND to targetComment
                    )
                }
                .show(fragmentManager, "ReplyDialog")
        }
    }
}