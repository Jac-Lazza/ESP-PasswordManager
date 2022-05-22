package it.unipd.dei.esp2122.passwordmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {

    private lateinit var etName : EditText
    private lateinit var etInsertPwd : EditText
    private lateinit var etConfirmPwd : EditText
    private lateinit var btnRegister : Button 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.et_name)
        etInsertPwd = findViewById(R.id.et_insert_pwd)
        etConfirmPwd = findViewById(R.id.et_confirm_pwd)
        btnRegister = findViewById(R.id.btn_register)

        if(savedInstanceState != null){
            val name = savedInstanceState.getString(getString(R.string.KEY_NAME_INSTSTATE))
            val insertPwd = savedInstanceState.getString(getString(R.string.KEY_INSERT_INSTSTATE))
            val confirmPwd = savedInstanceState.getString(getString(R.string.KEY_CONFIRM_INSTSTATE))
            if (name != null)
                etName.setText(name)
            if (insertPwd != null)
                etInsertPwd.setText(insertPwd)
            if (confirmPwd != null)
                etConfirmPwd.setText(confirmPwd)
        }

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val insertPwd = etInsertPwd.text.toString()
            val confirmPwd = etConfirmPwd.text.toString()
            //Fare funzione ausiliaria per gestire il controllo delle password
            if (insertPwd == confirmPwd){
                val preferences = getSharedPreferences(packageName, MODE_PRIVATE)
                val editor = preferences.edit()
                editor.putString(getString(R.string.KEY_NAME), name)
                editor.putString(getString(R.string.KEY_MASTER_PASSWORD), insertPwd)
                editor.apply()
                Toast.makeText(applicationContext, "Password Coincidenti", Toast.LENGTH_LONG).show()
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(applicationContext, "Password Non Coincidenti, RIPROVA", Toast.LENGTH_LONG).show()
            }

        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(getString(R.string.KEY_NAME_INSTSTATE),etName.text.toString())
        outState.putString(getString(R.string.KEY_INSERT_INSTSTATE),etInsertPwd.text.toString())
        outState.putString(getString(R.string.KEY_CONFIRM_INSTSTATE),etConfirmPwd.text.toString())
        super.onSaveInstanceState(outState)

    }


}