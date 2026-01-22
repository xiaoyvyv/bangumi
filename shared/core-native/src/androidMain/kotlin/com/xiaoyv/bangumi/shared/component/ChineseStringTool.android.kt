package com.xiaoyv.bangumi.shared.component

import com.github.promeg.pinyinhelper.Pinyin
import com.github.promeg.tinypinyin.lexicons.android.cncity.CnCityDict
import com.xiaoyv.bangumi.shared.application

object Tools {
    init {
        Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance(application)))
    }

    fun toPinyin(text: String, spectator: String = "") = Pinyin.toPinyin(text, spectator).orEmpty()
}

actual fun String.toPinYin() = Tools.toPinyin(this)