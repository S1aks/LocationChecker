package com.s1aks.locchecker.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.s1aks.locchecker.domain.DateTimeDBConverter
import java.time.LocalDateTime

@Entity(tableName = "locations")
@TypeConverters(DateTimeDBConverter::class)
data class MapPositionDTO(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "dateTime") val dateTime: LocalDateTime,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "information") var information: String
)
