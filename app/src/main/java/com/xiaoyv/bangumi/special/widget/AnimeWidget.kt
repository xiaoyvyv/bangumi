package com.xiaoyv.bangumi.special.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.R
import com.xiaoyv.common.api.response.api.ApiCalendarEntity
import com.xiaoyv.widget.kts.getParcelObj


/**
 * [AnimeWidget]
 */
class AnimeWidget : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_CLICK_LIST_ITEM) {
            val item: ApiCalendarEntity.CalendarEntityItem.Item? =
                intent.extras?.getParcelObj(NavKey.KEY_PARCELABLE)
            RouteHelper.jumpMediaDetail(item?.id.toString())
        }
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        // 可能有多个小部件处于活动状态，因此更新所有小部件。
        appWidgetIds.forEach { appWidgetId ->
            val views = RemoteViews(context.packageName, R.layout.widget_anime_calendar)
            onUpdateViews(context, appWidgetManager, views, appWidgetId)
        }
    }

    private fun onUpdateViews(
        context: Context,
        appWidgetManager: AppWidgetManager,
        views: RemoteViews,
        appWidgetId: Int,
    ) {

        val intent = Intent(context, AnimeWidgetDataService::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
        }

        views.setRemoteAdapter(R.id.lv_item, intent)

        val clickIntent: PendingIntent = Intent(context, AnimeWidget::class.java).run {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            action = ACTION_CLICK_LIST_ITEM
            data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            PendingIntent.getBroadcast(
                context, 0, this,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        }
        views.setPendingIntentTemplate(R.id.lv_item, clickIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    /**
     * 当用户删除小组件时，删除与其关联的首选项。
     */
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {

    }

    /**
     * 创建第一个小部件时
     */
    override fun onEnabled(context: Context) {
    }

    /**
     * 最后一个小部件被禁用
     */
    override fun onDisabled(context: Context) {
    }

    companion object {
        const val ACTION_CLICK_LIST_ITEM = "com.android.bangumi.ACTION_CLICK_LIST_ITEM"
    }
}
