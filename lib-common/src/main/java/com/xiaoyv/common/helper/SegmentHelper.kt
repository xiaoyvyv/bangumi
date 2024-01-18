package com.xiaoyv.common.helper

import com.huaban.analysis.jieba.WordDictionary
import com.huaban.analysis.jieba.viterbi.FinalSeg
import com.xiaoyv.blueprint.kts.launchProcess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

/**
 * Class: [SegmentHelper]
 *
 * @author why
 * @since 1/17/24
 */
object SegmentHelper {
    fun preload() {
        launchProcess {
            awaitAll(
                async(Dispatchers.IO) { WordDictionary.getInstance() },
                async(Dispatchers.IO) { FinalSeg.getInstance() }
            )
        }
    }
}