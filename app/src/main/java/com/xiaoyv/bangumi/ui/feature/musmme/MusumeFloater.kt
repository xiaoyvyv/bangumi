@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.feature.musmme

import android.view.View
import android.view.WindowManager
import com.xiaoyv.bangumi.ui.feature.floater.FloatingWindowManger
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.widget.musume.LAppMusumeView
import com.xiaoyv.floater.FloatyService
import com.xiaoyv.floater.FloatyWindow
import com.xiaoyv.widget.kts.dpi

/**
 * Class: [MusumeFloater]
 *
 * @author why
 * @since 11/26/23
 */
class MusumeFloater : FloatyWindow() {
    override fun onCreateView(service: FloatyService): View {
        return LAppMusumeView(service.applicationContext)
    }

    override fun onViewCreated(view: View) {
        debugLog { "xxxxx" }
        val musumeView = view as LAppMusumeView

        musumeView.init()
        musumeView.onResume()
    }

    override fun onAttachToWindow(view: View?, manager: WindowManager) {

    }

    override fun onCreateWindowLayoutParams(): WindowManager.LayoutParams {
        return FloatingWindowManger.createFullWindowLayoutParams().apply {
            width = 200.dpi
            height = 300.dpi
            flags = flags or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        }
    }
}