package com.phellipesilva.coolposts.postdetails.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.phellipesilva.coolposts.R
import com.phellipesilva.coolposts.extensions.load
import com.phellipesilva.coolposts.postlist.data.Post
import kotlinx.android.synthetic.main.activity_post_details.*

class PostDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)
        window.enterTransition = null

        val post = intent.getParcelableExtra<Post>("post")
        setupsCollapsingToolbar(post)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupsCollapsingToolbar(post: Post) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        supportPostponeEnterTransition()
        toolbarImageView.load(
            url = "https://picsum.photos/300/300/?image=${post.id}",
            onLoadingFinished = { supportStartPostponedEnterTransition() }
        )

        authorAvatarImageView.load(
            url = "https://api.adorable.io/avatars/${post.user.userId}",
            rounded = true,
            onLoadingFinished = { supportStartPostponedEnterTransition() }
        )

        postBodyTextView.text = post.body
        postTitleTextView.text = post.title
    }
}
