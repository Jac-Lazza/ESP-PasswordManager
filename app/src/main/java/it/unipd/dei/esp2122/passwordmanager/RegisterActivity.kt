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

class RegisterActivity : AppCompatActivity() {

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
        val passwordController = PasswordController(preferences)
        passwordController.init()

        etInsertPwd.setOnFocusChangeListener { view, b ->
            val insertPwd = (view as TextInputEditText).text.toString()
            if(!b){
                val strength = passwordController.strength(insertPwd)
                progressBar.progress = strength
            }
        }

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val insertPwd = etInsertPwd.text.toString()
            val confirmPwd = etConfirmPwd.text.toString()
            val strength = passwordController.strength(insertPwd)

            if ((strength > PasswordController.PASSWORD_WEAK) && (insertPwd == confirmPwd)){
                val editor = preferences.edit()
                editor.putString(getString(R.string.KEY_NAME), name)
                val digestPwd = passwordController.hash(insertPwd)
                editor.putString(getString(R.string.KEY_MASTER_PASSWORD), digestPwd)
                editor.apply()

                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)

                /* Intent for autofill service */
                val autofillServiceIntent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE)
                autofillServiceIntent.data = Uri.parse("package:${packageName}")
                startActivity(autofillServiceIntent)
                /* Stop */

                finish()
            }
            else{
                if(strength <= PasswordController.PASSWORD_WEAK)
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