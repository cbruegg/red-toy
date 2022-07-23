package com.cbruegg.redtoy.net

import javax.inject.Inject

class SimplifiedRedditServiceImpl @Inject constructor(private val redditService: RedditService) :
    SimplifiedRedditService {

    override suspend fun getPosts(
        subreddit: String,
        listing: String,
        limit: Int
    ): List<Post> = redditService.getPosts(subreddit, listing, limit).data.children.map { it.data }

    override suspend fun getPostData(
        permalink: String
    ): Pair<Post, List<CommentsData>> {
        val postData = redditService.getPostData(permalink)
        val post =
            postData.flatMap { it.data.children }.filterIsInstance<PostContentChild.PostChild>()
                .single().data
        val comments =
            postData.flatMap { it.data.children }.filterIsInstance<PostContentChild.CommentChild>()
                .map { it.data }

        return Pair(post, comments)
    }
}
