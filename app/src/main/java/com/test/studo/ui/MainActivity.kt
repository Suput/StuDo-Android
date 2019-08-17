package com.test.studo.ui

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.View
import com.test.studo.R
import com.test.studo.api.ApiService
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Context
import com.google.gson.Gson
import com.test.studo.api.models.UserLoginResponse


val api = ApiService.create()

lateinit var currentUser : UserLoginResponse

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val shared = getSharedPreferences("shared", Context.MODE_PRIVATE)

        if(!shared.contains("userWithToken")){
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        currentUser = Gson().fromJson(shared.getString("userWithToken", ""), UserLoginResponse::class.java)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = android.R.attr.windowBackground
        }

        bottom_navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            EventsPageFragment()
        ).commit()
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        var selectedFragment = Fragment()

        when (item.itemId) {
            R.id.navigation_events_page -> selectedFragment = EventsPageFragment()
            R.id.navigation_people_page -> selectedFragment = PeoplePageFragment()
            R.id.navigation_account_page -> selectedFragment = AccountPageFragment()
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, selectedFragment).commit()

        true
    }
}