package com.phellipesilva.coolposts.postlist.view

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.extensions.load
import com.phellipesilva.coolposts.postdetails.view.PostDetailsActivity
import com.phellipesilva.coolposts.postlist.data.Post
import kotlinx.android.synthetic.main.post_list_item.view.*

class PostListAdapter(private val activity: Activity) :
    RecyclerView.Adapter<PostListAdapter.PostViewHolder>() {

    private var posts = listOf<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(activity).inflate(R.layout.post_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position], activity)
    }

    fun updateData(newPosts: List<Post>) {
        this.posts = newPosts
        notifyDataSetChanged()
    }

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val postTitleTextView: TextView = view.postTitle
        private val postAuthorTextView: TextView = view.postAuthor
        private val authorAvatarImageView: ImageView = view.authorAvatar
        private val thumbnailImageView: ImageView = view.thumbnailImageView

        fun bind(post: Post, activity: Activity) {
            postTitleTextView.text = post.title
            postAuthorTextView.text = post.user.name

            authorAvatarImageView.load(
                url = "https://api.adorable.io/avatars/${post.user.userId}",
                rounded = true
            )

            thumbnailImageView.load(
                url = "https://picsum.photos/400/400/?image=${post.id}",
                rounded = false
            )

            itemView.setOnClickListener {
                val intent = Intent(activity, PostDetailsActivity::class.java)
                intent.putExtra("post", post)

                val pair1 = android.util.Pair(thumbnailImageView as View, "thumbnailImageView")
                val pair2 = android.util.Pair(authorAvatarImageView as View, "authorAvatarImageView")
                val pair3 = android.util.Pair(postTitleTextView as View, "postTitleTextView")
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    activity,
                    pair1,
                    pair2,
                    pair3
                )

                activity.startActivity(intent, options.toBundle())
                activity.window.exitTransition = null
            }
        }
    }
}
