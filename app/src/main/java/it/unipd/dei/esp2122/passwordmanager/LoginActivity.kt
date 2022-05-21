package it.unipd.dei.esp2122.passwordmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

    private lateinit var tvHello : TextView
    private lateinit var etLoginPwd : EditText
    private lateinit var btnLogin : Button

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

        tvHello = findViewById(R.id.tv_hello)
        etLoginPwd = findViewById(R.id.et_login_pwd)
        btnLogin = findViewById(R.id.btn_login)

        if(savedInstanceState != null){
            val loginPwd = savedInstanceState.getString(getString(R.string.KEY_PWD_INSTSTATE))
            if (loginPwd != null)
                etLoginPwd.setText(loginPwd)
        }

        val name = preferences.getString(getString(R.string.KEY_NAME), null)
        tvHello.text = getString(R.string.hello, name)

        btnLogin.setOnClickListener {
            val loginPwd = etLoginPwd.text.toString()
            if (loginPwd == masterPassword)
                Toast.makeText(applicationContext, "Password Corretta", Toast.LENGTH_LONG).show()
            else
                Toast.makeText(applicationContext, "Password Errata", Toast.LENGTH_LONG).show()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(getString(R.string.KEY_PWD_INSTSTATE),etLoginPwd.text.toString())
        super.onSaveInstanceState(outState)

    }
}