package com.s1aks.locchecker.ui.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s1aks.locchecker.domain.LocalRepository
import com.s1aks.locchecker.domain.entities.MapPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel(private val repository: LocalRepository) : ViewModel() {
    var data = MutableLiveData(MapPosition())
        private set

    fun saveMarker(marker: MapPosition) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveMarker(marker)
        }
    }

    fun getMarker(markerId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            data.postValue(repository.getMarker(markerId))
        }
    }
}