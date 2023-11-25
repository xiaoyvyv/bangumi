package com.xiaoyv.bangumi.helper

import com.blankj.utilcode.util.ActivityUtils
import com.xiaoyv.bangumi.ui.feature.calendar.CalendarActivity
import com.xiaoyv.bangumi.ui.feature.login.LoginActivity

/**
 * Class: [RouteHelper]
 *
 * @author why
 * @since 11/25/23
 */
object RouteHelper {

    fun jumpCalendar() {
        ActivityUtils.startActivity(CalendarActivity::class.java)
    }

    fun jumpLogin() {
        ActivityUtils.startActivity(LoginActivity::class.java)
    }
}