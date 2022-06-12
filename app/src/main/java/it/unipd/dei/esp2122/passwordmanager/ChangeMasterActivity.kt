package it.unipd.dei.esp2122.passwordmanager

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ChangeMasterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_master)

        val etOldMaster: TextInputEditText = findViewById(R.id.et_old_master)
        val tilOldMaster: TextInputLayout = findViewById(R.id.til_old_master)
        val etNewMaster: TextInputEditText = findViewById(R.id.et_new_master)
        val tilNewMaster: TextInputLayout = findViewById(R.id.til_new_master)
        val etConfirmMaster: TextInputEditText = findViewById(R.id.et_confirm_new_master)
        val tilConfirmMaster: TextInputLayout = findViewById(R.id.til_confirm_new_master)
        val btnChange: Button = findViewById(R.id.btn_change)
        val progressBar: ProgressBar = findViewById(R.id.progressBar_change)

        val preferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val passwordController = PasswordController(preferences)

        etNewMaster.setOnFocusChangeListener { view, b ->
            val newMaster = (view as TextInputEditText).text.toString()
            if(!b){
                val strength = passwordController.strength(newMaster)
                progressBar.progress = strength
            }
        }

        btnChange.setOnClickListener{
            val oldMaster = etOldMaster.text.toString()
            val newMaster = etNewMaster.text.toString()
            val confirmMaster = etConfirmMaster.text.toString()
            val masterHash = preferences.getString("master_password",null)
            val insertMasterHash = passwordController.hash(oldMaster)

            if((masterHash == insertMasterHash) && (newMaster == confirmMaster) && (passwordController.strength(newMaster) > PasswordController.PASSWORD_WEAK)){
                tilNewMaster.error = null
                val newHash = passwordController.hash(newMaster)
                val editor = preferences.edit()
                editor.putString("master_password",newHash)
                editor.apply()
                finish()
            }
            else{
                tilOldMaster.error = if(masterHash == insertMasterHash) null else "Wrong master password"
                tilNewMaster.error = if(passwordController.strength(newMaster) > PasswordController.PASSWORD_WEAK) null else "Weak password"
                tilConfirmMaster.error = if(newMaster == confirmMaster) null else "Passwords do not match"
            }

        }


    }
}