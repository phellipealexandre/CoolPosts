package com.phellipesilva.coolposts.postlist.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.postlist.domain.Post
import kotlinx.android.synthetic.main.post_list_item.view.*

class PostListAdapter(private val context: Context) : RecyclerView.Adapter<PostListAdapter.PostViewHolder>() {

    private var posts = listOf<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(context).inflate(R.layout.post_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    fun updateData(newPosts: List<Post>) {
        this.posts = newPosts
        notifyDataSetChanged()
    }

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val postTitleTextView: TextView = view.postTitle
        private val postAuthorTextView: TextView = view.postAuthor
        private val authorAvatar: ImageView = view.authorAvatar

        fun bind(post: Post) {
            postTitleTextView.text = post.title
            postAuthorTextView.text = post.user.name

            Glide.with(itemView.context)
                .load("https://api.adorable.io/avatars/${post.user.userId}")
                .apply(RequestOptions.circleCropTransform())
                .into(authorAvatar)
        }
    }
}
