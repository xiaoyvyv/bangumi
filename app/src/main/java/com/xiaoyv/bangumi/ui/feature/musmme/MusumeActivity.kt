@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.feature.musmme

import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import com.blankj.utilcode.util.Utils
import com.xiaoyv.bangumi.databinding.ActivityMusumeBinding
import com.xiaoyv.bangumi.ui.feature.floater.FloatingWindowManger
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.kts.initNavBack

/**
 * Class: [MusumeActivity]
 *
 * @author why
 * @since 11/25/23
 */
class MusumeActivity : BaseViewModelActivity<ActivityMusumeBinding, MusumeViewModel>() {

    override fun initView() {
        enableEdgeToEdge()

        setSupportActionBar(binding.toolbar)
        binding.toolbar.initNavBack(this)

//         FloatingWindowManger.showRobot()
    }

    override fun initData() {

    }

    override fun initListener() {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}