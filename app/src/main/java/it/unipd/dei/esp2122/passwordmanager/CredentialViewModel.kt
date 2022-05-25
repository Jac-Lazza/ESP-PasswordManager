package it.unipd.dei.esp2122.passwordmanager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CredentialViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CredentialRepository
    val allCredentials : LiveData<List<Credential>>

    init {
        val credentialsDao = CredentialRoomDatabase.getDatabase(application, viewModelScope).credentialDao()
        repository = CredentialRepository(credentialsDao)
        allCredentials = repository.allCredentials
    }

    fun insert(credential: Credential) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(credential)
    }
}
