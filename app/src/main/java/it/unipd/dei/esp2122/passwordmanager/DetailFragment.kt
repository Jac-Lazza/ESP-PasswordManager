package it.unipd.dei.esp2122.passwordmanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController

class DetailFragment : Fragment() {

    private lateinit var etDetailDomain : EditText
    private lateinit var etDetailUsername : EditText
    private lateinit var etDetailPassword : EditText
    private lateinit var etRemoveButton : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        val credentialViewModel = ViewModelProvider(this)[CredentialViewModel::class.java]

        etDetailDomain = view.findViewById(R.id.et_detail_domain)
        etDetailUsername = view.findViewById(R.id.et_detail_username)
        etDetailPassword = view.findViewById(R.id.et_detail_password)
        etRemoveButton = view.findViewById(R.id.btn_detail_remove)

        val id = DetailFragmentArgs.fromBundle(requireArguments()).id
        val domain = DetailFragmentArgs.fromBundle(requireArguments()).domain
        val username = DetailFragmentArgs.fromBundle(requireArguments()).username
        val password = DetailFragmentArgs.fromBundle(requireArguments()).password

        etDetailDomain.setText(domain)
        etDetailUsername.setText(username)
        etDetailPassword.setText(password)

        etRemoveButton.setOnClickListener {
            credentialViewModel.deleteByKey(id)
            Toast.makeText(view.context, "Password rimossa", Toast.LENGTH_LONG).show()
            view.findNavController().navigate(R.id.action_detailFragment_to_listFragment)

        }

        return view
    }
}

