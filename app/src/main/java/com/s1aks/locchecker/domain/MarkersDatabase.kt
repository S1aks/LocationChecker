package com.s1aks.locchecker.domain

import androidx.room.Database
import androidx.room.RoomDatabase
import com.s1aks.locchecker.domain.entities.MapPositionDTO

@Database(
    entities = [MapPositionDTO::class],
    version = 1,
    exportSchema = false
)
abstract class MarkersDatabase : RoomDatabase() {
    abstract val mapPositionDao: MapPositionDao

    companion object {
        const val DB_NAME = "database.db"
    }
}