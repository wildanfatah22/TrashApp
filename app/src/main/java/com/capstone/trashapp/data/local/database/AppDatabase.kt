package com.capstone.trashapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.capstone.trashapp.data.local.dao.ScanDao
import com.capstone.trashapp.data.local.entity.Scan

@Database(
    entities = [Scan::class], version = 1, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanDao(): ScanDao
}