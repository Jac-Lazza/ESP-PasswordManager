package it.unipd.dei.esp2122.passwordmanager

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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


class DetailFragment : Fragment() {

    private lateinit var tvName : TextView
    private lateinit var tilDetailUsername : TextInputLayout
    private lateinit var etDetailUsername : TextInputEditText
    private lateinit var tilDetailPassword : TextInputLayout
    private lateinit var etDetailPassword : TextInputEditText
    private lateinit var btnDelete : Button
    private lateinit var btnUpdate : Button
    private lateinit var btnCopyUser : Button
    private lateinit var btnCopyPwd : Button
    private lateinit var ivBadge : ImageView

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home)
            requireActivity().onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        val activity = requireActivity()
        val toolbar : Toolbar = view.findViewById(R.id.toolbar_detail)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

        val passwordController = PasswordController(activity.getSharedPreferences(activity.packageName, Context.MODE_PRIVATE))
        val credentialViewModel = ViewModelProvider(this)[CredentialViewModel::class.java]

        tvName = view.findViewById(R.id.tv_detail_name)
        tilDetailUsername = view.findViewById(R.id.til_username)
        etDetailUsername = view.findViewById(R.id.et_username)
        tilDetailPassword = view.findViewById(R.id.til_password)
        etDetailPassword = view.findViewById(R.id.et_password)
        btnDelete = view.findViewById(R.id.btn_delete)
        btnUpdate = view.findViewById(R.id.btn_update)
        btnCopyUser = view.findViewById(R.id.btn_username_copy)
        btnCopyPwd = view.findViewById(R.id.btn_password_copy)
        ivBadge = view.findViewById(R.id.badge_image)

        val id = DetailFragmentArgs.fromBundle(requireArguments()).id
        val name = DetailFragmentArgs.fromBundle(requireArguments()).name
        val domain = DetailFragmentArgs.fromBundle(requireArguments()).domain
        val username = DetailFragmentArgs.fromBundle(requireArguments()).username
        val password = DetailFragmentArgs.fromBundle(requireArguments()).password

        tvName.text = name
        etDetailUsername.setText(username)
        etDetailPassword.setText(password)

        try {
            val packageManager = activity.packageManager
            val appInfo = packageManager.getApplicationInfo(domain, PackageManager.GET_META_DATA)
            val icon = packageManager.getApplicationIcon(appInfo)
            ivBadge.setImageDrawable(icon)
        }
        catch (e: Exception){ /*Intentionally left blank*/ }

        btnCopyUser.setOnClickListener {
            val manager: ClipboardManager? = activity.applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val clipData = ClipData.newPlainText("Username copiato",etDetailUsername.text.toString().trim())
            manager!!.setPrimaryClip(clipData)
            Toast.makeText(activity.applicationContext, "Copied to the clipboard", Toast.LENGTH_LONG).show()

        }

        btnCopyPwd.setOnClickListener {
            val manager: ClipboardManager? = activity.applicationContext.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val clipData = ClipData.newPlainText("Password copiata",etDetailPassword.text.toString())
            manager!!.setPrimaryClip(clipData)
            Toast.makeText(activity.applicationContext, "Copied to the clipboard", Toast.LENGTH_LONG).show()

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

