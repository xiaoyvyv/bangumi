package com.xiaoyv.bangumi.ui.feature.summary

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.EncryptUtils
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.exception.NeedConfigException
import com.xiaoyv.common.api.parser.parseHtml
import com.xiaoyv.common.api.request.MicrosoftTranslateParam
import com.xiaoyv.common.api.response.MicrosoftJwtPayload
import com.xiaoyv.common.helper.CacheHelper
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserTokenHelper
import com.xiaoyv.common.kts.fromJson
import com.xiaoyv.common.kts.randId
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.orEmpty
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * Class: [SummaryViewModel]
 *
 * @author why
 * @since 12/10/23
 */
class SummaryViewModel : BaseViewModel() {
    internal var summary: Array<out String> = emptyArray()
    internal var summaryOriginal = MutableLiveData<List<CharSequence>?>()
    internal var summaryTranslate = MutableLiveData<String>()
    internal var isShowOriginal = true

    internal val onNeedConfig = UnPeekLiveData<Unit>()

    /**
     * 待翻译的文本
     */
    private val needTranslateText: String
        get() = summary.joinToString("\n") {
            it.parseHtml(imageGetter = null).toString().trim()
        }

    private val cacheKey: String
        get() = EncryptUtils.encryptMD5ToString(needTranslateText)

    /**
     * 直接刷新
     */
    fun showOriginal() {
        launchUI(stateView = loadingViewState) {
            isShowOriginal = true
            summaryOriginal.value = withContext(Dispatchers.IO) {
                summary.map { it.parseHtml(true) }
            }
        }
    }

    fun showTranslate() {
        isShowOriginal = false

        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()

                when (it) {
                    is NeedConfigException -> {
                        onNeedConfig.value = Unit
                    }

                    is HttpException -> {
                        if (it.code() == 401) {
                            ConfigHelper.edgeAuthToken = ""
                        }
                    }

                    else -> {
                        showToastCompat(it.errorMsg)
                    }
                }
            },
            block = {
                // 翻译缓存
                val translate = CacheHelper.readTranslate(cacheKey)
                if (translate.isNotBlank()) {
                    summaryTranslate.value = translate
                    return@launchUI
                }

                // 翻译
                val translateResult = when (ConfigHelper.translateType) {
                    0 -> doTranslateWithMicrosoft()
                    1 -> doTranslateWithBaidu()
                    else -> doTranslateWithBaidu()
                }

                // 缓存结果
                CacheHelper.saveTranslate(cacheKey, translateResult)

                summaryTranslate.value = translateResult
            }
        )
    }

    /**
     * 微软翻译
     */
    private suspend fun doTranslateWithMicrosoft(): String {
        return withContext(Dispatchers.IO) {
            val translateText = needTranslateText
            val microsoftToken = UserTokenHelper.queryMicrosoftToken()
            val translate = BgmApiManager.bgmJsonApi.postMicrosoftTranslate(
                authentication = "Bearer $microsoftToken",
                param = listOf(MicrosoftTranslateParam(text = translateText))
            )

            translate.joinToString("\n") {
                it.translations.orEmpty().joinToString(", ") { translation ->
                    translation.text.orEmpty()
                }
            }
        }
    }

    /**
     * 百度翻译
     */
    private suspend fun doTranslateWithBaidu(): String {
        return withContext(Dispatchers.IO) {
            val translateText = needTranslateText
            val (appId, secret) = ConfigHelper.readBaiduTranslateConfig()
            val salt = randId()
            val sign = generateSign(translateText, appId, secret, salt)

            if (appId.isBlank() || secret.isBlank()) {
                throw NeedConfigException("需要配置百度翻译")
            }

            val result = BgmApiManager.bgmJsonApi.postBaiduTranslate(
                q = translateText,
                appId = appId,
                secret = secret,
                salt = salt,
                sign = sign
            )

            require(result.errorMsg.isNullOrBlank()) { result.errorMsg.orEmpty() }

            result.transResult.orEmpty()
                .joinToString("\n") { it.dst.orEmpty() }
        }
    }

    private fun generateSign(text: String, appId: String, secret: String, salt: String): String {
        return EncryptUtils.encryptMD5ToString("$appId$text$salt$secret").lowercase()
    }
}