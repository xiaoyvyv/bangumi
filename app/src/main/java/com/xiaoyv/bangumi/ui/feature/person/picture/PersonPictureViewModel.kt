package com.xiaoyv.bangumi.ui.feature.person.picture

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.response.GalleryEntity
import com.xiaoyv.common.api.response.api.ApiCharacterEntity

/**
 * Class: [PersonPictureViewModel]
 *
 * @author why
 * @since 1/13/24
 */
class PersonPictureViewModel : BaseListViewModel<GalleryEntity>() {
    private var character: ApiCharacterEntity? = null
    internal var personId: String = ""

    override suspend fun onRequestListImpl(): List<GalleryEntity> {
        require(personId.isNotBlank()) { "暂无相关图片" }

        val character = character ?: BgmApiManager.bgmJsonApi.queryCharacter(personId).also {
            character = it
        }
        val characterName = fetchCharacterName(character)

        return BgmApiManager.bgmJsonApi
            .queryAnimePicture(
                searchTags = characterName,
                deniedTags = null,
                page = (current - 1).coerceAtLeast(0)
            )
            .posts.orEmpty()
            .map {
                GalleryEntity(
                    id = it.id,
                    width = it.width,
                    height = it.height,
                    size = it.size,
                    imageUrl = it.url,
                    largeImageUrl = it.largeUrl
                )
            }
    }

    private fun fetchCharacterName(character: ApiCharacterEntity): String {
        var name = character.name.orEmpty()
        val nameValue = character.infobox?.find { it.key.contains("别名") }?.value
        if (nameValue is List<*>) {
            val nameMap = hashMapOf<String, String>()
            val nameMapList = nameValue.filterIsInstance<Map<*, *>>()
            nameMapList.forEach {
                nameMap[it["k"].toString()] = it["v"].toString().let { name ->
                    if (name.contains("[\u4e00-\u9fa5]+".toRegex())) name.replace(
                        "\\s+".toRegex(),
                        ""
                    )
                    else name
                }
            }

            val targetName = nameMap["日文名"] ?: nameMap["英文名"] ?: nameMap["罗马字"]
            if (targetName != null) {
                name = targetName.toString()
            }
        }
        return name
    }
}