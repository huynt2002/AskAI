package com.example.retrofit.data.local_database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.retrofit.data.local_database.database.dao.ConversationDao
import com.example.retrofit.data.local_database.database.dao.MessageDao
import com.example.retrofit.data.local_database.database.model.ConversationEntity
import com.example.retrofit.data.local_database.database.model.MessageEntity

@Database(version = 1, entities = [MessageEntity::class, ConversationEntity::class])
abstract class Database: RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
}
