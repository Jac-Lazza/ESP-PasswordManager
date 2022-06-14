package it.unipd.dei.esp2122.passwordmanager

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/*
DetailFragment permette di vedere il dettaglio della credenziale, mostrando il nome, l'username e la password. Fornisce la
possibilità di aggiornare il valore di username e password, e di eliminare la credenziale dalla lista.
 */
class DetailFragment : Fragment() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Impostazione della freccia per tornare indietro nel back stack
        if(item.itemId == android.R.id.home)
            requireActivity().onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar_detail)

        val activity = requireActivity()
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)     //Impostazione della toolbar
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)     //Viene mostrata l'icona della freccia per tornare indietro
        setHasOptionsMenu(true)     //Il fragment contribuisce alla popolazione dell'options menu

        val tvName: TextView = view.findViewById(R.id.tv_detail_name)
        val etDetailUsername: TextInputEditText = view.findViewById(R.id.et_username)
        val tilDetailPassword: TextInputLayout = view.findViewById(R.id.til_password)
        val etDetailPassword: TextInputEditText = view.findViewById(R.id.et_password)
        val btnDelete: Button = view.findViewById(R.id.btn_delete)
        val btnUpdate: Button = view.findViewById(R.id.btn_update)
        val btnCopyUser: Button = view.findViewById(R.id.btn_username_copy)
        val btnCopyPwd: Button = view.findViewById(R.id.btn_password_copy)
        val ivBadge: ImageView = view.findViewById(R.id.badge_image)

        val passwordController = PasswordController(activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE))
        val credentialViewModel = ViewModelProvider(this)[CredentialViewModel::class.java]

        val id = DetailFragmentArgs.fromBundle(requireArguments()).id
        val name = DetailFragmentArgs.fromBundle(requireArguments()).name
        val domain = DetailFragmentArgs.fromBundle(requireArguments()).domain
        val username = DetailFragmentArgs.fromBundle(requireArguments()).username
        val password = DetailFragmentArgs.fromBundle(requireArguments()).password

        tvName.text = name
        etDetailUsername.setText(username)
        etDetailPassword.setText(password)

        //Si ottiene da un package esistente l'icona della relativa applicazione e la si imposta, altrimenti rimane quella di default
        try {
            val packageManager = activity.packageManager
            val appInfo = packageManager.getApplicationInfo(domain, PackageManager.GET_META_DATA)
            val icon = packageManager.getApplicationIcon(appInfo)
            ivBadge.setImageDrawable(icon)
        }
        catch (e: Exception){ /*Intentionally left blank*/ }

        //Gestione della copia dell'username nel Clipboard
        btnCopyUser.setOnClickListener {
            val manager: ClipboardManager? = activity.applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val clipData = ClipData.newPlainText("Username",etDetailUsername.text.toString().trim())
            manager!!.setPrimaryClip(clipData)
            Toast.makeText(requireContext(), getString(R.string.copied_clipboard), Toast.LENGTH_LONG).show()

        }

        //Gestione della copia della password nel Clipboard
        btnCopyPwd.setOnClickListener {
            val manager: ClipboardManager? = activity.applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val clipData = ClipData.newPlainText("Password",etDetailPassword.text.toString())
            manager!!.setPrimaryClip(clipData)
            Toast.makeText(requireContext(), getString(R.string.copied_clipboard), Toast.LENGTH_LONG).show()

        }

        btnDelete.setOnClickListener {
            showDialog(view, credentialViewModel, id)
        }

        btnUpdate.setOnClickListener {
            val updatedUsername = etDetailUsername.text.toString().trim()
            val updatedPassword = etDetailPassword.text.toString()

            if(updatedPassword.isNotEmpty()) {
                credentialViewModel.update(
                    Credential(
                        id = id,
                        name = name,
                        domain = domain,
                        username = updatedUsername,
                        password = passwordController.encrypt(updatedPassword)
                    )
                )
                view.findNavController()
                    .navigate(R.id.action_detailFragment_to_listFragment)
            }
            else{
                if (updatedPassword.isEmpty())
                    tilDetailPassword.error = getString(R.string.empty_pwd)
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

