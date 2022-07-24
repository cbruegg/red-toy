package com.cbruegg.redtoy.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.cbruegg.redtoy.databinding.RowCommentBinding
import com.cbruegg.redtoy.databinding.RowFullLinkPostBinding
import com.cbruegg.redtoy.databinding.RowFullSelfPostBinding
import com.cbruegg.redtoy.databinding.RowLoadingBinding
import com.cbruegg.redtoy.db.Comment
import com.cbruegg.redtoy.db.Post
import io.noties.markwon.Markwon

/**
 * Displays the post at position 0 and the comments after that.
 */
class PostContentAdapter(
    var post: Post?,
    var comments: List<Comment>,
    /**
     * Called when the user clicks on the link of a link post
     */
    val onLinkClick: () -> Unit,
    private val markwon: Markwon
) :
    RecyclerView.Adapter<PostContentViewHolder>() {

    private enum class ViewType { Loading, SelfPost, LinkPost, Comment }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostContentViewHolder {
        return when (viewType) {
            ViewType.SelfPost.ordinal ->
                PostContentViewHolder.SelfPostContent(
                    RowFullSelfPostBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            ViewType.LinkPost.ordinal ->
                PostContentViewHolder.LinkPostContent(
                    RowFullLinkPostBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            ViewType.Comment.ordinal ->
                PostContentViewHolder.Comment(
                    RowCommentBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            ViewType.Loading.ordinal ->
                PostContentViewHolder.Loading(
                    RowLoadingBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            else -> throw IllegalArgumentException("Unexpected viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: PostContentViewHolder, position: Int) {
        if (position == 0) {
            val post = post
            when (holder) {
                is PostContentViewHolder.SelfPostContent -> {
                    if (post == null) { // should not happen, but let's be safe:
                        holder.binding.selfPostText.text = ""
                        holder.binding.selfPostTitle.text = ""
                    } else {
                        markwon.setMarkdown(holder.binding.selfPostText, post.selftext ?: "")
                        holder.binding.selfPostTitle.text = post.title
                    }
                }
                is PostContentViewHolder.LinkPostContent -> {
                    if (post == null) { // should not happen, but let's be safe:
                        holder.binding.linkPostLink.text = ""
                        holder.binding.linkPostLinkCard.setOnClickListener(null)
                        holder.binding.linkPostTitle.text = ""
                    } else {
                        holder.binding.linkPostLink.text = post.url
                        holder.binding.linkPostLinkCard.setOnClickListener { onLinkClick() }
                        holder.binding.linkPostTitle.text = post.title
                    }
                }
                is PostContentViewHolder.Loading -> {} // Nothing to do
                else -> throw IllegalStateException("Did not expect ${holder.javaClass} at position 0!")
            }
        } else {
            holder as PostContentViewHolder.Comment
            val comment = comments[position - 1]
            holder.binding.commentAuthor.text = comment.author
            markwon.setMarkdown(holder.binding.commentContent, comment.body ?: "")
        }
    }

    override fun getItemCount() = 1 + comments.size

    override fun getItemViewType(position: Int): Int {
        val post = post
        return when (position) {
            0 -> when {
                post == null -> ViewType.Loading.ordinal
                post.isSelf -> ViewType.SelfPost.ordinal
                else -> ViewType.LinkPost.ordinal
            }
            else -> ViewType.Comment.ordinal
        }
    }
}

sealed class PostContentViewHolder(binding: ViewBinding) : ViewHolder(binding.root) {
    class LinkPostContent(val binding: RowFullLinkPostBinding) : PostContentViewHolder(binding)
    class SelfPostContent(val binding: RowFullSelfPostBinding) : PostContentViewHolder(binding)
    class Comment(val binding: RowCommentBinding) : PostContentViewHolder(binding)
    class Loading(binding: RowLoadingBinding) : PostContentViewHolder(binding)
}