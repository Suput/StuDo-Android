package com.test.studo.ui


import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.test.studo.*
import com.test.studo.api.models.Ad
import com.test.studo.api.models.AdCreateRequest
import com.test.studo.api.models.AdEditRequest
import kotlinx.android.synthetic.main.fragment_create_and_edit_ad.*
import kotlinx.android.synthetic.main.fragment_create_and_edit_ad.view.*
import kotlinx.android.synthetic.main.view_collapsing_toolbar.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CreateAndEditAdFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_and_edit_ad, container, false)

        val ad = arguments?.getSerializable("ad") as Ad?

        if (ad != null){
            view.collapse_toolbar.title = resources.getText(R.string.edit_ad)
            view.end_date.text = clientDataFormat.format(serverDataFormat.parse(ad.endTime))
            view.input_title.editText?.setText(ad.name)
            view.input_short_description.editText?.setText(ad.shortDescription)
            view.input_description.editText?.setText(ad.description)
            view.fab.setOnClickListener { editAd(ad.id) }
        } else {
            view.collapse_toolbar.title = resources.getText(R.string.create_ad)
            view.end_date.text = clientDataFormat.format(Calendar.getInstance().time)
            view.fab.setOnClickListener { createAd() }
        }

        view.edit_date_btn.setOnClickListener(onEditDateClickListener)

        return view
    }

    private val onEditDateClickListener = View.OnClickListener {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        DatePickerDialog(it.context, DatePickerDialog.OnDateSetListener {
                _, year, monthOfYear, dayOfMonth ->
            var dayOfMonthStr = dayOfMonth.toString()
            if (dayOfMonth < 10){
                dayOfMonthStr = "0$dayOfMonthStr"
            }
            var monthOfYearStr = (monthOfYear + 1).toString()
            if (monthOfYearStr.toInt() < 10){
                monthOfYearStr = "0$monthOfYearStr"
            }
            end_date.text = "$dayOfMonthStr.$monthOfYearStr.$year"
        }, currentYear, currentMonth, currentDay).show()
    }

    private fun isUserDataIsCorrect() : Boolean{
        if (input_title.editText!!.text.isEmpty()){
            input_title.error = resources.getText(R.string.empty_field_error)
            return false
        } else {
            input_title.isErrorEnabled = false
        }
        if (input_short_description.editText!!.text.isEmpty()){
            input_short_description.error = resources.getText(R.string.empty_field_error)
            return false
        } else {
            input_title.isErrorEnabled = false
        }
        if (input_description.editText!!.text.isEmpty()){
            input_description.error = resources.getText(R.string.empty_field_error)
            return false
        } else {
            input_title.isErrorEnabled = false
        }

        return true
    }

    private fun createAd(){

        if (!isUserDataIsCorrect()){
            return
        }

        var date = clientDataFormat.parse(end_date.text.toString())
        date.hours = 23
        date.minutes = 59
        date.seconds = 59
        date = Date(date.time + 999L)

        val adCreateRequest = AdCreateRequest(
            input_title.editText!!.text.toString(),
            input_description.editText!!.text.toString(),
            input_short_description.editText!!.text.toString(),
            serverDataFormat.format(Calendar.getInstance().time),
            serverDataFormat.format(date),
            null
            )

        api.createAd(adCreateRequest, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<Ad> {
            override fun onResponse(call: Call<Ad>, response: Response<Ad>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, resources.getText(R.string.create_ad_success), Toast.LENGTH_LONG).show()
                    compactAdList = null
                    activity?.supportFragmentManager?.popBackStack()
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null) {
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<Ad>, t: Throwable) {
                Toast.makeText(context, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun editAd(adId : String){

        if (!isUserDataIsCorrect()){
            return
        }

        var date = clientDataFormat.parse(end_date.text.toString())
        date.hours = 23
        date.minutes = 59
        date.seconds = 59
        date = Date(date.time + 999L)

        val adEditRequest = AdEditRequest(
            adId,
            input_title.editText!!.text.toString(),
            input_description.editText!!.text.toString(),
            input_short_description.editText!!.text.toString(),
            serverDataFormat.format(Calendar.getInstance().time),
            serverDataFormat.format(date)
        )

        api.editAd(adEditRequest, "Bearer " + currentUserWithToken.accessToken).enqueue(object : Callback<Ad> {
            override fun onResponse(call: Call<Ad>, response: Response<Ad>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, resources.getText(R.string.edit_ad_success), Toast.LENGTH_LONG).show()
                    compactAdList = null

                    activity?.supportFragmentManager?.popBackStack()
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null) {
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "ERROR CODE: " + response.code().toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<Ad>, t: Throwable) {
                Toast.makeText(context, resources.getText(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
            }
        })
    }
}
