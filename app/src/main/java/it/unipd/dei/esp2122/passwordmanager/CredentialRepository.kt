package it.unipd.dei.esp2122.passwordmanager

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class CredentialRepository(private val credentialDao: CredentialDao) {

    val allCredentials: LiveData<List<Credential>> = credentialDao.getCredentials()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(credential: Credential) {
        credentialDao.insert(credential)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(credential: Credential) {
        credentialDao.update(credential)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteByKey(key : Int) {
        credentialDao.deleteByKey(key)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        credentialDao.deleteAll()
    }
}