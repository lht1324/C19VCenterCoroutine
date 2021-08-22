package com.overeasy.c19vcentercoroutine.model.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CenterData::class],
    version = 1
)
abstract class VCenterDatabase : RoomDatabase() {
    abstract fun vCenterDao(): VCenterDao

    companion object {
        @Volatile
        private var INSTANCE: VCenterDatabase? = null

        fun getInstance(context: Context) : VCenterDatabase? {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        VCenterDatabase::class.java,
                        "vcenter_database")
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }
    }
}