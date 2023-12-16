package com.xiaoyv.bangumi.ui.discover.blog.detail

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.BlogDetailEntity
import com.xiaoyv.common.api.parser.impl.parserBlogDetail
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [BlogViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class BlogViewModel : BaseViewModel() {
    internal var blogId: String = ""

    internal val onBlogDetailLiveData = MutableLiveData<BlogDetailEntity?>()
    internal val onDeleteResult = MutableLiveData<Boolean>()

    private val requireBlogUserId: String
        get() = onBlogDetailLiveData.value?.userId.orEmpty()

    /**
     * 是否为自己的帖子
     */
    internal val isMine: Boolean
        get() = requireBlogUserId.isNotBlank() && requireBlogUserId == UserHelper.currentUser.id

    fun queryBlogDetail() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
            },
            block = {
                onBlogDetailLiveData.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryBlogDetail(blogId).parserBlogDetail(blogId)
                }
            }
        )
    }

    /**
     * 删除日志
     */
    fun deleteBlog() {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)
            },
            block = {
                withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.deleteBlog(blogId, UserHelper.formHash)
                }
                onDeleteResult.value = true

                UserHelper.notifyDelete(BgmPathType.TYPE_BLOG)
            }
        )
    }
}