package com.test.studo.ui

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.test.studo.R
import com.test.studo.api.ApiService
import com.test.studo.api.models.userResponse
import kotlinx.android.synthetic.main.activity_main.*

val api = ApiService.create()

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        if (userResponse?.user?.id == null){
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = android.R.attr.windowBackground
        }


        bottom_navigation.setOnNavigationItemSelectedListener {item ->

            var selectedFragment = Fragment()

            when (item.itemId) {
                R.id.navigation_events -> selectedFragment = EventsFragment()
                R.id.navigation_people -> selectedFragment = PeopleFragment()
                R.id.navigation_settings -> selectedFragment = OtherFragment()
            }
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()

            true
        }

        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            EventsFragment()
        ).commit()
    }
}
