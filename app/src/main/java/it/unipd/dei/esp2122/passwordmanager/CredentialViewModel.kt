package it.unipd.dei.esp2122.passwordmanager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
ViewModel interagisce con la Repository per fornire alla View i dati necessari in un LiveData, cioè un contenitore osservabile
da cui si è notificati ogni volta che il contenuto cambia
*/
class CredentialViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CredentialRepository
    val allCredentials : LiveData<List<Credential>>

    init {
        val credentialsDao = CredentialRoomDatabase.getDatabase(application).credentialDao()
        repository = CredentialRepository(credentialsDao)
        allCredentials = repository.allCredentials
    }

    //Funzioni di accesso alla Repository, lanciate su un thread separato attraverso le Coroutine
    fun insert(credential: Credential) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(credential)
    }

    fun update(credential: Credential) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(credential)
    }

    fun deleteByKey(key : Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteByKey(key)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }
}
