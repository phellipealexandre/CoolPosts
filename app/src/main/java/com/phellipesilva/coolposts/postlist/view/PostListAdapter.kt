package com.phellipesilva.coolposts.postlist.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding.view.RxView
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.extensions.loadRoundedAvatar
import com.phellipesilva.coolposts.extensions.loadThumbnail
import com.phellipesilva.coolposts.postlist.data.Post
import kotlinx.android.synthetic.main.post_list_item.view.*
import java.util.concurrent.TimeUnit

typealias AndroidTransitionPair = android.util.Pair<View, String>

class PostListAdapter : ListAdapter<Post, PostListAdapter.PostViewHolder>(PostsDiffCallback()) {

    private var onItemClickListener: ((Array<AndroidTransitionPair>, Post) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(com.phellipesilva.coolposts.R.layout.post_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClickListener)
    }

    fun setOnItemClickListener(onItemClickListener: (Array<AndroidTransitionPair>, Post) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    @SuppressLint("CheckResult")
    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val postTitleTextView: TextView = itemView.postTitleTextView
        private val postAuthorTextView: TextView = itemView.postAuthorTextView
        private val authorAvatarImageView: ImageView = itemView.authorAvatarImageView
        private val thumbnailImageView: ImageView = itemView.thumbnailImageView

        fun bind(post: Post, onItemClickListener: ((Array<AndroidTransitionPair>, Post) -> Unit)?) {
            postTitleTextView.text = post.title
            postAuthorTextView.text = post.userName

            authorAvatarImageView.loadRoundedAvatar(post.userId)
            thumbnailImageView.loadThumbnail(post.id)

            RxView.clicks(itemView)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe {
                    val transitionElements = arrayOf(
                        AndroidTransitionPair(authorAvatarImageView, itemView.context.getString(R.string.user_avatar_transition_id)),
                        AndroidTransitionPair(thumbnailImageView, itemView.context.getString(R.string.thumbnail_transition_id))
                    )

                    onItemClickListener?.invoke(transitionElements, post)
                }
        }
    }

    private class PostsDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.title == newItem.title && oldItem.userName == newItem.userName
        }
    }
}
