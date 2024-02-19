package com.xiaoyv.common.helper

import com.xiaoyv.common.api.response.anime.AnimeSyncEntity

/**
 * Class: [SyncerHelper]
 *
 * @author why
 * @since 1/26/24
 */
class SyncerHelper private constructor() {
    private val bgmCollections: MutableList<AnimeSyncEntity> = mutableListOf()

    private val bgmNotExistWishCollections: MutableList<AnimeSyncEntity> = mutableListOf()
    private val bgmNotExistDoneCollections: MutableList<AnimeSyncEntity> = mutableListOf()
    private val bgmNotExistDoingCollections: MutableList<AnimeSyncEntity> = mutableListOf()

    private val platformWishCollections: MutableList<AnimeSyncEntity> = mutableListOf()
    private val platformDoneCollections: MutableList<AnimeSyncEntity> = mutableListOf()
    private val platformDoingCollections: MutableList<AnimeSyncEntity> = mutableListOf()

    fun cachePlatformWishCollect(platformCollections: List<AnimeSyncEntity>) {
        this.platformWishCollections.clear()
        this.platformWishCollections.addAll(platformCollections)
    }

    fun cachePlatformDoneCollect(platformCollections: List<AnimeSyncEntity>) {
        this.platformDoneCollections.clear()
        this.platformDoneCollections.addAll(platformCollections)
    }

    fun cachePlatformDoingCollect(platformCollections: List<AnimeSyncEntity>) {
        this.platformDoingCollections.clear()
        this.platformDoingCollections.addAll(platformCollections)
    }

    fun cacheBgmCollect(bgmCollections: List<AnimeSyncEntity>) {
        this.bgmCollections.clear()
        this.bgmCollections.addAll(bgmCollections)
    }

    /**
     * 过滤出BGM不存在的数据
     */
    fun filterBgmNotExist() {
        bgmNotExistWishCollections.clear()
        platformWishCollections.forEach {
            if (findBgmItemByName(it.name, it.nameCn) == null) {
                bgmNotExistWishCollections.add(it)
            }
        }

        bgmNotExistDoingCollections.clear()
        platformDoingCollections.forEach {
            if (findBgmItemByName(it.name, it.nameCn) == null) {
                bgmNotExistDoingCollections.add(it)
            }
        }

        bgmNotExistDoneCollections.clear()
        platformDoneCollections.forEach {
            if (findBgmItemByName(it.name, it.nameCn) == null) {
                bgmNotExistDoneCollections.add(it)
            }
        }
    }

    /**
     * 获取第三方平台的条目数据
     */
    fun fetchData(): List<AnimeSyncEntity> {
        return mutableListOf<AnimeSyncEntity>().apply {
            addAll(bgmNotExistWishCollections)
            addAll(bgmNotExistDoingCollections)
            addAll(bgmNotExistDoneCollections)
        }
    }

    /**
     * 查询收藏是已经包含指定名字的条目
     */
    private fun findBgmItemByName(targetName: String, targetNameCn: String): AnimeSyncEntity? {
        return bgmCollections.find { bgm ->
            bgm.name == targetName
                    || bgm.nameCn == targetName
                    || bgm.name == targetNameCn
                    || bgm.nameCn == targetNameCn
        }
    }

    companion object {
        val instance by lazy { SyncerHelper() }
    }
}