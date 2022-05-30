package it.unipd.dei.esp2122.passwordmanager

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {

    private lateinit var tilName : TextInputLayout
    private lateinit var etName : TextInputEditText
    private lateinit var tilInsertPwd : TextInputLayout
    private lateinit var etInsertPwd : TextInputEditText
    private lateinit var tilConfirmPwd : TextInputLayout
    private lateinit var etConfirmPwd : TextInputEditText
    private lateinit var btnRegister : Button 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        tilName = findViewById(R.id.til_insert_name)
        etName = findViewById(R.id.et_insert_name)
        tilInsertPwd = findViewById(R.id.til_insert_pwd)
        etInsertPwd = findViewById(R.id.et_insert_pwd)
        tilConfirmPwd = findViewById(R.id.til_confirm_pwd)
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

        val preferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val passwordController = PasswordController(preferences)
        passwordController.init()

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val insertPwd = etInsertPwd.text.toString()
            val confirmPwd = etConfirmPwd.text.toString()
            val strength = passwordController.strength(insertPwd)

            if ((strength != PasswordController.PASSWORD_WEAK) && (insertPwd == confirmPwd)){
                val editor = preferences.edit()
                editor.putString(getString(R.string.KEY_NAME), name)
                val digestPwd = passwordController.hash(insertPwd)
                editor.putString(getString(R.string.KEY_MASTER_PASSWORD), digestPwd)
                editor.apply()

                Toast.makeText(applicationContext, "Password Coincidenti", Toast.LENGTH_LONG).show()

                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                if(strength == PasswordController.PASSWORD_WEAK)
                    tilInsertPwd.error = "Password debole!"
                else
                    tilInsertPwd.error = null

                if(insertPwd != confirmPwd)
                    tilConfirmPwd.error = "Password non coincidenti"
                else
                    tilConfirmPwd.error = null
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