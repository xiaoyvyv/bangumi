package com.xiaoyv.bangumi.ui.feature.scheme

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.EncodeUtils
import com.xiaoyv.bangumi.databinding.ActivitySchemeBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

/**
 * Class: [SchemeActivity]
 *
 * @author why
 * @since 12/10/23
 */
class SchemeActivity : BaseBindingActivity<ActivitySchemeBinding>() {

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        val scheme = intent.scheme ?: return
        val uri = intent.data ?: return
        handleDeepLink(scheme, uri)
    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initListener() {
        binding.btnJump.setOnFastLimitClickListener {
            ActivityUtils.startActivity(
                Intent.createChooser(
                    Intent.parseUri("https://bgm.tv/subject_search/", Intent.URI_ALLOW_UNSAFE),
                    "跳转"
                )
            )
        }
    }

    private fun handleDeepLink(scheme: String, uri: Uri) {
        debugLog { "Scheme: $scheme, uri: $uri" }

        when (scheme) {
            "bgm" -> {
                val routeData = uri.getQueryParameter("data").orEmpty().let {
                    EncodeUtils.base64Decode(it).decodeToString()
                }
                if (routeData.isNotBlank()) {
                    RouteHelper.handleUrl(routeData)
                }
            }

            else -> RouteHelper.handleUrl(uri.toString())
        }
        finish()
    }
}