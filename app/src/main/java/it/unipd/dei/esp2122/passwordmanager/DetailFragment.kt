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

class DetailFragment : Fragment() {

    private lateinit var etDetailDomain : EditText
    private lateinit var etDetailUsername : EditText
    private lateinit var etDetailPassword : EditText
    private lateinit var etDeleteButton : Button
    private lateinit var etUpdateButton : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        val credentialViewModel = ViewModelProvider(this)[CredentialViewModel::class.java]

        etDetailDomain = view.findViewById(R.id.et_detail_domain)
        etDetailUsername = view.findViewById(R.id.et_detail_username)
        etDetailPassword = view.findViewById(R.id.et_detail_password)
        etDeleteButton = view.findViewById(R.id.btn_detail_delete)
        etUpdateButton = view.findViewById(R.id.btn_detail_update)

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
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

            val modifiedDomain = etDetailDomain.text.toString()
            val modifiedUsername = etDetailUsername.text.toString()
            val modifiedPassword = etDetailPassword.text.toString()
            when {
                modifiedDomain.trim().isEmpty() -> Toast.makeText(view.context, "Domain non valido", Toast.LENGTH_LONG).show()
                modifiedPassword.isEmpty() -> Toast.makeText(view.context, "Password non valida", Toast.LENGTH_LONG).show()
                else -> {
                    credentialViewModel.update(
                        Credential(
                            id = id,
                            domain = modifiedDomain,
                            username = modifiedUsername,
                            password = modifiedPassword
                        )
                    )
                }
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

