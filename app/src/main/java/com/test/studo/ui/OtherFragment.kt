package com.test.studo.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.studo.R


class OtherFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_other, container, false)

//        view.collapse_toolbar.title = activity?.resources?.getString(R.string.app_name)
//
//        view.tv1.setOnClickListener{
//            startActivity(Intent(activity, SignInActivity::class.java))
//        }
//
//        view.tv2.setOnClickListener{
//            startActivity(Intent(activity, SignUpActivity::class.java))
//        }
//
//        view.othersList.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, resources.getStringArray(
//            R.array.others
//        ))

        return view
    }
}
