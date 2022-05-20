package it.unipd.dei.esp2122.passwordmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val preferences = getSharedPreferences("SP1", MODE_PRIVATE)
        val editor = preferences.edit()

        editor.putString("Master_Password", "pippo")
        editor.apply()


    }
}