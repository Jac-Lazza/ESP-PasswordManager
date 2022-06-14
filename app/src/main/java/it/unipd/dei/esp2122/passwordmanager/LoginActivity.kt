package it.unipd.dei.esp2122.passwordmanager

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import it.unipd.dei.esp2122.passwordmanager.RegisterActivity.Companion.KEY_MASTER_PASSWORD
import it.unipd.dei.esp2122.passwordmanager.RegisterActivity.Companion.KEY_NAME

class LoginActivity : AppCompatActivity() {

    private lateinit var tvHello : TextView
    private lateinit var tilLoginPwd : TextInputLayout
    private lateinit var etLoginPwd : TextInputEditText
    private lateinit var btnLogin : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val masterPassword = preferences.getString(KEY_MASTER_PASSWORD, null)

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

        val name = preferences.getString(KEY_NAME, "")
        tvHello.text = getString(R.string.hello, name)

        val passwordController = PasswordController(preferences)

        btnLogin.setOnClickListener {
            val loginPwd = etLoginPwd.text.toString()
            val digestPwd = passwordController.hash(loginPwd)
            if (digestPwd == masterPassword){
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else
                tilLoginPwd.error = getString(R.string.wrong_pwd)
        }

    }
}