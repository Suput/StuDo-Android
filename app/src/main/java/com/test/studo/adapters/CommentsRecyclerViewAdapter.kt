package com.test.studo.adapters

import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import com.test.studo.R
import com.test.studo.api.models.Comment
import com.test.studo.clientDataFormat
import com.test.studo.clientTimeFormat
import com.test.studo.serverDataFormat
import kotlinx.android.synthetic.main.recyclerview_row_comments.view.*


class CommentsRecyclerViewAdapter(private var commentsList: List<Comment>) : RecyclerView.Adapter<CommentsRecyclerViewAdapter.CommentsViewHolder>() {

    var clickListener : ItemClickListener? = null

    override fun getItemCount() = commentsList.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int) : CommentsViewHolder{
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recyclerview_row_comments, viewGroup, false)
        return CommentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, i: Int) {
        holder.author.text = commentsList[i].author
        holder.text.text = commentsList[i].text

        val fullTime = commentsList[i].commentTime
        holder.time.text = clientTimeFormat.format(serverDataFormat.parse(fullTime))
        holder.date.text = clientDataFormat.format(serverDataFormat.parse(fullTime))
    }

    inner class CommentsViewHolder internal constructor(itemView: View)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var author : TextView = itemView.author
        var time : TextView = itemView.time
        var date : TextView = itemView.date
        var text: TextView = itemView.comment_text

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            clickListener?.onCommentClick(view!!, adapterPosition)
        }
    }

    fun getItem(position : Int) = commentsList[position]

    fun setOnItemClickListener(itemClickListener : ItemClickListener){
        this.clickListener = itemClickListener
    }

    interface ItemClickListener{
        fun onCommentClick(view : View, position: Int)
    }
}