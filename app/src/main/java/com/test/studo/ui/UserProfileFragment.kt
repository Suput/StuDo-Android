package com.test.studo.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import com.test.studo.R
import com.test.studo.adapters.ListViewAdapter
import com.test.studo.adapters.ListViewItemModel
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

        view.collapse_toolbar.title = resources.getText(R.string.profile)

        view.name_and_surname.text = user.firstName + " " + user.secondName
        view.email.text = user.email

        val list = mutableListOf(
            ListViewItemModel(resources.getText(R.string.ads).toString(), R.drawable.ic_assignment_blue_24dp),
            ListViewItemModel(resources.getText(R.string.resumes).toString(), R.drawable.ic_assignment_ind_blue_24dp)
        )

        view.user_fragments_lv.adapter = ListViewAdapter(context!!, R.layout.view_item_listview, list)

        view.user_fragments_lv.onItemClickListener = onListViewItemClickListener

        return view
    }

    enum class UserFragmentsItems(val value: Int) {
        ADS(0), RESUMES(1)
    }

    private val onListViewItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        when (position) {
            UserFragmentsItems.ADS.value -> {
                openUserFragment(AdsPageFragment())
            }
            UserFragmentsItems.RESUMES.value -> {
                openUserFragment(ResumesPageFragment())
            }
        }
    }

    private fun openUserFragment(fragment: Fragment){
        val bundle = Bundle()
        bundle.putSerializable("user", user)
        fragment.arguments = bundle

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.setCustomAnimations(
                R.anim.slide_from_right,
                R.anim.slide_to_left,
                R.anim.slide_from_left,
                R.anim.slide_to_right
            )
            ?.addToBackStack(null)
            ?.replace(R.id.main_fragment_container, fragment)
            ?.commit()
    }
}
