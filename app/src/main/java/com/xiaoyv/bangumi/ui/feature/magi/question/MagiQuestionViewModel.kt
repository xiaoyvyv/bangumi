package com.xiaoyv.bangumi.ui.feature.magi.question

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MagiQuestionEntity
import com.xiaoyv.common.api.parser.impl.parserMagiQuestion
import com.xiaoyv.common.config.annotation.MagiType
import com.xiaoyv.common.config.bean.MagiTab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [MagiQuestionViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MagiQuestionViewModel : BaseViewModel() {
    internal val onMagiQuestionLiveData = MutableLiveData<MagiQuestionEntity?>()

    internal var tab: MagiTab? = null
    internal var magiType: String = MagiType.TYPE_UNKNOWN

    /**
     * 首次请求进入页面不显示回答弹窗
     */
    internal var isRefresh = true

    fun queryQuestion() {
        isRefresh = true

        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
            },
            block = {
                onMagiQuestionLiveData.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryMagi(magiType).parserMagiQuestion()
                }
            }
        )
    }

    /**
     * 回答
     *
     * ```
     * formhash	"275b091c"
     * quiz_id	"6299"
     * cat	""
     * answer	"4"
     * submit	"回答"
     * ```
     */
    fun submitAnswer(item: MagiQuestionEntity.Option) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
            },
            block = {
                isRefresh = false

                onMagiQuestionLiveData.value = withContext(Dispatchers.IO) {
                    val params = onMagiQuestionLiveData.value?.forms.orEmpty().toMutableMap()
                    params[item.field] = item.id
                    params["submit"] = "回答"
                    BgmApiManager.bgmWebApi.postMagiAnswer(params = params).parserMagiQuestion()
                }
            }
        )
    }
}