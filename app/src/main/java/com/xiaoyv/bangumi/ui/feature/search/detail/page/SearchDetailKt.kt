package com.xiaoyv.bangumi.ui.feature.search.detail.page

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import com.xiaoyv.common.databinding.ViewSearchApiFilterBinding
import com.xiaoyv.common.kts.CommonId
import com.xiaoyv.common.kts.showOptionsDialog
import com.xiaoyv.common.widget.scroll.AnimeRecyclerView
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

fun searchApiFilter(
    activity: FragmentActivity,
    layoutInflater: LayoutInflater,
    container: FrameLayout,
    recyclerView: AnimeRecyclerView,
    onChangeOrder: (Int) -> Unit,
    onChangeMode: (Boolean) -> Unit,
    onRefresh: () -> Unit,
) {
    // 过滤菜单
    val filterBinding =
        ViewSearchApiFilterBinding.inflate(layoutInflater, container, true)
    filterBinding.root.doOnPreDraw {
        recyclerView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = filterBinding.root.height
        }
    }

    // 切换
    filterBinding.listType.setOnCheckedStateChangeListener { _, ints ->
        val type = when (ints.firstOrNull()) {
            CommonId.type_0 -> onChangeOrder(0)
            CommonId.type_1 -> onChangeOrder(1)
            else -> null
        }

        // 刷新类型
        if (type != null) {
            onRefresh()
        }
    }

    // 类别切换
    filterBinding.typeMode.setOnFastLimitClickListener {
        activity.showOptionsDialog(
            title = "匹配模式",
            items = listOf("模糊匹配", "精准匹配"),
            onItemClick = { text, position ->
                filterBinding.typeMode.text = text

                onChangeMode(position == 1)
                onRefresh()
            }
        )
    }
}