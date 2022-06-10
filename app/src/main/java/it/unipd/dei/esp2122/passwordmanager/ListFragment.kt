package it.unipd.dei.esp2122.passwordmanager

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.Toolbar


class ListFragment : Fragment() {

    private lateinit var credentialViewModel : CredentialViewModel
    private lateinit var adapter : CredentialAdapter

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_items, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
             }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_all -> showDialog(credentialViewModel)
            R.id.autofill -> {
                val autofillServiceIntent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE)
                autofillServiceIntent.data = Uri.parse("package:${requireActivity().packageName}")
                startActivity(autofillServiceIntent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val toolbar : Toolbar = view.findViewById(R.id.toolbar_list)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        val fab : FloatingActionButton = view.findViewById(R.id.add_floating_btn)

        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        credentialViewModel = ViewModelProvider(this)[CredentialViewModel::class.java]

        val preferences = requireActivity().getSharedPreferences(requireActivity().packageName, Context.MODE_PRIVATE)
        adapter = CredentialAdapter(PasswordController(preferences), requireContext().packageManager)
        recyclerView.adapter = adapter

        credentialViewModel.allCredentials.observe(viewLifecycleOwner, Observer { credential ->
                credential?.let { adapter.setCredentials(it) }
        })

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_newCredentialFragment)
        }

        return view
    }

    private fun showDialog(credentialViewModel: CredentialViewModel){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage(R.string.deleteAll_dialog_msg)
            .setPositiveButton(R.string.DELETE,
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                    credentialViewModel.deleteAll()
                })
            .setNegativeButton(R.string.CANCEL,
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                })
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}