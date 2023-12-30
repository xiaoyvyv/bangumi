package com.xiaoyv.common.widget.reply

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import com.effective.android.panel.PanelSwitchHelper
import com.effective.android.panel.view.panel.PanelView
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.entity.LoadingState
import com.xiaoyv.blueprint.kts.params
import com.xiaoyv.common.R
import com.xiaoyv.common.api.parser.entity.CommentFormEntity
import com.xiaoyv.common.api.parser.entity.CommentTreeEntity
import com.xiaoyv.common.api.response.ReplyResultEntity
import com.xiaoyv.common.databinding.ViewReplyDialogBinding
import com.xiaoyv.common.helper.BBCode
import com.xiaoyv.common.kts.hideSnackBar
import com.xiaoyv.common.kts.showSnackBar
import com.xiaoyv.common.widget.emoji.grid.UiFacePanel
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.getParcelObj
import com.xiaoyv.widget.kts.updateWindowParams

class ReplyCommentDialog : DialogFragment() {

    private var switchHelper: PanelSwitchHelper? = null

    private val viewModel by viewModels<ReplyCommentViewModel>()

    /**
     * 参数
     */
    private var onReplySuccess: (ReplyResultEntity) -> Unit = {}

    /**
     * 选取图片
     */
    private val selectLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            it?.let { it1 -> viewModel.handleImagePicture(it1) }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ViewReplyDialogBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = ViewReplyDialogBinding.bind(view)

        // 参数
        viewModel.replyForm = arguments?.getParcelObj(NavKey.KEY_PARCELABLE)
        viewModel.targetComment = arguments?.getParcelObj(NavKey.KEY_PARCELABLE_SECOND)
        viewModel.replyJs = arguments?.getString(NavKey.KEY_STRING).orEmpty()

        // 添加新主评论
        if (viewModel.replyJs.isBlank()) {
            binding.edReply.hint = "说点什么吧..."
        }
        // 回复评论
        else {
            binding.edReply.hint = viewModel.buildReplyHint()
        }

        // 加载缓存的评论
        val cacheComment = viewModel.comment
        if (cacheComment.isNotBlank()) {
            runCatching {
                binding.edReply.setText(cacheComment)
                binding.edReply.setSelection(cacheComment.length)
            }
        }

        initListener(binding)
        viewLifecycleOwner.initObserver(binding)
    }

    private fun initListener(binding: ViewReplyDialogBinding) {
        binding.edReply.doAfterTextChanged {
            val input = it.toString().trim()

            viewModel.comment = input
            binding.btnSend.isEnabled = input.isNotBlank()
        }
        /*
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
        */

        // 发送
        binding.btnSend.setOnFastLimitClickListener {
            val input = binding.edReply.text.toString().trim()
            val replyForm = viewModel.replyForm
            if (input.isBlank() || replyForm == null) return@setOnFastLimitClickListener

            // 拼接引用回复
            val replyQuote = viewModel.targetComment?.replyQuote.orEmpty()
            val targetContent = replyQuote + input

            // 回复
            viewModel.sendReply(targetContent)
        }

        // 选取图像
        binding.menuImage.setOnFastLimitClickListener {
            switchHelper?.resetState()

            selectLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.vTmp.setOnClickListener {
            if (switchHelper?.isKeyboardState() == true) {
                switchHelper?.resetState()
            } else {
                dismissAllowingStateLoss()
            }
        }
    }

    private fun LifecycleOwner.initObserver(binding: ViewReplyDialogBinding) {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = viewModel.loadingViewState
        )

        // 图片
        viewModel.onUploadImageResult.observe(this) {
            BBCode.insertImage(binding.edReply, it.orEmpty())

            // 打开键盘
            switchHelper?.toKeyboardState(true)

            binding.edReply.requestFocus()
        }

        viewModel.loadingDialogLiveData.observe(this) {
            when (it.type) {
                LoadingState.STATE_STARTING -> {
                    showSnackBar(message = "发送中...")
                }

                LoadingState.STATE_ENDING -> {
                    hideSnackBar()
                }
            }
        }

        // 发送结果
        viewModel.onReplyLiveData.observe(this) {
            binding.edReply.text = null

            onReplySuccess(it)
            dismissAllowingStateLoss()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog ?: return
        val window = dialog.window ?: return

        window.setGravity(Gravity.BOTTOM)
        window.setSoftInputMode(16)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.setDimAmount(0.0f)
        window.updateWindowParams {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
        }

        if (switchHelper == null) {
            switchHelper = PanelSwitchHelper.Builder(requireActivity().window, view)
                .configHelper(ViewReplyDialogBinding.bind(requireView()))
                .logTrack(false)
                .build(true)
        }
    }

    /**
     * 配置面板
     */
    private fun PanelSwitchHelper.Builder.configHelper(binding: ViewReplyDialogBinding): PanelSwitchHelper.Builder {
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
                            bindEmojiGridView(
                                binding.edReply,
                                panelView.findViewById(R.id.view_emotion)
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * 表情面板
     */
    private fun bindEmojiGridView(edReply: EditText, facePanel: UiFacePanel) {
        facePanel.fillEmojis(requireActivity()) { adapter, _, position ->
            edReply.append(adapter.getItem(position)?.title.orEmpty())
        }
    }

    companion object {

        /**
         * 下面两个参数为空时，表示添加新的主评论
         *
         * @param replyJs 回复的js
         * @param targetComment 回复的评论
         */
        fun show(
            activity: FragmentActivity,
            replyForm: CommentFormEntity,
            replyJs: String? = null,
            targetComment: CommentTreeEntity?,
            onReplyListener: (ReplyResultEntity) -> Unit = {},
        ) {
            ReplyCommentDialog()
                .apply { onReplySuccess = onReplyListener }
                .params(
                    NavKey.KEY_STRING to replyJs,
                    NavKey.KEY_PARCELABLE to replyForm,
                    NavKey.KEY_PARCELABLE_SECOND to targetComment
                )
                .show(activity.supportFragmentManager, "ReplyCommentDialog")
        }
    }
}
