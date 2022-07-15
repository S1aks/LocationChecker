package com.s1aks.locchecker.ui.markers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.s1aks.locchecker.domain.LocalRepository
import com.s1aks.locchecker.domain.entities.MapPosition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MarkersViewModel(private val repository: LocalRepository) : ViewModel() {
    var data = MutableLiveData<List<MapPosition>>(emptyList())
        private set

    fun getAllMarkers() {
        viewModelScope.launch(Dispatchers.IO) {
            data.postValue(repository.getAllMarkers())
        }
    }

    fun saveMarker(marker: MapPosition) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveMarker(marker)
        }
    }

    fun deleteMarker(markerId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMarker(markerId)
        }
    }
}