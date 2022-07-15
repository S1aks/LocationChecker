package com.s1aks.locchecker.di

import androidx.room.Room
import com.s1aks.locchecker.domain.LocalRepository
import com.s1aks.locchecker.domain.MarkersDatabase
import com.s1aks.locchecker.impl.LocalRepositoryImpl
import com.s1aks.locchecker.ui.map.MapViewModel
import com.s1aks.locchecker.ui.markers.MarkersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            get(),
            MarkersDatabase::class.java,
            MarkersDatabase.DB_NAME
        ).build()
    }
    single<LocalRepository> { LocalRepositoryImpl(database = get()) }
    viewModel { MapViewModel(repository = get()) }
    viewModel { MarkersViewModel(repository = get()) }
}