package it.unipd.dei.esp2122.passwordmanager

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class DetailFragment : Fragment() {

    private lateinit var tilDetailDomain : TextInputLayout
    private lateinit var etDetailDomain : TextInputEditText
    private lateinit var etDetailUsername : TextInputEditText
    private lateinit var tilDetailPassword : TextInputLayout
    private lateinit var etDetailPassword : TextInputEditText
    private lateinit var etDeleteButton : Button
    private lateinit var etUpdateButton : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        val activity = requireActivity()
        val passwordController = PasswordController(activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE))
        val credentialViewModel = ViewModelProvider(this)[CredentialViewModel::class.java]

        tilDetailDomain = view.findViewById(R.id.til_domain)
        etDetailDomain = view.findViewById(R.id.et_domain)
        etDetailUsername = view.findViewById(R.id.et_username)
        tilDetailPassword = view.findViewById(R.id.til_password)
        etDetailPassword = view.findViewById(R.id.et_password)
        etDeleteButton = view.findViewById(R.id.btn_delete)
        etUpdateButton = view.findViewById(R.id.btn_update)

        val id = DetailFragmentArgs.fromBundle(requireArguments()).id
        val domain = DetailFragmentArgs.fromBundle(requireArguments()).domain
        val username = DetailFragmentArgs.fromBundle(requireArguments()).username
        val password = DetailFragmentArgs.fromBundle(requireArguments()).password

        etDetailDomain.setText(domain)
        etDetailUsername.setText(username)
        etDetailPassword.setText(password)

        etDeleteButton.setOnClickListener {
            showDialog(view, credentialViewModel, id)
        }

        etUpdateButton.setOnClickListener {
            val domain = etDetailDomain.text.toString().trim()
            val username = etDetailUsername.text.toString()
            val password = etDetailPassword.text.toString()

            if(domain.isNotEmpty() && password.isNotEmpty()) {
                credentialViewModel.update(
                    Credential(
                        id = id,
                        domain = domain,
                        username = username,
                        password = passwordController.encrypt(password)
                    )
                )
                view.findNavController()
                    .navigate(R.id.action_detailFragment_to_listFragment)
            }
            else{
                if (domain.isEmpty())
                    tilDetailDomain.error = "Domain non valido"
                else
                    tilDetailDomain.error = null

                if (password.isEmpty())
                    tilDetailPassword.error = "Password non valida"
                else
                    tilDetailPassword.error = null
            }
        }

        return view
    }

    private fun showDialog(view: View, credentialViewModel: CredentialViewModel, id: Int){
        val builder : AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage(R.string.delete_dialog_msg)
            .setPositiveButton(R.string.DELETE,
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                    credentialViewModel.deleteByKey(id)
                    Toast.makeText(context, "Password rimossa", Toast.LENGTH_LONG).show()
                    view.findNavController().navigate(R.id.action_detailFragment_to_listFragment)
            })
            .setNegativeButton(R.string.CANCEL,
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                })
        val dialog : AlertDialog = builder.create()
        dialog.show()
    }


}

