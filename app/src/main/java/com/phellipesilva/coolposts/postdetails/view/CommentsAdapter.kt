package com.phellipesilva.coolposts.postdetails.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.postdetails.entity.CommentEntity
import kotlinx.android.synthetic.main.comment_list_item.view.*

class CommentsAdapter(private val context: Context) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    private var comments = listOf<CommentEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentsAdapter.CommentViewHolder(
            LayoutInflater.from(context).inflate(R.layout.comment_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    fun updateData(newComments: List<CommentEntity>) {
        this.comments = newComments
        notifyDataSetChanged()
    }

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val commentBodyTextView: TextView = view.commentBody
        private val commentEmailTextView: TextView = view.commentEmail

        fun bind(commentEntity: CommentEntity) {
            commentBodyTextView.text = commentEntity.body
            commentEmailTextView.text = commentEntity.email
        }
    }

}