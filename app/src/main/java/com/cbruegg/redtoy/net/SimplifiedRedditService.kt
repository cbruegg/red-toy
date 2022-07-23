package com.cbruegg.redtoy.net

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