package com.xiaoyv.bangumi.ui.discover.blog.detail

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.BlogDetailEntity
import com.xiaoyv.common.api.parser.impl.parserBlogDetail
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


    fun queryBlogDetail() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
                onBlogDetailLiveData.value = null
            },
            block = {
                onBlogDetailLiveData.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryBlogDetail(blogId)
                }.parserBlogDetail(blogId)
            }
        )
    }
}