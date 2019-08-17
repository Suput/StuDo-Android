package com.test.studo.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.studo.R
import kotlinx.android.synthetic.main.collapsing_toolbar.view.*
import kotlinx.android.synthetic.main.fragment_profile_settings.view.*

class ProfileSettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile_settings, container, false)

        view.collapse_toolbar.title = resources.getString(R.string.title_profile)

        view.input_first_name.text = Editable.Factory.getInstance().newEditable(currentUser.user.firstName)
        view.input_second_name.text = Editable.Factory.getInstance().newEditable(currentUser.user.secondName)
        currentUser.user.studentCardNumber?.let {
            view.input_card_number.text = Editable.Factory.getInstance().newEditable(currentUser.user.studentCardNumber)
        }

        view.log_out_btn.setOnClickListener(onLogOutButtonClick)

        return view
    }

    private val onLogOutButtonClick = View.OnClickListener {
        this.activity?.getSharedPreferences("shared", Context.MODE_PRIVATE)?.edit()?.clear()?.apply()

        startActivity(Intent(context, MainActivity::class.java))
        activity?.finish()
    }
}