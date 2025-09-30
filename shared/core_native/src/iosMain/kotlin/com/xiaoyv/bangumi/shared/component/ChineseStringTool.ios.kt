@file:Suppress("OPT_IN_USAGE")

package com.xiaoyv.bangumi.shared.component

import platform.Foundation.NSString
import platform.Foundation.NSStringTransformStripDiacritics
import platform.Foundation.NSStringTransformToLatin
import platform.Foundation.create
import platform.Foundation.stringByApplyingTransform

actual fun String.toPinYin(): String {
    val latin = NSString.create(string = this).stringByApplyingTransform(NSStringTransformToLatin, false).orEmpty()
    return NSString.create(string = latin).stringByApplyingTransform(NSStringTransformStripDiacritics, false).orEmpty()
}