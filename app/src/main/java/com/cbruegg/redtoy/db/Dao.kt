package com.cbruegg.redtoy.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PostDao {
    @Query("SELECT * FROM posts WHERE subreddit = :subreddit")
    abstract fun getOfSubreddit(subreddit: String): Flow<List<Post>>

    @Insert
    abstract fun insertAll(posts: List<Post>)

    @Query("DELETE FROM posts WHERE subreddit = :subreddit")
    abstract fun deleteOfSubreddit(subreddit: String)

    @Transaction
    open fun updateOfSubreddit(subreddit: String, newPosts: List<Post>) {
        deleteOfSubreddit(subreddit)
        insertAll(newPosts)
    }

    @Update
    abstract fun update(post: Post)

    @Query("SELECT * from posts WHERE id = :postId")
    abstract fun getPost(postId: PostId): Flow<List<Post>>
}

@Dao
abstract class CommentDao {
    @Query("SELECT * FROM comments where postId = :postId")
    abstract fun getForPostId(postId: PostId): Flow<List<Comment>>

    @Insert
    abstract fun insertAll(comments: List<Comment>)

    @Query("DELETE FROM comments WHERE postId = :postId")
    abstract fun deleteOfPost(postId: PostId)

    @Transaction
    open fun updateOfPost(postId: PostId, newComments: List<Comment>) {
        deleteOfPost(postId)
        insertAll(newComments)
    }
}