package com.xiaoyv.bangumi.ui.feature.setting.translate.model

import androidx.lifecycle.MutableLiveData
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.config.bean.LanguageModel
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Class: [TranslateModelViewModel]
 *
 * @author why
 * @since 1/24/24
 */
class TranslateModelViewModel : BaseListViewModel<LanguageModel>() {
    private var models: MutableList<TranslateRemoteModel> = mutableListOf()
    private val modelManager = RemoteModelManager.getInstance()

    internal val onDownloadLiveData = UnPeekLiveData<Boolean>()

    private val languagePairs = listOf(
        TranslateLanguage.AFRIKAANS to "南非荷兰语",
        TranslateLanguage.ALBANIAN to "阿尔巴尼亚语",
        TranslateLanguage.ARABIC to "阿拉伯语",
        TranslateLanguage.BELARUSIAN to "白俄罗斯语",
        TranslateLanguage.BULGARIAN to "保加利亚语",
        TranslateLanguage.BENGALI to "孟加拉语",
        TranslateLanguage.CATALAN to "加泰罗尼亚语",
        TranslateLanguage.CHINESE to "中文",
        TranslateLanguage.CROATIAN to "克罗地亚语",
        TranslateLanguage.CZECH to "捷克语",
        TranslateLanguage.DANISH to "丹麦语",
        TranslateLanguage.DUTCH to "荷兰语",
        TranslateLanguage.ENGLISH to "英语",
        TranslateLanguage.ESPERANTO to "世界语",
        TranslateLanguage.ESTONIAN to "爱沙尼亚语",
        TranslateLanguage.FINNISH to "芬兰语",
        TranslateLanguage.FRENCH to "法语",
        TranslateLanguage.GALICIAN to "加利西亚语",
        TranslateLanguage.GEORGIAN to "格鲁吉亚语",
        TranslateLanguage.GERMAN to "德语",
        TranslateLanguage.GREEK to "希腊语",
        TranslateLanguage.GUJARATI to "古吉拉特语",
        TranslateLanguage.HAITIAN_CREOLE to "海地克里奥尔语",
        TranslateLanguage.HEBREW to "希伯来语",
        TranslateLanguage.HINDI to "印地语",
        TranslateLanguage.HUNGARIAN to "匈牙利语",
        TranslateLanguage.ICELANDIC to "冰岛语",
        TranslateLanguage.INDONESIAN to "印尼语",
        TranslateLanguage.IRISH to "爱尔兰语",
        TranslateLanguage.ITALIAN to "意大利语",
        TranslateLanguage.JAPANESE to "日语",
        TranslateLanguage.KANNADA to "卡纳达语",
        TranslateLanguage.KOREAN to "韩语",
        TranslateLanguage.LITHUANIAN to "立陶宛语",
        TranslateLanguage.LATVIAN to "拉脱维亚语",
        TranslateLanguage.MACEDONIAN to "马其顿语",
        TranslateLanguage.MARATHI to "马拉地语",
        TranslateLanguage.MALAY to "马来语",
        TranslateLanguage.MALTESE to "马耳他语",
        TranslateLanguage.NORWEGIAN to "挪威语",
        TranslateLanguage.PERSIAN to "波斯语",
        TranslateLanguage.POLISH to "波兰语",
        TranslateLanguage.PORTUGUESE to "葡萄牙语",
        TranslateLanguage.ROMANIAN to "罗马尼亚语",
        TranslateLanguage.RUSSIAN to "俄语",
        TranslateLanguage.SLOVAK to "斯洛伐克语",
        TranslateLanguage.SLOVENIAN to "斯洛文尼亚语",
        TranslateLanguage.SPANISH to "西班牙语",
        TranslateLanguage.SWEDISH to "瑞典语",
        TranslateLanguage.SWAHILI to "斯瓦希里语",
        TranslateLanguage.TAGALOG to "塔加洛语",
        TranslateLanguage.TAMIL to "泰米尔语",
        TranslateLanguage.TELUGU to "泰卢固语",
        TranslateLanguage.THAI to "泰语",
        TranslateLanguage.TURKISH to "土耳其语",
        TranslateLanguage.UKRAINIAN to "乌克兰语",
        TranslateLanguage.URDU to "乌尔都语",
        TranslateLanguage.VIETNAMESE to "越南语",
        TranslateLanguage.WELSH to "威尔士语"
    )

    override suspend fun onRequestListImpl(): List<LanguageModel> {
        models.clear()
        models.addAll(suspendCancellableCoroutine { emit ->
            modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
                .addOnSuccessListener { models ->
                    emit.resumeWith(Result.success(models))
                }
                .addOnFailureListener {
                    emit.resumeWith(Result.failure(it))
                }
        })

        return languagePairs.map { pair ->
            LanguageModel(
                id = pair.first,
                name = pair.second,
                download = models.find { it.language == pair.first } != null
            )
        }
    }

    fun downloadModel(id: String) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
                showToastCompat(it.errorMsg)
            },
            block = {
                val frenchModel = TranslateRemoteModel.Builder(id).build()
                val conditions = DownloadConditions.Builder()
                    .build()

                onDownloadLiveData.value = suspendCancellableCoroutine<Boolean> { emit ->
                    modelManager.download(frenchModel, conditions)
                        .addOnSuccessListener {
                            emit.resumeWith(Result.success(true))
                        }
                        .addOnFailureListener {
                            emit.resumeWith(Result.failure(it))
                        }
                }
            }
        )
    }
}