package com.cbruegg.redtoy.db

import androidx.room.Database
import androidx.room.RoomDatabase

// Easier to mock than a RoomDatabase
interface AppDatabase {
    fun postDao(): PostDao
    fun commentDao(): CommentDao
}

@Database(entities = [Post::class, Comment::class], version = 1)
abstract class RoomAppDatabase: RoomDatabase(), AppDatabase {
    abstract override fun postDao(): PostDao
    abstract override fun commentDao(): CommentDao
}