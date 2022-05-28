package it.unipd.dei.esp2122.passwordmanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController

class NewCredentialFragment : Fragment() {

    private lateinit var etDomain : EditText
    private lateinit var etUsername : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnAddCredential : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_new_credential, container, false)

        etDomain = view.findViewById(R.id.et_new_domain)
        etUsername = view.findViewById(R.id.et_new_username)
        etPassword = view.findViewById(R.id.et_new_password)
        btnAddCredential = view.findViewById(R.id.btn_add_credential)

        val credentialViewModel = ViewModelProvider(this)[CredentialViewModel::class.java]
        
        btnAddCredential.setOnClickListener {
            if (etDomain.text.toString().isBlank() || etDomain.text.toString().isEmpty())
                Toast.makeText(view.context, "Domain non valido", Toast.LENGTH_LONG).show()
            else if (etUsername.text.toString().isBlank() || etUsername.text.toString().isEmpty())
                Toast.makeText(view.context, "Username non valido", Toast.LENGTH_LONG).show()
            else if (etPassword.text.toString().isBlank() || etPassword.text.toString().isEmpty())
                Toast.makeText(view.context, "Password non valida", Toast.LENGTH_LONG).show()
            else {
                credentialViewModel.insert(
                    Credential(
                        domain = etDomain.text.toString(),
                        username = etUsername.text.toString(),
                        password = etPassword.text.toString()
                    )
                )
                Toast.makeText(view.context, "Password aggiunta", Toast.LENGTH_LONG).show()
                view.findNavController().navigate(R.id.action_newCredentialFragment_to_listFragment)
            }
        }

        return view

    }
}