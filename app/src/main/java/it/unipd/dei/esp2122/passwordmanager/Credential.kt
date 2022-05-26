package it.unipd.dei.esp2122.passwordmanager

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credentials_table")
data class Credential (

    @PrimaryKey(autoGenerate = true) @ColumnInfo(name="id")
    val id : Int = 0,

    @ColumnInfo(name="domain")
    val domain : String,

    @ColumnInfo(name="username")
    val username : String?,

    @ColumnInfo(name="password")
    val password : String
)