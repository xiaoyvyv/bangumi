package com.xiaoyv.common.widget.image

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.isGone
import com.google.android.material.imageview.ShapeableImageView
import com.xiaoyv.common.helper.MiKanHelper
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.showToastCompat

/**
 * Class: [AnimeMikanImageView]
 *
 * @author why
 * @since 3/20/24
 */
class AnimeMikanImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : ShapeableImageView(context, attrs) {
    private var bgmId: String? = null

    init {
        setOnFastLimitClickListener {
            val mapId = MiKanHelper.getMikanMapId(bgmId)
            if (mapId.isNotBlank()) {
                showToastCompat("MikanId = $mapId")
            }
        }
    }

    fun bindBgmId(bgmId: String?) {
        this.bgmId = bgmId
        isGone = bgmId.isNullOrBlank()
    }
}