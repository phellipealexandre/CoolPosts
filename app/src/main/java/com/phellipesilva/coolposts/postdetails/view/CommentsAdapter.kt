package com.phellipesilva.coolposts.postdetails.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.postdetails.data.Comment
import kotlinx.android.synthetic.main.comment_list_item.view.*

class CommentsAdapter : ListAdapter<Comment, CommentsAdapter.CommentViewHolder>(CommentsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentsAdapter.CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.comment_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val commentBodyTextView: TextView = view.commentBody
        private val commentEmailTextView: TextView = view.commentEmail

        fun bind(comment: Comment) {
            commentBodyTextView.text = comment.body
            commentEmailTextView.text = comment.email
        }
    }

    private class CommentsDiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.body == newItem.body && oldItem.email == newItem.email
        }
    }

}
