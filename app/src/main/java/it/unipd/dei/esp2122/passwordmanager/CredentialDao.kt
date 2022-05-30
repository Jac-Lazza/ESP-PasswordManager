package it.unipd.dei.esp2122.passwordmanager

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CredentialDao {

    @Query("SELECT * FROM credentials_table ORDER BY domain ASC")
    fun getCredentials(): LiveData<List<Credential>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(credential: Credential)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(credential: Credential)

    @Query("DELETE FROM credentials_table WHERE id = :key")
    fun deleteByKey(key: Int)

    @Query("DELETE FROM credentials_table")
    fun deleteAll()

}