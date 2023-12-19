package com.xiaoyv.bangumi.ui.feature.person.overview

import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.common.api.parser.entity.PersonEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.config.bean.SampleImageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [PersonOverviewViewModel]
 *
 * @author why
 * @since 12/4/23
 */
class PersonOverviewViewModel : BaseViewModel() {
    /**
     * 人物ID和是否为虚拟人物
     */
    internal var personId: String = ""
    internal var isVirtual: Boolean = false

    suspend fun buildBinderList(entity: PersonEntity): List<AdapterTypeItem> {
        return withContext(Dispatchers.IO) {
            val items = mutableListOf<AdapterTypeItem>()
            items.add(
                AdapterTypeItem(
                    entity = entity,
                    type = PersonOverviewAdapter.TYPE_INFOS,
                    title = "基本信息"
                )
            )
            items.add(
                AdapterTypeItem(
                    entity = entity,
                    type = PersonOverviewAdapter.TYPE_SUMMARY,
                    title = "人物介绍"
                )
            )
            if (isVirtual) {
                // 出演
                if (entity.performers.isNotEmpty()) items.add(
                    AdapterTypeItem(
                        entity = entity.performers,
                        type = PersonOverviewAdapter.TYPE_VOICE,
                        title = "出演",
                    )
                )
            } else {
                // 最近出演的角色
                if (entity.recentCharacters.isNotEmpty()) items.add(
                    AdapterTypeItem(
                        entity = entity.recentCharacters,
                        type = PersonOverviewAdapter.TYPE_CHARACTER,
                        title = "最近出演的角色",
                    )
                )
                // 最近参与
                if (entity.recentOpuses.isNotEmpty()) items.add(
                    AdapterTypeItem(
                        entity = entity.recentOpuses,
                        type = PersonOverviewAdapter.TYPE_OPUS,
                        title = "最近参与",
                    )
                )
                // 合作
                if (entity.recentCooperates.isNotEmpty()) items.add(
                    AdapterTypeItem(
                        entity = entity.recentCooperates.map {
                            SampleImageEntity(
                                it.id,
                                it.avatar,
                                it.name,
                                String.format("x%d", it.times),
                                BgmPathType.TYPE_PERSON
                            )
                        },
                        type = PersonOverviewAdapter.TYPE_COOPERATE,
                        title = "合作",
                    )
                )
            }

            // 谁收藏了
            if (entity.whoCollects.isNotEmpty()) items.add(
                AdapterTypeItem(
                    entity = entity.whoCollects.map {
                        SampleImageEntity(
                            it.id,
                            it.avatar,
                            it.name,
                            it.time,
                            BgmPathType.TYPE_USER
                        )
                    },
                    type = PersonOverviewAdapter.TYPE_COLLECTOR,
                    title = "谁收藏了"
                )
            )

            // 推荐的目录
            if (entity.recommendIndexes.isNotEmpty()) items.add(
                AdapterTypeItem(
                    entity = entity.recommendIndexes.map {
                        SampleImageEntity(
                            it.userId,
                            it.userAvatar,
                            it.title,
                            String.format("by：%s", it.userName),
                            BgmPathType.TYPE_INDEX
                        )
                    },
                    type = PersonOverviewAdapter.TYPE_INDEX,
                    title = "推荐的目录"
                )
            )
            items
        }
    }
}