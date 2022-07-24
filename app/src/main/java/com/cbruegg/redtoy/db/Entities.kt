package com.cbruegg.redtoy.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

typealias PostId = String

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey val id: PostId,
    val subreddit: String,
    val author: String,
    val createdUtc: Long,
    val isSelf: Boolean,
    val isVideo: Boolean,
    val numComments: Int,
    val permalink: String,
    val selftext: String?,
    val selftextHtml: String?,
    val thumbnail: String,
    val title: String,
    /** External if is_self **/
    val url: String
)

@Entity(
    tableName = "comments",
    foreignKeys = [ForeignKey(
        entity = Post::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("postId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("postId")]
)
data class Comment(
    @PrimaryKey val id: String,
    val postId: PostId,
    val author: String,
    val body: String?,
    val createdUtc: Long,
    val parentId: String,
    val score: Int
)