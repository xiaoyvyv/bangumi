package com.xiaoyv.common.widget.appbar

import android.content.Context
import android.util.AttributeSet
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blankj.utilcode.util.BarUtils
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout

/**
 * Class: [AnimeCollapsingToolbarLayout]
 *
 * @author why
 * @since 12/7/23
 */
class AnimeCollapsingToolbarLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : CollapsingToolbarLayout(context, attrs)