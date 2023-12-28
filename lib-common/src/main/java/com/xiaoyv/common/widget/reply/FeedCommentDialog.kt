package com.xiaoyv.common.widget.reply

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentActivity
import com.effective.android.panel.PanelSwitchHelper
import com.effective.android.panel.view.panel.PanelView
import com.effective.android.panel.window.PanelDialog
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.R
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.CommentFormEntity
import com.xiaoyv.common.api.parser.entity.CommentTreeEntity
import com.xiaoyv.common.api.parser.impl.parserReplyParam
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.api.response.ReplyResultEntity
import com.xiaoyv.common.databinding.ViewReplyDialogBinding
import com.xiaoyv.common.helper.BBCode
import com.xiaoyv.common.widget.emoji.grid.UiFacePanel
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class FeedCommentDialog(private val activity: FragmentActivity) : PanelDialog(activity) {
    private var switchHelper: PanelSwitchHelper? = null

    /**
     * 参数
     */
    var onReplySuccess: (ReplyResultEntity) -> Unit = {}
    var replyForm: CommentFormEntity? = null
    var targetComment: CommentTreeEntity? = null
    var replyJs: String = ""

    private val binding by lazy {
        ViewReplyDialogBinding.bind(rootView)
    }

    override fun getDialogLayout(): Int {
        return R.layout.view_reply_dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (switchHelper == null) {
            switchHelper = PanelSwitchHelper.Builder(activity.window, rootView)
                .configHelper()
                .logTrack(false)
                .build(true)
        }

        val replyParam = replyJs.parserReplyParam()

        // 添加新主评论
        if (replyJs.isBlank()) {
            binding.edReply.hint = "说点什么吧..."
        }
        // 回复评论
        else {
            // 显示回复的目标评论部分内容
            val replyContent = targetComment?.replyContent.orEmpty().parseHtml().toString()
            val summaryHint = if (replyContent.length > 20) {
                replyContent.substring(0, 20) + "..."
            } else {
                replyContent
            }
            binding.edReply.hint =
                String.format("回复 %s：%s", targetComment?.userName.orEmpty(), summaryHint)
        }

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
            val replyForm = replyForm
            if (input.isBlank() || replyForm == null) return@setOnFastLimitClickListener

            // 拼接引用回复
            val replyQuote = targetComment?.replyQuote.orEmpty()
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
        activity.launchUI(
            error = {
                hideLoading(binding)

                it.printStackTrace()

                activity.toast(it.errorMsg)
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

                dismiss()
            }
        )
    }

    private fun showLoading(binding: ViewReplyDialogBinding) {
//        binding.groupLoading.isVisible = true
//        binding.pbProgress.show()
    }

    private fun hideLoading(binding: ViewReplyDialogBinding) {
//        binding.groupLoading.isVisible = false
//        binding.pbProgress.hide()
    }


    /**
     * 配置面板
     */
    private fun PanelSwitchHelper.Builder.configHelper(): PanelSwitchHelper.Builder {
        return addPanelChangeListener {
            onNone {
                binding.menuEmoji.setSelected(false)
            }
            onKeyboard {
                binding.menuEmoji.setSelected(false)
            }
            onPanel {
                if (it is PanelView) {
                    binding.menuEmoji.setSelected(it.id == R.id.panel_emoji)
                }
            }
            onPanelSizeChange { panelView, _, _, _, _, _ ->
                if (panelView is PanelView) {
                    when (panelView.id) {
                        // 表情面板
                        R.id.panel_emoji -> {
                            bindEmojiGridView(panelView.findViewById(R.id.view_emotion))
                        }
                    }
                }
            }
        }
    }

    /**
     * 表情面板
     */
    private fun bindEmojiGridView(facePanel: UiFacePanel) {
        facePanel.fillEmojis(activity) { adapter, _, position ->
            binding.edReply.append(adapter.getItem(position)?.title.orEmpty())
        }
    }
}