package it.unipd.dei.esp2122.passwordmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val et_name : EditText = findViewById(R.id.et_name)
        val et_insert_pwd : EditText = findViewById(R.id.et_insert_pwd)
        val et_confirm_pwd : EditText = findViewById(R.id.et_confirm_pwd)
        val btn_register : Button = findViewById(R.id.btn_register)

        btn_register.setOnClickListener {
            val name = et_name.text.toString()
            val insert_pwd = et_insert_pwd.text.toString()
            val confirm_pwd = et_confirm_pwd.text.toString()
            //Fare funzione ausiliaria per gestire il controllo delle password
            if (insert_pwd == confirm_pwd){
                val preferences = getSharedPreferences(packageName, MODE_PRIVATE)
                val editor = preferences.edit()
                editor.putString(getString(R.string.KEY_NAME), name)
                editor.putString(getString(R.string.KEY_MASTER_PASSWORD), insert_pwd)
                editor.apply()
                Toast.makeText(applicationContext, "Password Coincidenti", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(applicationContext, "Password Non Coincidenti, RIPROVA", Toast.LENGTH_LONG).show()
            }

        }


    }
}