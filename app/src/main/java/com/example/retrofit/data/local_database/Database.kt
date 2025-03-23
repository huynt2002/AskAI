package com.example.retrofit.data.local_database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.retrofit.data.local_database.dao.ConversationDao
import com.example.retrofit.data.local_database.dao.MessageDao
import com.example.retrofit.data.local_database.model.ConversationEntity
import com.example.retrofit.data.local_database.model.MessageEntity

@Database(version = 1, entities = [MessageEntity::class, ConversationEntity::class])
abstract class Database: RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
}
