package com.xiaoyv.bangumi.ui.feature.sign.up

import android.graphics.Paint
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.SpanUtils
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivitySignUpBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.showToastCompat


/**
 * Class: [SignUpActivity]
 *
 * @author why
 * @since 11/25/23
 */
class SignUpActivity : BaseBindingActivity<ActivitySignUpBinding>() {

    override fun initView() {
        enableEdgeToEdge()
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        binding.tvUsage4.paintFlags = binding.tvUsage4.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        SpanUtils.with(binding.tvPrivacy)
            .append("我已阅读并同意")
            .append("《用户协议》")
            .setClickSpan(getAttrColor(GoogleAttr.colorPrimary), false) {
                RouteHelper.jumpWeb(
                    GlobalConfig.docArgument,
                    fitToolbar = true,
                    smallToolbar = true
                )
            }
            .append("和")
            .append("《隐私政策》")
            .setClickSpan(getAttrColor(GoogleAttr.colorPrimary), false) {
                RouteHelper.jumpWeb(
                    GlobalConfig.docPrivacy,
                    fitToolbar = true,
                    smallToolbar = true
                )
            }
            .create()
    }

    override fun initListener() {
        binding.btnSignUp.setOnFastLimitClickListener {
            if (!binding.checkPrivacy.isChecked) {
                showToastCompat("您必须阅读并同意《用户协议》和《隐私政策》")
                return@setOnFastLimitClickListener
            }

            when (binding.btnGroup.checkedButtonId) {
                R.id.tv_usage_1 -> {
                    showConfirmDialog(
                        title = "温馨提示",
                        message = "Bangumi 仅提供条目资料与个人收藏记录，\n" +
                                "不提供任何在线观看以及下载，\n" +
                                "您可至以下网站收看正版在线内容：\n\n" +
                                "Netflix、爱奇艺、优酷、哔哩哔哩、腾讯视频 ",
                        cancelText = null
                    )
                }

                R.id.tv_usage_2, R.id.tv_usage_3, R.id.tv_usage_4 -> {
                    RouteHelper.jumpSignUpAction()
                    finish()
                }

                else -> showToast("你要注册 Bangumi 做什么呢？")
            }
        }


        binding.tvSignIn.setOnFastLimitClickListener {
            RouteHelper.jumpSignIn()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}