package com.s1aks.locchecker.domain.entities

import java.time.LocalDateTime

data class MapPosition(
    val id: Int = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val dateTime: LocalDateTime = LocalDateTime.now(),
    var title: String = "",
    var information: String = ""
)
