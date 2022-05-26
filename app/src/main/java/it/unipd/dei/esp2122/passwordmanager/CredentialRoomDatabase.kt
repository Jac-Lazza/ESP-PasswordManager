package it.unipd.dei.esp2122.passwordmanager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        /*
        private class CredentialDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onOpen method to populate the database.
             * For this sample, we clear the database every time it is created or opened.
             */
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.wordDao())
                    }
                }
            }
        }


        fun populateDatabase(credentialDao: CredentialDao) {
            var credential = Credential(0, "Instagram", "avjack_", "PIPPO")
            credentialDao.insert(credential)
        }*/
    }

}