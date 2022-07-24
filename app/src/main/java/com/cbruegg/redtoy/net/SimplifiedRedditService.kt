package com.cbruegg.redtoy.net

/**
 * Reddit's tokenless API response is unnecessarily nested. This abstraction layer simplifies the responses.
 */
interface SimplifiedRedditService {
    suspend fun getPosts(
        subreddit: String,
        listing: String,
        limit: Int
    ): List<Post>

    suspend fun getPostData(
        permalink: String
    ): Pair<Post, List<CommentsData>>
}