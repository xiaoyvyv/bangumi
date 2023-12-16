package com.xiaoyv.common.widget.web

import android.view.View
import android.widget.PopupWindow
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.CommentTreeEntity
import com.xiaoyv.common.api.parser.entity.LikeEntity
import com.xiaoyv.common.api.parser.entity.LikeEntity.Companion.normal
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.LikeType
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WebEmojiListener(
    private val activity: BaseViewModelActivity<*, *>,
    private val window: PopupWindow,
    private val entity: CommentTreeEntity,
    private val likeValue: String,
    private var onEmojiResult: (Map<String, List<LikeEntity.LikeAction>>) -> Unit = {},
) : View.OnClickListener {

    override fun onClick(v: View) {
        activity.launchUI(
            state = activity.viewModel.loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                activity.toast(it.errorMsg)
            },
            block = {
                val emojiParam = requireNotNull(entity.emojiParam) { "不支持贴贴" }

                val response = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.toggleLike(
                        type = emojiParam.likeType,
                        mainId = emojiParam.likeMainId,
                        commendId = emojiParam.likeCommentId,
                        likeValue = likeValue,
                        gh = entity.gh
                    ).normal(entity.id)
                }

                onEmojiResult(response)
                window.dismiss()
            }
        )
    }

    /**
     * 将 [BgmPathType] 转为 [LikeType]
     */
    @LikeType
    private fun transformType(@BgmPathType pathType: String): String {
        return LikeType.TYPE_TOPIC
    }
}