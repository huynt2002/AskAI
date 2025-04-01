package com.example.retrofit.data.local_database.converter

import androidx.room.TypeConverter
import com.example.retrofit.data.ai.model.Role

class RoleConverter {
    @TypeConverter
    fun roleToString(role: Role?): String? {
        return role?.name
    }

    @TypeConverter
    fun stringToRole(string: String?): Role? {
        return string?.let { Role.valueOf(it) }
    }
}
