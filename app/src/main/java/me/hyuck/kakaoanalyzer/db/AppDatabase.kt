package me.hyuck.kakaoanalyzer.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import me.hyuck.kakaoanalyzer.db.conveter.DateConverter
import me.hyuck.kakaoanalyzer.db.dao.ChatDao
import me.hyuck.kakaoanalyzer.db.dao.KeywordDao
import me.hyuck.kakaoanalyzer.db.dao.MessageDao
import me.hyuck.kakaoanalyzer.db.entity.Chat
import me.hyuck.kakaoanalyzer.db.entity.Keyword
import me.hyuck.kakaoanalyzer.db.entity.Message

@Database(entities = [Chat::class, Message::class, Keyword::class], version = 2)
@TypeConverters(DateConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun keywordDao(): KeywordDao

    companion object {
        private var instance: AppDatabase? = null
        private const val DATABASE_NAME = "CHATS"

        fun getInstance(context: Context): AppDatabase? {
            if ( instance == null ) {
                synchronized(AppDatabase::class) {
                    instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)//.build()
                        .addMigrations(MIGRATION_1_2).build()
                }
            }
            return instance
        }

        fun destroyDatabase() {
            instance = null
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE chat_info ADD COLUMN isComplete INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }

}