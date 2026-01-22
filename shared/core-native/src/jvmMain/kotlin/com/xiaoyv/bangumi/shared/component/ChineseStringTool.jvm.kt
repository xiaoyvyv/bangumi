package com.xiaoyv.bangumi.shared.component

import com.github.promeg.pinyinhelper.Pinyin
import com.github.promeg.tinypinyin.lexicons.java.cncity.CnCityDict

object Tools {
    init {
        Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance()))
    }

    fun toPinyin(text: String, spectator: String = "") = Pinyin.toPinyin(text, spectator).orEmpty()
}

actual fun String.toPinYin() = Tools.toPinyin(this)
