package com.example.finalproject.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProviderViewModel : ViewModel() {
    private val _providers = MutableStateFlow<List<Provider>>(emptyList())
    val providers: StateFlow<List<Provider>> = _providers

    init {
        fetchProviders()
    }

    private fun fetchProviders() {
        viewModelScope.launch {
            try {
                val snapshot = FirebaseFirestore.getInstance()
                    .collection("providers")
                    .get()
                    .await()
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Provider::class.java)?.copy(id = doc.id)
                }
                _providers.value = list
            } catch (e: Exception) {
                _providers.value = emptyList()
            }
        }
    }
}