package com.mimo.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AppRule::class, ClosureLog::class],
    version = 1,
    exportSchema = false
)
abstract class MimoDatabase : RoomDatabase() {

    abstract fun appRuleDao(): AppRuleDao
    abstract fun closureLogDao(): ClosureLogDao

    companion object {
        @Volatile private var INSTANCE: MimoDatabase? = null

        fun getInstance(context: Context): MimoDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MimoDatabase::class.java,
                    "mimo_database"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}
