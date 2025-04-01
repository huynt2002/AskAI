package com.example.retrofit.data.local_database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.retrofit.data.local_database.converter.RoleConverter
import com.example.retrofit.data.local_database.database.dao.ConversationDao
import com.example.retrofit.data.local_database.database.dao.MessageDao
import com.example.retrofit.data.local_database.database.model.ConversationEntity
import com.example.retrofit.data.local_database.database.model.MessageEntity

@Database(version = 1, entities = [MessageEntity::class, ConversationEntity::class])
@TypeConverters(RoleConverter::class)
abstract class Database : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao

    abstract fun messageDao(): MessageDao
}
