package com.example.retrofit.presentation.app_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofit.data.auth.RemoteAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class AppViewModel @Inject constructor(private val remoteAuthRepository: RemoteAuthRepository) :
    ViewModel() {
    fun saveUser() {
        viewModelScope.launch(Dispatchers.IO) { remoteAuthRepository.saveUser() }
    }
}
