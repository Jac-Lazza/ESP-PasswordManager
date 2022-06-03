package it.unipd.dei.esp2122.passwordmanager

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private lateinit var tvHello : TextView
    private lateinit var tilLoginPwd : TextInputLayout
    private lateinit var etLoginPwd : TextInputEditText
    private lateinit var btnLogin : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val masterPassword = preferences.getString(getString(R.string.KEY_MASTER_PASSWORD), null)

        if (masterPassword == null){
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        setContentView(R.layout.activity_login)

        tvHello = findViewById(R.id.tv_hello)
        tilLoginPwd = findViewById(R.id.til_login_pwd)
        etLoginPwd = findViewById(R.id.et_login_pwd)
        btnLogin = findViewById(R.id.btn_login)

        val name = preferences.getString(getString(R.string.KEY_NAME), null)
        tvHello.text = getString(R.string.hello, name)

        val passwordController = PasswordController(preferences)

        /* Intent for autofill service */
        val autofillServiceIntent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE)
        autofillServiceIntent.data = Uri.parse("package:${packageName}")
        startActivity(autofillServiceIntent)
        /* Stop */

        btnLogin.setOnClickListener {
            val loginPwd = etLoginPwd.text.toString()
            val digestPwd = passwordController.hash(loginPwd)
            if (digestPwd == masterPassword){
                Toast.makeText(applicationContext, "Password corretta", Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else
                tilLoginPwd.error = "Password Errata"
        }

    }
}