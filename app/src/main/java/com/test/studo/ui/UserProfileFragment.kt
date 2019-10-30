package com.test.studo.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import com.test.studo.R
import com.test.studo.adapters.ListViewAdapter
import com.test.studo.adapters.ListViewItemModel
import com.test.studo.api
import com.test.studo.api.models.User
import com.test.studo.currentUserWithToken
import com.test.studo.openFragment
import kotlinx.android.synthetic.main.fragment_user_profile.view.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserProfileFragment : Fragment() {

    lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        view.collapse_toolbar.title = resources.getString(R.string.profile)

        user = arguments!!.getSerializable("user") as User

        fillUserData(view)
        getUser(user.id, view)

        view.swipe_container.setOnRefreshListener { getUser(user.id, view, view.swipe_container) }

        view.lv.adapter = ListViewAdapter(context!!, R.layout.listview_row, mutableListOf(
            ListViewItemModel(resources.getString(R.string.ads).toString(), R.drawable.ic_assignment_purple_24dp),
            ListViewItemModel(resources.getString(R.string.resumes).toString(), R.drawable.ic_assignment_ind_purple_24dp)
        ))
        view.lv.onItemClickListener = onListViewItemClickListener

        return view
    }

    private fun getUser(userId : String, view : View, swipeRefreshLayout: SwipeRefreshLayout? = null){
        api.getUser(userId, "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful){
                        user = response.body()!!
                        fillUserData(view)
                    }  else {
                        val errorBodyText = response.errorBody()?.string()
                        if (errorBodyText != null && errorBodyText.isNotEmpty()){
                            Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG).show()
                        }
                    }
                    swipeRefreshLayout?.isRefreshing = false
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                    swipeRefreshLayout?.isRefreshing = false
                }
            })
    }

    private fun fillUserData(view : View){
        view.name_and_surname.text = resources.getString(
            R.string.name_and_surname,
            user.firstName,
            user.secondName
        )
        view.email.text = user.email
    }

    private enum class UserFragmentsItems{
        ADS, RESUMES
    }

    private val onListViewItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        when (position) {
            UserFragmentsItems.ADS.ordinal -> {
                openUserFragment(AdsPageFragment())
            }
            UserFragmentsItems.RESUMES.ordinal -> {
                openUserFragment(ResumesPageFragment())
            }
        }
    }

    private fun openUserFragment(fragment: Fragment){
        val bundle = Bundle()
        bundle.putSerializable("user", user)
        fragment.arguments = bundle

        openFragment(requireActivity(), fragment)
    }
}
