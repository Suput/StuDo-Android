package com.test.studo.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
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
import com.test.studo.api.models.Organization
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

        ad?.let {
            view.collapse_toolbar.title = resources.getString(R.string.edit_ad)

            view.input_title.editText?.setText(ad.name)
            view.input_short_description.editText?.setText(ad.shortDescription)
            view.input_description.editText?.setText(ad.description)
            try{
                view.end_date?.text = clientDataFormat.format(serverDataFormat.parse(ad.endTime))
            } catch(e : Exception){
                view.end_date?.text = clientDataFormat.format(serverDataFormatWithoutMillis.parse(ad.endTime))
            }

            view.save_ad_fab.setOnClickListener { editAd(ad.id) }

            view.delete_ad_fab.show()
            view.delete_ad_fab.setOnClickListener { deleteAd(ad.id) }
        } ?:run {
            view.collapse_toolbar.title = resources.getString(R.string.create_ad)

            view.end_date.text = clientDataFormat.format(Calendar.getInstance().time)

            view.save_ad_fab.setOnClickListener { createAd() }
        }

        view.edit_date_btn.setOnClickListener(onEditDateClickListener)

        return view
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

        val organization = arguments?.getSerializable("organization") as Organization?

        val adCreateRequest = AdCreateRequest(
            input_title.editText!!.text.toString(),
            input_description.editText!!.text.toString(),
            input_short_description.editText!!.text.toString(),
            serverDataFormat.format(Calendar.getInstance().time),
            serverDataFormat.format(date),
            organization?.id ?:run {
                null
            }
        )

        api.createAd(adCreateRequest, "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<Ad> {
            override fun onResponse(call: Call<Ad>, response: Response<Ad>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, resources.getString(R.string.create_ad_success), Toast.LENGTH_LONG).show()
                    allAdsList = null
                    activity?.supportFragmentManager?.popBackStack()
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null && errorBodyText.isNotEmpty()) {
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<Ad>, t: Throwable) {
                Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
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

        api.editAd(adEditRequest, "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<Ad> {
            override fun onResponse(call: Call<Ad>, response: Response<Ad>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, resources.getString(R.string.edit_ad_success), Toast.LENGTH_LONG).show()
                    allAdsList = null

                    activity?.supportFragmentManager?.popBackStack()
                } else {
                    val errorBodyText = response.errorBody()?.string()
                    if (errorBodyText != null && errorBodyText.isNotEmpty()) {
                        Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<Ad>, t: Throwable) {
                Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun deleteAd(adId : String){

        val onPositiveButtonClick = { _: DialogInterface, _: Int ->
            api.deleteAd(adId, "Bearer " + currentUserWithToken.accessToken)
                .enqueue(object : Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, resources.getString(R.string.delete_ad_success), Toast.LENGTH_LONG).show()
                        allAdsList = null

                        with(activity?.supportFragmentManager){
                            this?.popBackStack()
                            this?.popBackStack()
                        }
                    } else {
                        val errorBodyText = response.errorBody()?.string()
                        if (errorBodyText != null && errorBodyText.isNotEmpty()) {
                            Toast.makeText(context, errorBodyText, Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, resources.getString(R.string.error_code) + response.code(), Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(context, resources.getString(R.string.connection_with_server_error), Toast.LENGTH_LONG).show()
                }
            })
        }

        AlertDialog.Builder(context)
            .setTitle(resources.getString(R.string.delete_ad_confirmation))
            .setCancelable(true)
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .setPositiveButton(resources.getString(R.string.ok), DialogInterface.OnClickListener(function = onPositiveButtonClick))
            .show()
    }

    private fun isUserDataIsCorrect() : Boolean{
        if (input_title.editText!!.text.isEmpty()){
            input_title.error = resources.getString(R.string.empty_field_error)
            return false
        } else {
            input_title.isErrorEnabled = false
        }
        if (input_short_description.editText!!.text.isEmpty()){
            input_short_description.error = resources.getString(R.string.empty_field_error)
            return false
        } else {
            input_title.isErrorEnabled = false
        }
        if (input_description.editText!!.text.isEmpty()){
            input_description.error = resources.getString(R.string.empty_field_error)
            return false
        } else {
            input_title.isErrorEnabled = false
        }

        return true
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
}
