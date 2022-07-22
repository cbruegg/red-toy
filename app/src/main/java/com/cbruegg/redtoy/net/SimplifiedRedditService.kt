package com.cbruegg.redtoy.net

import javax.inject.Inject

class SimplifiedRedditService @Inject constructor(private val redditService: RedditService) {
    suspend fun getPosts(
        subreddit: String,
        listing: String,
        limit: Int
    ): List<Post> = redditService.getPosts(subreddit, listing, limit).data.children.map { it.data }

    suspend fun getComments(
        permalink: String
    ): List<CommentsData> = redditService.getComments(permalink)
        .flatMap { it.data.children }
        .filter { it.kind == "t3" }
        .map { it.data }
}
