package com.s1aks.locchecker.impl.utils

import com.s1aks.locchecker.domain.entities.MapPosition
import com.s1aks.locchecker.domain.entities.MapPositionDTO

val MapPosition.toDTO: MapPositionDTO
    get() = MapPositionDTO(
        id,
        latitude,
        longitude,
        dateTime,
        title,
        information
    )

val MapPositionDTO.toModel: MapPosition
    get() = MapPosition(
        id,
        latitude,
        longitude,
        dateTime,
        title,
        information
    )

val List<MapPosition>.toDTO: List<MapPositionDTO>
    get() = this.map { it.toDTO }

val List<MapPositionDTO>.toModel: List<MapPosition>
    get() = this.map { it.toModel }
