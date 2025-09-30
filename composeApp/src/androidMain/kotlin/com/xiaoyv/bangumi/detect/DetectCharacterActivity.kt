package com.xiaoyv.bangumi.detect

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.xiaoyv.bangumi.MainActivity
import com.xiaoyv.bangumi.shared.component.DetectType

/**
 * [DetectCharacterActivity]
 *
 * @since 2025/5/15
 */
class DetectCharacterActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.let {
            startActivity(
                Intent(this, MainActivity::class.java).apply {
                    action = it.action
                    type = it.type
                    putExtra(Intent.EXTRA_REFERRER, DetectType.CHARACTER)
                    putExtras(it.extras ?: Bundle.EMPTY)
                }
            )
        }
        finish()
    }
}