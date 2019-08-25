package com.test.studo.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.studo.R
import com.test.studo.api.models.User
import kotlinx.android.synthetic.main.fragment_user_profile.view.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*

class UserProfileFragment : Fragment() {

    lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        user = arguments?.getSerializable("user") as User

        view.collapse_toolbar.title = resources.getText(R.string.title_profile)

        view.name_and_surname.text = user.firstName + " " + user.secondName
        view.email.text = user.email

        return view
    }
}
