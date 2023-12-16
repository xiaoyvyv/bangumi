package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.config.annotation.PrivacyType
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [PrivacyEntity]
 *
 * @author why
 * @since 12/17/23
 */
@Keep
@Parcelize
data class PrivacyEntity(
    override var id: String = "",
    var title: String = "",
    @PrivacyType var privacyType: Int = PrivacyType.TYPE_ALL,
) : Parcelable, IdEntity
