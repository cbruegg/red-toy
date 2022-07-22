package com.cbruegg.redtoy.net

import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RedditService {
    @GET("/r/{subreddit}/{listing}.json")
    suspend fun getPosts(
        @Path("subreddit") subreddit: String,
        @Path("listing") listing: String,
        @Query("limit") limit: Int
    ): PostsResponse

    @GET("{permalink}.json")
    suspend fun getComments(
        @Path("permalink") permalink: String
    ): List<CommentsResponse>
}

@JsonClass(generateAdapter = true)
data class PostsResponse(val data: PostsResponseData)

@JsonClass(generateAdapter = true)
data class PostsResponseData(val children: List<PostsChild>)

@JsonClass(generateAdapter = true)
data class PostsChild(val data: Post)

@JsonClass(generateAdapter = true)
data class Post(val author: String, val created_utc: Long, val id: String, val is_self: Boolean,
                val is_video: Boolean, val num_comments: Int, val permalink: String,
                val selftext: String?, val selftext_html: String?, val thumbnail: String,
                val title: String, /** External if is_self **/ val url: String, val preview: Preview?
)

@JsonClass(generateAdapter = true)
data class Preview(val enabled: Boolean, val images: List<Image>)

@JsonClass(generateAdapter = true)
data class Image(val id: String, val resolutions: List<ImageResolution>, val source: ImageSource)

@JsonClass(generateAdapter = true)
data class ImageResolution(val height: Int, val width: Int, val url: String)

@JsonClass(generateAdapter = true)
data class ImageSource(val height: Int, val width: Int, val url: String)

@JsonClass(generateAdapter = true)
data class CommentsResponse(val data: CommentsResponseData)

@JsonClass(generateAdapter = true)
data class CommentsResponseData(val children: List<CommentsChild>)

@JsonClass(generateAdapter = true)
data class CommentsChild(val kind: String, val data: CommentsData)

@JsonClass(generateAdapter = true)
data class CommentsData(
    val author: String,
    val body: String?,
    val created_utc: Long,
    val id: String,
    val parent_id: String,
    val score: Int
)