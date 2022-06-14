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

/*
LoginActivity è l'Activity che risponde all'avvio dell'applicazione. In primis si occupa di verificare se è già stata
impostata una password principale per l'app (master password): in caso negativo avvia la RegisterActivity per permettere
all'utente creare la master password e termina, in caso positivo viene mostrata l'interfaccia in cui l'utente può inserire la
master password. Se la password inserita è corretta, viene permesso l'accesso alla lista delle credenziali.
*/
class LoginActivity : AppCompatActivity() {

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

        val tvHello: TextView = findViewById(R.id.tv_hello)
        val tilLoginPwd: TextInputLayout = findViewById(R.id.til_login_pwd)
        val etLoginPwd: TextInputEditText = findViewById(R.id.et_login_pwd)
        val btnLogin: Button = findViewById(R.id.btn_login)

        val name = preferences.getString(KEY_NAME, "")
        tvHello.text = getString(R.string.hello, name)

        val passwordController = PasswordController(preferences)    //necessario per l'hash della password

        btnLogin.setOnClickListener {
            val loginPwd = etLoginPwd.text.toString()
            val digestPwd = passwordController.hash(loginPwd)

            //Si verifica che l'hash della password inserita coincida con l'hash della master password
            if (digestPwd == masterPassword){
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else
                tilLoginPwd.error = getString(R.string.wrong_pwd)   //errore mostrato sulla TextInputLayout
        }

    }
}