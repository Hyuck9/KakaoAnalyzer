package me.hyuck.kakaoanalyzer.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import me.hyuck.kakaoanalyzer.db.dao.ChatDao
import me.hyuck.kakaoanalyzer.db.entity.Chat

@Database(entities = [Chat::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun chatDao(): ChatDao

    companion object {
        private var instance: AppDatabase? = null
        private const val DATABASE_NAME = "CHATS"

        fun getInstance(context: Context): AppDatabase? {
            if ( instance == null ) {
                synchronized(AppDatabase::class) {
                    instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME).build()
                }
            }
            return instance
        }

        fun destroyDatabase() {
            instance = null
        }
    }
}