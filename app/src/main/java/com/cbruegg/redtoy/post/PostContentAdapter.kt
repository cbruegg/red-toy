package com.cbruegg.redtoy.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.cbruegg.redtoy.databinding.RowCommentBinding
import com.cbruegg.redtoy.databinding.RowFullSelfPostBinding
import com.cbruegg.redtoy.net.CommentsData
import com.cbruegg.redtoy.net.Post

// TODO Write tests
// TODO Add support for link posts

class PostContentAdapter(var post: Post?, var comments: List<CommentsData>): RecyclerView.Adapter<PostContentViewHolder>() {
    private enum class ViewType { Post, Comment }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostContentViewHolder {
        return when (viewType) {
            ViewType.Post.ordinal ->
                PostContentViewHolder.SelfPostContent(RowFullSelfPostBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ViewType.Comment.ordinal ->
                PostContentViewHolder.Comment(RowCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException("Unexpected viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: PostContentViewHolder, position: Int) {
        if (position == 0) {
            holder as PostContentViewHolder.SelfPostContent
            val post = post
            if (post == null) {
                holder.binding.selfPostText.text = ""
                holder.binding.selfPostTitle.text = ""
                // TODO Activate progress bar
            } else {
                holder.binding.selfPostText.text = post.selftext
                holder.binding.selfPostTitle.text = post.title
                // TODO Deactivate progress bar
            }
        } else {
            holder as PostContentViewHolder.Comment
            val comment = comments[position - 1]
            holder.binding.commentAuthor.text = comment.author
            holder.binding.commentContent.text = comment.body
        }
    }

    override fun getItemCount() = 1 + comments.size

    override fun getItemViewType(position: Int) =
        if (position == 0) ViewType.Post.ordinal else ViewType.Comment.ordinal
}

sealed class PostContentViewHolder(binding: ViewBinding): ViewHolder(binding.root) {
    class SelfPostContent(val binding: RowFullSelfPostBinding): PostContentViewHolder(binding)
    class Comment(val binding: RowCommentBinding): PostContentViewHolder(binding)
}