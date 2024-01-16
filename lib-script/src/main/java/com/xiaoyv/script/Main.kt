package com.xiaoyv.script

import kotlin.concurrent.thread

/**
 * Class: [Main]
 *
 * @author why
 * @since 1/15/24
 */
object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        thread { BgmScript.main(args) }
        thread { IndexScript.main(args) }
    }
}