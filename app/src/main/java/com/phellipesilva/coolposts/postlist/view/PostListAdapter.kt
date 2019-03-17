package com.phellipesilva.coolposts.postlist.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding.view.RxView
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.extensions.load
import com.phellipesilva.coolposts.postdetails.view.PostDetailsActivity
import com.phellipesilva.coolposts.postlist.data.Post
import kotlinx.android.synthetic.main.activity_post_list.*
import kotlinx.android.synthetic.main.post_list_item.view.*
import java.util.concurrent.TimeUnit

class PostListAdapter(private val activity: Activity) : ListAdapter<Post, PostListAdapter.PostViewHolder>(PostsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(activity).inflate(com.phellipesilva.coolposts.R.layout.post_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position), activity)
    }

    @SuppressLint("CheckResult")
    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val postTitleTextView: TextView = view.postTitleTextView
        private val postAuthorTextView: TextView = view.postAuthorTextView
        private val authorAvatarImageView: ImageView = view.authorAvatarImageView
        private val thumbnailImageView: ImageView = view.thumbnailImageView

        fun bind(post: Post, activity: Activity) {
            postTitleTextView.text = post.title
            postAuthorTextView.text = post.user.name

            authorAvatarImageView.load(
                url = "https://api.adorable.io/avatars/${post.user.userId}",
                rounded = true,
                withCrossFade = true
            )

            thumbnailImageView.load(
                url = "https://picsum.photos/400/400/?image=${post.id}",
                rounded = false,
                withCrossFade = true
            )

            RxView.clicks(itemView)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe {
                    val intent = Intent(activity, PostDetailsActivity::class.java)
                    intent.putExtra("post", post)

                    val options = ActivityOptions.makeSceneTransitionAnimation(
                        activity,
                        android.util.Pair(authorAvatarImageView as View, activity.getString(R.string.user_avatar_transition_id)),
                        android.util.Pair(thumbnailImageView as View, activity.getString(R.string.thumbnail_transition_id)),
                        android.util.Pair(activity.postListAppBarLayout, activity.getString(R.string.post_list_appbarlayout_transition_id)),
                        android.util.Pair(activity.findViewById(android.R.id.navigationBarBackground), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME),
                        android.util.Pair(activity.findViewById(android.R.id.statusBarBackground), Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME)
                    )

                    activity.startActivity(intent, options.toBundle())
                }
        }
    }

    private class PostsDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.title == newItem.title && oldItem.user.name == newItem.user.name
        }
    }
}
