package it.unipd.dei.esp2122.passwordmanager

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import it.unipd.dei.esp2122.passwordmanager.RegisterActivity.Companion.KEY_MASTER_PASSWORD

class ChangeMasterActivity : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_master)
        val toolbar : Toolbar = findViewById(R.id.toolbar_change_master)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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
            val masterHash = preferences.getString(KEY_MASTER_PASSWORD, null) //hash della master passsword memorizzata
            val insertMasterHash = passwordController.hash(oldMaster) //hash della master passsword inserita dall'utente

            if((masterHash == insertMasterHash) && (newMaster == confirmMaster) && (passwordController.strength(newMaster) > PasswordController.PASSWORD_WEAK)){
                tilNewMaster.error = null
                val newHash = passwordController.hash(newMaster) //hash della nuova master password
                val editor = preferences.edit()
                editor.putString(KEY_MASTER_PASSWORD, newHash)
                editor.apply()
                finish()
            }
            else{
                when {
                    newMaster.isEmpty() -> tilNewMaster.error = getString(R.string.empty_pwd)
                    passwordController.strength(newMaster) == PasswordController.PASSWORD_WEAK -> tilNewMaster.error = getString(R.string.weak_pwd)
                    else -> tilNewMaster.error = null
                }
                tilOldMaster.error = if(masterHash == insertMasterHash) null else getString(R.string.wrong_master)
                tilConfirmMaster.error = if(newMaster == confirmMaster) null else getString(R.string.pwd_do_not_match)
            }

        }


    }
}