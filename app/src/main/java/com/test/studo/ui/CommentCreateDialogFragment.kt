package com.test.studo.ui


import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.test.studo.R
import com.test.studo.api
import com.test.studo.api.models.CreateCommentRequest
import com.test.studo.currentUserWithToken
import kotlinx.android.synthetic.main.fragment_comment_create_dialog.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.yydcdut.markdown.syntax.edit.EditFactory
import com.yydcdut.markdown.MarkdownProcessor
import kotlinx.android.synthetic.main.fragment_comment_create_dialog.view.*


class CommentCreateDialogFragment : DialogFragment() {

    lateinit var adId : String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        adId = arguments!!.getString("adId")!!

        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_comment_create_dialog, null)

        val markdownProcessor = MarkdownProcessor(context)
        markdownProcessor.factory(EditFactory.create())
        markdownProcessor.live(view.comment_text)

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.comment)
            .setView(view)
            .setPositiveButton(R.string.create, onCreateButtonClick)
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private val onCreateButtonClick = DialogInterface.OnClickListener{ _: DialogInterface, _: Int ->
        val commentText = dialog?.comment_text?.text.toString().trim()
        if (commentText.isNotEmpty()){
            createComment(commentText)
            dismiss()
        }
    }

    private fun createComment(commentText : String){
        api.createComment(adId, CreateCommentRequest(commentText), "Bearer " + currentUserWithToken.accessToken)
            .enqueue(object : Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful){
                        (parentFragment as AdFragment).getAd(adId)
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {}
            })
    }
}
