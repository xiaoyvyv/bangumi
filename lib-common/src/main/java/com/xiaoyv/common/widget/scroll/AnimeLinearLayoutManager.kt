package com.xiaoyv.common.widget.scroll

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils

/**
 * Class: [AnimeLinearLayoutManager]
 *
 * @author why
 * @since 12/5/23
 */
class AnimeLinearLayoutManager : LinearLayoutManager {

    var extraLayoutSpaceScale = 1

    constructor(context: Context?) : super(context)

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    override fun calculateExtraLayoutSpace(state: RecyclerView.State, extraLayoutSpace: IntArray) {
        super.calculateExtraLayoutSpace(state, extraLayoutSpace)
        runCatching {
            extraLayoutSpace[0] = ScreenUtils.getScreenHeight() * extraLayoutSpaceScale
            extraLayoutSpace[1] = ScreenUtils.getScreenWidth() * extraLayoutSpaceScale
        }
    }
}