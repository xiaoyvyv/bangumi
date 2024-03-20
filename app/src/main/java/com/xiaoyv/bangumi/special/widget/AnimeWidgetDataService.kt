package com.xiaoyv.bangumi.special.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.blankj.utilcode.util.SpanUtils
import com.bumptech.glide.Glide
import com.xiaoyv.bangumi.R
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.response.api.ApiCalendarEntity
import com.xiaoyv.common.config.annotation.SubjectType
import com.xiaoyv.common.currentApplication
import com.xiaoyv.common.kts.centerCropBitmap
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.roundedCorner
import com.xiaoyv.widget.kts.dpf
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class AnimeWidgetDataService : RemoteViewsService() {
    private val context get() = applicationContext

    private val calendar by lazy { Calendar.getInstance() }

    /**
     * 1-7
     */
    private val weekId: Int
        get() = calendar.let {
            it.timeInMillis = System.currentTimeMillis()
            val dayOfWeek = it.get(Calendar.DAY_OF_WEEK)
            if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
        }

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val items = mutableListOf<ApiCalendarEntity.MediaItem>()
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
        )

        return object : RemoteViewsFactory {
            override fun onCreate() {

            }

            override fun onDataSetChanged() {
                runBlocking {
                    debugLog { "数据变化：$appWidgetId" }

                    // 获取今日数据
                    val calendar = BgmApiManager.bgmJsonApi.queryCalendar()
                    val todayAnime = calendar.find { it.weekday?.id == weekId }?.items.orEmpty()

                    items.clear()
                    items.addAll(todayAnime)

                    debugLog { "数据变化：刷新完成: ${items.size}" }
                }
            }

            override fun onDestroy() {
                items.clear()
            }

            override fun getCount(): Int {
                return items.size
            }

            override fun getViewAt(position: Int): RemoteViews? {
                if (position < 0 || position >= items.size) return null

                val item = items[position]
                val score = item.rating?.score ?: 0.0
                val rank = item.rank
                val name = item.nameCn.orEmpty().ifBlank { item.name.orEmpty() }

                val bitmap = runCatching {
                    Glide.with(context)
                        .asBitmap()
                        .load(item.images?.common.orEmpty())
                        .timeout(15000)
                        .submit()
                        .get()
                        .centerCropBitmap(300, 400)
                        .roundedCorner(6.dpf)
                }.getOrNull()

                val itemViews = RemoteViews(
                    packageName,
                    com.xiaoyv.common.R.layout.widget_anime_calendar_item
                )

                itemViews.setImageViewBitmap(R.id.iv_cover, bitmap)
                itemViews.setTextViewText(R.id.tv_rank, if (rank == 0) "" else rank.toString())
                itemViews.setTextViewText(
                    R.id.tv_title, SpanUtils.with(null)
                        .append(name)
                        .append("\u3000")
                        .append(if (score == 0.0) "" else score.toString())
                        .setForegroundColor(Color.parseColor("#FF9800"))
                        .create()
                )
                itemViews.setTextViewText(
                    R.id.tv_desc,
                    buildString {
                        append(SubjectType.string(item.type))
                        append(" / ")
                        append("首播：")
                        append(item.airDate)
                        append(" / ")
                        append("每周 ")
                        append(item.airWeekday)
                    }
                )

                val fillInIntent = Intent().apply {
                    putExtra(NavKey.KEY_PARCELABLE, item)
                }

                itemViews.setOnClickFillInIntent(android.R.id.background, fillInIntent)

                return itemViews
            }

            override fun getLoadingView(): RemoteViews? {
                return null
            }

            override fun getViewTypeCount(): Int {
                return 1
            }

            override fun getItemId(position: Int): Long {
                return position.toLong()
            }

            override fun hasStableIds(): Boolean {
                return true
            }
        }
    }

    companion object {

        /**
         * 刷新
         */
        fun refresh() {
            AppWidgetManager.getInstance(currentApplication).apply {
                val appWidgetIds =
                    getAppWidgetIds(ComponentName(currentApplication, AnimeWidget::class.java))

                notifyAppWidgetViewDataChanged(appWidgetIds, com.xiaoyv.common.R.id.lv_item)
            }
        }
    }
}