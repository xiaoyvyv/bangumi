package com.xiaoyv.bangumi.shared.data.parser.bbcode


// BBCode
internal const val ElementCodeB = "b"
internal const val ElementCodeI = "i"
internal const val ElementCodeU = "u"
internal const val ElementCodeS = "s"
internal const val ElementCodeSize = "size"
internal const val ElementCodeColor = "color"
internal const val ElementCodeUrl = "url"
internal const val ElementCodeImg = "img"
internal const val ElementCodeQuote = "quote"
internal const val ElementCodeCode = "code"
internal const val ElementCodeCenter = "center"
internal const val ElementCodeMask = "mask"
internal const val ElementCodeList = "list"
internal const val ElementCodeListItem = "*"

// BBCode
val defaultSupportedTags = setOf(
    ElementCodeB,
    ElementCodeI,
    ElementCodeU,
    ElementCodeS,
    ElementCodeSize,
    ElementCodeColor,
    ElementCodeUrl,
    ElementCodeImg,
    ElementCodeQuote,
    ElementCodeCode,
    ElementCodeList,
    ElementCodeListItem,
    ElementCodeCenter,
    ElementCodeMask,
)
