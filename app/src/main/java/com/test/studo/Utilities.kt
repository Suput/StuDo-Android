package com.test.studo

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity

fun String.isEmail() : Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun openFragment(activity : FragmentActivity, fragment : Fragment){
    activity.supportFragmentManager
        .beginTransaction()
        .setCustomAnimations(
            R.anim.slide_from_right,
            R.anim.slide_to_left,
            R.anim.slide_from_left,
            R.anim.slide_to_right
        )
        .addToBackStack(null)
        .replace(R.id.main_fragment_container, fragment)
        .commit()
}