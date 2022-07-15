package com.s1aks.locchecker.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.s1aks.locchecker.domain.entities.MapPositionDTO

@Dao
interface MapPositionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(position: MapPositionDTO)

    @Query("DELETE FROM locations WHERE id = :markerId")
    fun delete(markerId: Int)

    @Query("SELECT * FROM locations WHERE id = :markerId")
    fun get(markerId: Int): MapPositionDTO

    @Query("DELETE FROM locations")
    fun clearAll()

    @Query("SELECT * FROM locations")
    fun getAll(): List<MapPositionDTO>
}