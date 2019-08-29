package com.test.studo.ui

import android.accounts.Account
import android.app.UiModeManager
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.studo.R
import kotlinx.android.synthetic.main.fragment_settings.view.dark_theme_switch

class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES){
            view.dark_theme_switch.isChecked = true
        }

        view.dark_theme_switch.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked){
                AppCompatDelegate.setDefaultNightMode(UiModeManager.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(UiModeManager.MODE_NIGHT_NO)
            }
            activity?.recreate()
        }

        return view
    }
}
