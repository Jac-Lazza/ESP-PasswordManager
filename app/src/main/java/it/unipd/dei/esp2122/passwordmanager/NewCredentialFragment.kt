package it.unipd.dei.esp2122.passwordmanager

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/*
NewCredentialFragment permette di aggiungere una nuova credenziale specificando il dominio, il nome utente (non obbligatorio) e la
password. Se il dominio della credenziale inserita coincide con il nome di un'applicazione installata nel dispositivo, viene collegata
al relativo package nel database.
*/
class NewCredentialFragment : Fragment() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Impostazione della freccia per tornare indietro nel back stack
        if(item.itemId == android.R.id.home)
            requireActivity().onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_new_credential, container, false)
        val activity = requireActivity()
        val toolbar : Toolbar = view.findViewById(R.id.toolbar_new_credential)

        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)     //Impostazione della toolbar
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)     //Viene mostrata l'icona della freccia per tornare indietro
        setHasOptionsMenu(true)     //Il fragment contribuisce alla popolazione dell'options menu

        val tilDomain: TextInputLayout = view.findViewById(R.id.til_new_domain)
        val etDomain: TextInputEditText  = view.findViewById(R.id.et_new_domain)
        val etUsername: TextInputEditText = view.findViewById(R.id.et_new_username)
        val tilPassword: TextInputLayout = view.findViewById(R.id.til_new_password)
        val etPassword: TextInputEditText = view.findViewById(R.id.et_new_password)
        val btnAddCredential: Button = view.findViewById(R.id.btn_add)
        val btnGeneratePwd: Button = view.findViewById(R.id.btn_generate)

        val passwordController = PasswordController(activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE))
        val credentialViewModel = ViewModelProvider(this)[CredentialViewModel::class.java]

        val packageManager = requireContext().packageManager
        val listInfo = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)    //Lista con tutte le ApplicationInfo delle app installate

        btnGeneratePwd.setOnClickListener {
            val generatedPwd = passwordController.generatePassword()    //Generazione di una password random di 16 caratteri
            etPassword.setText(generatedPwd)
        }

        btnAddCredential.setOnClickListener {
            var domain = etDomain.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString()

            var name = domain
            /*
            Se il dominio associato alla credenziale corrisponde al nome di un'applicazione installata sul dispositivo, viene inserito
            come dominio il package di tale applicazione, se non c'è alcuna corrispondenza il dominio coincide con ciò che ha
            inserito l'utente
            */
            for(elem in listInfo){
                val appName = packageManager.getApplicationLabel(elem).toString()
                if(appName.lowercase() == domain.lowercase()) {
                    domain = elem.packageName
                    name = appName
                    break
                }
            }

            if(domain.isNotEmpty() && password.isNotEmpty()) {
                credentialViewModel.insert(
                    Credential(
                        name = name,
                        domain = domain,
                        username = username,
                        password = passwordController.encrypt(password)
                    )
                )
                view.findNavController()
                    .navigate(R.id.action_newCredentialFragment_to_listFragment)
            }
            else{
                if (domain.isEmpty())
                    tilDomain.error = getString(R.string.empty_domain)
                else
                    tilDomain.error = null

                if (password.isEmpty())
                    tilPassword.error = getString(R.string.empty_pwd)
                else
                    tilPassword.error = null
            }

        }

        return view

    }
}