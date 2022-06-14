package it.unipd.dei.esp2122.passwordmanager

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CredentialDao {

    @Query("SELECT * FROM credentials_table ORDER BY name COLLATE NOCASE ASC")
    fun getCredentials(): LiveData<List<Credential>>

    /*
    Nell'inserimento di una Credential che viola il vincolo di unicità della combinazione di dominio e username,
    viene mantenuta la Credential già presente, ignorando quella che si sta tentando di aggiungere
    */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(credential: Credential)

    /*
    Nell'aggiornamento di una Credential che porta a violare il vincolo di unicità della combinazione di dominio e
    username, viene sostituita la Credential già presente con quella dell'aggiornamento
    */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(credential: Credential)

    @Query("DELETE FROM credentials_table WHERE id = :key")
    fun deleteByKey(key: Int)

    @Query("DELETE FROM credentials_table")
    fun deleteAll()

    @Query("SELECT * FROM credentials_table WHERE domain = :domain")
    fun searchCredentials(domain: String): List<Credential>

}