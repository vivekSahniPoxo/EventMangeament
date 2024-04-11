package com.example.eventmangeament.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.eventmangeament.userinfo.AllScannedDetails
import com.example.eventmangeament.userinfo.SyncEntryDetails
import com.example.eventmangeament.userinfo.TempData
import com.example.eventmangeament.userinfo.UserEntryDetails


@Database(entities = [UserEntryDetails::class,SyncEntryDetails::class , AllScannedDetails::class,TempData::class], version = 9, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: EventDatabase? = null

        fun getDatabase(context: Context): EventDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    "Event_database")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
