package com.xiaoyv.bangumi.ui.feature.post.topic

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.UriUtils
import com.xiaoyv.bangumi.ui.feature.post.BasePostViewModel
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.optImageUrl
import com.xiaoyv.common.config.bean.SampleAvatar
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

/**
 * Class: [PostTopicViewModel]
 *
 * @author why
 * @since 12/8/23
 */
open class PostTopicViewModel : BasePostViewModel() {

}