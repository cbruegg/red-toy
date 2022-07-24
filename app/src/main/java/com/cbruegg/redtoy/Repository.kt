package com.cbruegg.redtoy

import com.cbruegg.redtoy.db.AppDatabase
import com.cbruegg.redtoy.db.Comment
import com.cbruegg.redtoy.db.PostId
import com.cbruegg.redtoy.net.CommentsData
import com.cbruegg.redtoy.net.Post
import com.cbruegg.redtoy.net.SimplifiedRedditService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@InstallIn(ViewModelComponent::class, FragmentComponent::class)
@Module
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(repositoryImpl: RepositoryImpl): Repository
}

interface Repository {
    fun postsOfSubreddit(subreddit: String): Flow<List<com.cbruegg.redtoy.db.Post>>
    fun commentsOfPost(postId: PostId): Flow<List<Comment>>
    fun post(postId: PostId): Flow<com.cbruegg.redtoy.db.Post>

    @Throws(IOException::class)
    suspend fun updateSubreddit(subreddit: String)

    @Throws(IOException::class)
    suspend fun updateDataOfPost(permalink: String, subreddit: String)
}

class RepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase,
    private val simplifiedRedditService: SimplifiedRedditService
) : Repository {

    override fun postsOfSubreddit(subreddit: String) = appDatabase.postDao().getOfSubreddit(subreddit)

    override fun commentsOfPost(postId: PostId) = appDatabase.commentDao().getForPostId(postId)

    override fun post(postId: PostId) = appDatabase.postDao().getPost(postId).map { it.single() }

    @Throws(IOException::class)
    override suspend fun updateSubreddit(subreddit: String) {
        val newPosts = simplifiedRedditService.getPosts(subreddit, "hot", 50)
        withContext(Dispatchers.IO) {
            val dbPosts = newPosts.map { it.toDbPost(subreddit) }
            appDatabase.postDao().updateOfSubreddit(subreddit, dbPosts)
        }
    }

    @Throws(IOException::class)
    override suspend fun updateDataOfPost(permalink: String, subreddit: String) {
        val (newPost, newComments) = simplifiedRedditService.getPostData(permalink)
        withContext(Dispatchers.IO) {
            val newDbComments = newComments.map { it.toDbComment(newPost.id) }
            appDatabase.postDao().update(newPost.toDbPost(subreddit))
            appDatabase.commentDao().updateOfPost(newPost.id, newDbComments)
        }
    }
}

private fun Post.toDbPost(subreddit: String): com.cbruegg.redtoy.db.Post {
    return com.cbruegg.redtoy.db.Post(
        id,
        subreddit,
        author,
        created_utc,
        is_self,
        is_video,
        num_comments,
        permalink,
        selftext,
        selftext_html,
        thumbnail,
        title,
        url
    )
}

private fun CommentsData.toDbComment(postId: PostId): Comment {
    return Comment(
        id,
        postId,
        author,
        body,
        created_utc,
        parent_id,
        score
    )
}