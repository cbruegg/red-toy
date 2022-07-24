package com.cbruegg.redtoy.posts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.cbruegg.redtoy.R
import com.cbruegg.redtoy.databinding.RowPostBinding
import com.cbruegg.redtoy.db.Post

/**
 * Displays each post's metadata (not their content)
 */
class PostAdapter(var posts: List<Post>, val onPostClicked: (Post) -> Unit): RecyclerView.Adapter<PostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = RowPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.binding.postTitle.text = post.title
        holder.binding.postAuthor.text = post.author
        if (post.thumbnail == "self") {
            holder.binding.postImage.setImageResource(R.drawable.ic_baseline_article_24)
        } else {
            holder.binding.postImage.load(post.thumbnail) {
                crossfade(true)
            }
        }
        holder.binding.root.setOnClickListener { onPostClicked(post) }
    }

    override fun getItemCount() = posts.size
}

class PostViewHolder(val binding: RowPostBinding) : ViewHolder(binding.root)
