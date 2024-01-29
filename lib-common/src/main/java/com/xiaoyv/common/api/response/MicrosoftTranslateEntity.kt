package com.xiaoyv.common.api.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


/**
 * Class: [MicrosoftTranslateEntity]
 *
 * @author why
 * @since 1/29/24
 */
class MicrosoftTranslateEntity :
    ArrayList<MicrosoftTranslateEntity.MicrosoftTranslateEntityItem>() {

    @Keep
    @Parcelize
    data class MicrosoftTranslateEntityItem(
        @SerializedName("detectedLanguage")
        var detectedLanguage: DetectedLanguage? = null,
        @SerializedName("translations")
        var translations: List<Translation>? = null,
    ) : Parcelable

    @Keep
    @Parcelize
    data class DetectedLanguage(
        @SerializedName("language")
        var language: String? = null,
        @SerializedName("score")
        var score: Double = 0.0,
    ) : Parcelable

    @Keep
    @Parcelize
    data class Translation(
        @SerializedName("text")
        var text: String? = null,
        @SerializedName("to")
        var to: String? = null,
    ) : Parcelable
}