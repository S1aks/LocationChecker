package com.s1aks.locchecker.impl

import com.s1aks.locchecker.domain.LocalRepository
import com.s1aks.locchecker.domain.MarkersDatabase
import com.s1aks.locchecker.domain.entities.MapPosition
import com.s1aks.locchecker.impl.utils.toDTO
import com.s1aks.locchecker.impl.utils.toModel

class LocalRepositoryImpl(private val database: MarkersDatabase) : LocalRepository {
    override suspend fun getAllMarkers(): List<MapPosition> =
        database.mapPositionDao.getAll().toModel

    override suspend fun saveMarker(marker: MapPosition) {
        database.mapPositionDao.insert(marker.toDTO)
    }

    override suspend fun getMarker(markerId: Int): MapPosition =
        database.mapPositionDao.get(markerId).toModel

    override suspend fun deleteMarker(markerId: Int) {
        database.mapPositionDao.delete(markerId)
    }

    override suspend fun deleteAllMarkers() {
        database.mapPositionDao.clearAll()
    }
}