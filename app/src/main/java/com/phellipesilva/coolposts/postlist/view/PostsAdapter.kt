package com.phellipesilva.coolposts.postlist.view

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
import com.phellipesilva.coolposts.extensions.AndroidTransitionPair
import com.phellipesilva.coolposts.extensions.loadRoundedAvatar
import com.phellipesilva.coolposts.extensions.loadThumbnail
import com.phellipesilva.coolposts.postlist.domain.Post
import java.util.concurrent.TimeUnit

class PostsAdapter : ListAdapter<Post, PostsAdapter.PostViewHolder>(PostsDiffCallback()) {

    private var onItemClickListener: ((Array<AndroidTransitionPair>, Post) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.post_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClickListener)
    }

    fun setOnItemClickListener(onItemClickListener: (Array<AndroidTransitionPair>, Post) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val postTitleTextView: TextView = itemView.findViewById(R.id.postTitleTextView)
        private val postAuthorTextView: TextView = itemView.findViewById(R.id.postAuthorTextView)
        private val authorAvatarImageView: ImageView = itemView.findViewById(R.id.authorAvatarImageView)
        private val thumbnailImageView: ImageView = itemView.findViewById(R.id.thumbnailImageView)

        fun bind(post: Post, onItemClickListener: ((Array<AndroidTransitionPair>, Post) -> Unit)?) {
            postTitleTextView.text = post.title
            postAuthorTextView.text = post.userName

            authorAvatarImageView.loadRoundedAvatar(post.userId)
            thumbnailImageView.loadThumbnail(post.id)

            RxView.clicks(itemView)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe {
                    onItemClickListener?.let { listener ->
                        val transitionElements = arrayOf(
                            AndroidTransitionPair(authorAvatarImageView, itemView.context.getString(R.string.user_avatar_transition_id)),
                            AndroidTransitionPair(thumbnailImageView, itemView.context.getString(R.string.thumbnail_transition_id))
                        )

                        listener(transitionElements, post)
                    }
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
