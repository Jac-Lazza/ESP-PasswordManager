package it.unipd.dei.esp2122.passwordmanager

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ProgressBar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/*
La RegisterActivity viene lanciata al primo avvio dell'applicazione, quando è necessario inserire la master password per l'accesso
all'applicazione. Va inserita e confermata obbligatoriamente una password mediamente robusta (la robustezza della password è
evidenziata da una progress bar), viene richiesto in modo facoltativo il nome da mostrare poi in fase di login.
La master password viene salvata, anzi, per maggiore sicurezza, il relativo hash viene salvato nelle SharedPreferences (stato
persistente) ottenute in modalità privata, in questo modo sono visibili solo internamente all'applicazione.
Inoltre, visto che si tratta del primo accesso, prima di entrare viene richiesto di abilitare il servizio di autofill per questa
app, se non viene concesso, l'utente può decidere di attivarlo in un secondo momento.
*/
class RegisterActivity : AppCompatActivity() {
    companion object{
        //Chiavi per i dati conservati nelle SharedPreferences
        const val KEY_MASTER_PASSWORD = "master_password"
        const val KEY_NAME = "NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName: TextInputEditText = findViewById(R.id.et_insert_name)
        val tilInsertPwd: TextInputLayout = findViewById(R.id.til_insert_pwd)
        val etInsertPwd: TextInputEditText = findViewById(R.id.et_insert_pwd)
        val tilConfirmPwd: TextInputLayout = findViewById(R.id.til_confirm_pwd)
        val etConfirmPwd: TextInputEditText = findViewById(R.id.et_confirm_pwd)
        val btnRegister: Button = findViewById(R.id.btn_register)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)

        val preferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val passwordController = PasswordController(preferences)    //serve per calcolare la robustezza e la forza della password
        passwordController.init()

        etInsertPwd.setOnFocusChangeListener { view, b ->
            val insertPwd = (view as TextInputEditText).text.toString()
            if(!b){
                /*
                Quando si esce dal focus della TextInputEditText per l'inserimento della password, si imposta il valore della
                progress bar in base alla robustezza della password (nulla, bassa, media, alta)
                */
                val strength = passwordController.strength(insertPwd)
                progressBar.progress = strength
            }
        }

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val insertPwd = etInsertPwd.text.toString()
            val confirmPwd = etConfirmPwd.text.toString()
            val strength = passwordController.strength(insertPwd)

            /*
            Si controlla che la password inserita non sia debole e che sia coincidente con la password di conferma.
            Se una di queste due condizioni non capita, non si accetta la master password, altrimenti si procede calcolando l'hash
            della master password e salvandolo nelle SharedPreferences, ottenute in modalità privata.
            */
            if ((strength > PasswordController.PASSWORD_WEAK) && (insertPwd == confirmPwd)){
                val editor = preferences.edit()
                editor.putString(KEY_NAME, name)
                val digestPwd = passwordController.hash(insertPwd)
                editor.putString(KEY_MASTER_PASSWORD, digestPwd)
                editor.apply()

                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)

                //Intent per richiedere l'abilitazione dell'autofill service
                val autofillServiceIntent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE)
                autofillServiceIntent.data = Uri.parse("package:${packageName}")
                startActivity(autofillServiceIntent)
                // Stop

                finish()
            }
            else{
                when {
                    insertPwd.isEmpty() -> tilInsertPwd.error = getString(R.string.empty_pwd)
                    strength == PasswordController.PASSWORD_WEAK -> tilInsertPwd.error = getString(R.string.weak_pwd)
                    else -> tilInsertPwd.error = null
                }

                if(insertPwd != confirmPwd)
                    tilConfirmPwd.error = getString(R.string.pwd_do_not_match)
                else
                    tilConfirmPwd.error = null
            }
        }
    }

}