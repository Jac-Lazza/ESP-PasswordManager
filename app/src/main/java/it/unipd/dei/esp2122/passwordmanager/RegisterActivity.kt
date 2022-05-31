package it.unipd.dei.esp2122.passwordmanager

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
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
    private lateinit var progressBar : ProgressBar

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
        progressBar = findViewById(R.id.progressBar)

        val preferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val passwordController = PasswordController(preferences)
        passwordController.init()

        etInsertPwd.setOnFocusChangeListener { view, b ->
            val insertPwd = (view as TextInputEditText).text.toString()
            if(!b){
                val strength = passwordController.strength(insertPwd)
                progressBar.progress = strength
                when(passwordController.strength(insertPwd)) {
                    PasswordController.PASSWORD_WEAK -> progressBar.progressTintList = ColorStateList.valueOf(Color.RED)
                    PasswordController.PASSWORD_MEDIUM -> progressBar.progressTintList = ColorStateList.valueOf(Color.YELLOW)
                    PasswordController.PASSWORD_HARD -> progressBar.progressTintList = ColorStateList.valueOf(Color.GREEN)
                }

            }
        }

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

}