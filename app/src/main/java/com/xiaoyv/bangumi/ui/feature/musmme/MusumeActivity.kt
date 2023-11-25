@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.feature.musmme

import android.view.MenuItem
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import com.xiaoyv.bangumi.databinding.ActivityMusumeBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.widget.musume.LAppDelegate

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
    }

    override fun initData() {
        LAppDelegate.getInstance().onStart(this)
        binding.robotView.init()
    }

    override fun onResume() {
        super.onResume()
        binding.robotView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.robotView.onPause()
    }

    override fun initListener() {

    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pointX = event.x
        val pointY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> LAppDelegate.getInstance().onTouchBegan(pointX, pointY)
            MotionEvent.ACTION_UP -> LAppDelegate.getInstance().onTouchEnd(pointX, pointY)
            MotionEvent.ACTION_MOVE -> LAppDelegate.getInstance().onTouchMoved(pointX, pointY)
        }
        return super.onTouchEvent(event)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}