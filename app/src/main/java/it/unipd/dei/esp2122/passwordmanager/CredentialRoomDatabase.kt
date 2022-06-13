package it.unipd.dei.esp2122.passwordmanager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Credential::class], version = 1)
abstract class CredentialRoomDatabase : RoomDatabase() {
    abstract fun credentialDao() : CredentialDao

    companion object {
        @Volatile
        private var INSTANCE: CredentialRoomDatabase? = null

        fun getDatabase(context: Context): CredentialRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CredentialRoomDatabase::class.java,
                    "credentials_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}