package com.phellipesilva.coolposts.postdetails.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.postdetails.entity.CommentEntity
import kotlinx.android.synthetic.main.comment_list_item.view.*

class CommentsAdapter : ListAdapter<CommentEntity, CommentsAdapter.CommentViewHolder>(CommentsDiffCallback()) {

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

        fun bind(commentEntity: CommentEntity) {
            commentBodyTextView.text = commentEntity.body
            commentEmailTextView.text = commentEntity.email
        }
    }

    private class CommentsDiffCallback : DiffUtil.ItemCallback<CommentEntity>() {
        override fun areItemsTheSame(oldItem: CommentEntity, newItem: CommentEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CommentEntity, newItem: CommentEntity): Boolean {
            return oldItem.body == newItem.body && oldItem.email == newItem.email
        }
    }

}
