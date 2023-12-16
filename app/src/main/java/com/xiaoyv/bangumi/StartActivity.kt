package com.xiaoyv.bangumi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blankj.utilcode.util.ActivityUtils
import com.xiaoyv.bangumi.databinding.ActivityMainBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.HomeActivity
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

/**
 * Class: [StartActivity]
 *
 * @author why
 * @since 11/24/23
 */
class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RouteHelper.jumpHome()

        // debug()
        finish()
    }

    private fun debug() {
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.root.setOnFastLimitClickListener {
            ActivityUtils.startActivity(HomeActivity::class.java)
        }

        binding.login.setOnFastLimitClickListener {
            RouteHelper.jumpLogin()
        }

        binding.calendar.setOnFastLimitClickListener {
        }

        binding.robot.setOnFastLimitClickListener {
            RouteHelper.jumpRobot()
        }
    }
}