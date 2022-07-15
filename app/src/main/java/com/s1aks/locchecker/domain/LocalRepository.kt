package com.s1aks.locchecker.domain

import com.s1aks.locchecker.domain.entities.MapPosition

interface LocalRepository {
    suspend fun getAllMarkers(): List<MapPosition>
    suspend fun saveMarker(marker: MapPosition)
    suspend fun getMarker(markerId: Int): MapPosition
    suspend fun deleteMarker(markerId: Int)
    suspend fun deleteAllMarkers()
}