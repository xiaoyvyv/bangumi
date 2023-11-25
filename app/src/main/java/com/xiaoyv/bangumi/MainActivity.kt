package com.xiaoyv.bangumi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blankj.utilcode.util.ActivityUtils
import com.xiaoyv.bangumi.databinding.ActivityMainBinding
import com.xiaoyv.bangumi.ui.HomeActivity
import com.xiaoyv.bangumi.ui.feature.calendar.CalendarActivity
import com.xiaoyv.bangumi.ui.feature.login.LoginActivity
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            ActivityUtils.startActivity(LoginActivity::class.java)
        }
        binding.calendar.setOnFastLimitClickListener {
            ActivityUtils.startActivity(CalendarActivity::class.java)
        }
    }
}