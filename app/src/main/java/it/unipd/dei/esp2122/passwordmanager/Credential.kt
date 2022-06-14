package it.unipd.dei.esp2122.passwordmanager

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/*
Entità Credential che mappa la tabella con gli attributi id (chiave primaria), nome, dominio, nome utente e password.
C'è un vincolo di unicità nella combinazione del dominio e del nome utente, quindi non possono esistere due istanze
della entità Credential (= due righe della tabella) con lo stesso dominio e nome utente.
*/
@Entity(tableName = "credentials_table", indices = [Index(value = ["domain", "username"], unique = true)])
data class Credential (

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name="id")
    val id : Int = 0,

    @ColumnInfo(name="name")
    val name : String,

    @ColumnInfo(name="domain")
    val domain : String,

    @ColumnInfo(name="username")
    val username : String,

    @ColumnInfo(name="password")
    val password : String
)