package com.xiaoyv.bangumi.ui.feature.person.overview

import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.feature.person.overview.binder.PersonOverviewCharacterBinder
import com.xiaoyv.bangumi.ui.feature.person.overview.binder.PersonOverviewGridBinder
import com.xiaoyv.bangumi.ui.feature.person.overview.binder.PersonOverviewOpusBinder
import com.xiaoyv.bangumi.ui.feature.person.overview.binder.PersonOverviewSummaryBinder
import com.xiaoyv.bangumi.ui.feature.person.overview.binder.PersonOverviewVoiceBinder
import com.xiaoyv.common.config.annotation.PersonBinderClickType
import com.xiaoyv.common.helper.RecyclerItemTouchedListener

/**
 * Class: [PersonOverviewAdapter]
 *
 * @author why
 * @since 12/5/23
 */
class PersonOverviewAdapter(
    touchedListener: RecyclerItemTouchedListener,
    clickSubItem: (Int, String) -> Unit
) : BaseMultiItemAdapter<PersonOverviewAdapter.Item>() {
    init {
        val gridPool = RecycledViewPool()

        this.addItemType(TYPE_INFOS, PersonOverviewSummaryBinder(false))
            .addItemType(TYPE_SUMMARY, PersonOverviewSummaryBinder(true))
            .addItemType(TYPE_COOPERATE, PersonOverviewGridBinder(gridPool, touchedListener) {
                clickSubItem(it.type, it.id)
            })
            .addItemType(TYPE_COLLECTOR, PersonOverviewGridBinder(gridPool, touchedListener) {
                clickSubItem(it.type, it.id)
            })
            .addItemType(TYPE_INDEX, PersonOverviewGridBinder(gridPool, touchedListener) {
                clickSubItem(it.type, it.id)
            })
            .addItemType(TYPE_VOICE, PersonOverviewVoiceBinder(
                clickMediaItem = {
                    RouteHelper.jumpMediaDetail(it.media.id)
                },
                clickPersonItem = {
                    RouteHelper.jumpPerson(it.character.id, false)
                }
            ))
            .addItemType(TYPE_CHARACTER, PersonOverviewCharacterBinder (
                clickMediaItem = {
                    RouteHelper.jumpMediaDetail(it.media.id)
                },
                clickPersonItem = {
                    RouteHelper.jumpPerson(it.character.id, false)
                }
            ))
            .addItemType(TYPE_OPUS, PersonOverviewOpusBinder {
                clickSubItem(PersonBinderClickType.TYPE_OPUS, it.id)
            })
            .onItemViewType { position, list ->
                list[position].type
            }
    }

    companion object {
        const val TYPE_INFOS = 1
        const val TYPE_SUMMARY = 2
        const val TYPE_COOPERATE = 3
        const val TYPE_COLLECTOR = 4
        const val TYPE_INDEX = 5
        const val TYPE_VOICE = 6
        const val TYPE_CHARACTER = 7
        const val TYPE_OPUS = 8
        const val TYPE_COMMENT = 9
    }

    data class Item(
        var entity: Any,
        var type: Int,
        var title: String
    )
}