package it.unipd.dei.esp2122.passwordmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences("SP1", MODE_PRIVATE)
        val master_password = preferences.getString("Master_Password", null)
        Log.d("Tag", master_password.toString())
        if (master_password == null){
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContentView(R.layout.activity_login)

    }
}