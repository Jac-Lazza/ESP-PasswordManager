package it.unipd.dei.esp2122.passwordmanager

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class NewCredentialFragment : Fragment() {

    private lateinit var tilDomain : TextInputLayout
    private lateinit var etDomain : TextInputEditText
    private lateinit var etUsername : TextInputEditText
    private lateinit var tilPassword : TextInputLayout
    private lateinit var etPassword : TextInputEditText
    private lateinit var btnAddCredential : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_new_credential, container, false)

        tilDomain = view.findViewById(R.id.til_new_domain)
        etDomain = view.findViewById(R.id.et_new_domain)
        etUsername = view.findViewById(R.id.et_new_username)
        tilPassword = view.findViewById(R.id.til_new_password)
        etPassword = view.findViewById(R.id.et_new_password)
        btnAddCredential = view.findViewById(R.id.btn_add)

        val activity = requireActivity()
        val passwordController = PasswordController(activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE))
        val credentialViewModel = ViewModelProvider(this)[CredentialViewModel::class.java]

        btnAddCredential.setOnClickListener {
            val domain = etDomain.text.toString().trim()
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if(domain.isNotEmpty() && password.isNotEmpty()) {
                credentialViewModel.insert(
                    Credential(
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
                    tilDomain.error = "Domain non valido"
                else
                    tilDomain.error = null

                if (password.isEmpty())
                    tilPassword.error = "Password non valida"
                else
                    tilPassword.error = null
            }

        }

        return view

    }
}