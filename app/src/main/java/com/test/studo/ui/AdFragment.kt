package com.test.studo.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.test.studo.R
import com.test.studo.api
import com.test.studo.api.models.Ad
import com.test.studo.currentUserWithToken
import kotlinx.android.synthetic.main.fragment_ad.*
import kotlinx.android.synthetic.main.fragment_ad.view.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class AdFragment : Fragment() {

    lateinit var ad : Ad

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        arguments?.getString("adId")?.let { getAd(it) }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_ad, container, false)

        view.collapse_toolbar.title = resources.getText(R.string.ad)

        view.name.text = arguments?.getString("name")
        view.short_description.text = arguments?.getString("short_description")
        view.separator_1.visibility = View.VISIBLE
        view.separator_2.visibility = View.VISIBLE

        view.creator_panel.setOnClickListener(onProfilePanelClickListener)

        return view
    }

    private val onProfilePanelClickListener = View.OnClickListener {

        val userProfileFragment = UserProfileFragment()
        val bundle = Bundle()

        bundle.putSerializable("user", ad.user)
        userProfileFragment.arguments = bundle

        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.addSharedElement(avatar, avatar.transitionName)
            ?.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right)
            ?.addToBackStack(null)
            ?.replace(R.id.main_fragment_container, userProfileFragment)
            ?.commit()
    }

    private fun getAd(adId : String){
        api.getOneAd(adId, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<Ad>{
            override fun onResponse(call: Call<Ad>, response: Response<Ad>) {
                if (response.isSuccessful){
                    ad = response.body()!!

                    name.text = ad.name
                    short_description.text = ad.shortDescription
                    description.text = ad.description
                    creator_name_and_surname.text = ad.user.firstName + " " + ad.user.secondName

                    val serverDataFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
                    val clientDataFormat = SimpleDateFormat("dd.MM.yyyy")
                    begin_time.text = clientDataFormat.format(serverDataFormat.parse(ad.beginTime))
                    end_time.text = clientDataFormat.format(serverDataFormat.parse(ad.endTime))
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null){
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<Ad>, t: Throwable) {
                Toast.makeText(context, "No connection with server", Toast.LENGTH_LONG).show()
            }
        })
    }
}
