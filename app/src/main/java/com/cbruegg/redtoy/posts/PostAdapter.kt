package com.cbruegg.redtoy.posts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cbruegg.redtoy.databinding.RowPostBinding
import com.cbruegg.redtoy.net.Post

class PostAdapter(var posts: List<Post>, val onPostClicked: (Post) -> Unit): RecyclerView.Adapter<PostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = RowPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.binding.postTitle.text = post.title
        holder.binding.postAuthor.text = post.author
        // TODO Show thumbnail
        holder.binding.root.setOnClickListener { onPostClicked(post) }
    }

    override fun getItemCount() = posts.size
}

class PostViewHolder(val binding: RowPostBinding) : ViewHolder(binding.root)
