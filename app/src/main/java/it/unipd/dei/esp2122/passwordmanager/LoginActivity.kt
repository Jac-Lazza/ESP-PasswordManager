package it.unipd.dei.esp2122.passwordmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences(packageName, MODE_PRIVATE)
        val masterPassword = preferences.getString(getString(R.string.KEY_MASTER_PASSWORD), null)

        if (masterPassword == null){
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        setContentView(R.layout.activity_login)

        val tv_hello : TextView = findViewById(R.id.tv_hello)
        val btn_login : Button = findViewById(R.id.btn_login)
        val et_login_pwd : TextView = findViewById(R.id.et_login_pwd)

        val name = preferences.getString(getString(R.string.KEY_NAME), null)
        tv_hello.text = getString(R.string.hello, name)

        btn_login.setOnClickListener {
            val login_pwd = et_login_pwd.text.toString()
            if (login_pwd == masterPassword)
                Toast.makeText(applicationContext, "Password Corretta", Toast.LENGTH_LONG).show()
            else
                Toast.makeText(applicationContext, "Password Errata", Toast.LENGTH_LONG).show()
        }

    }
}