package it.unipd.dei.esp2122.passwordmanager

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.Toolbar


class ListFragment : Fragment() {

    private lateinit var credentialViewModel : CredentialViewModel

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_items, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_delete_all)
            showDialog(credentialViewModel)
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        credentialViewModel = ViewModelProvider(this)[CredentialViewModel::class.java]

        val toolbar : Toolbar = view.findViewById(R.id.toolbar_list)
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        val preferences = requireActivity().getSharedPreferences(requireActivity().packageName, Context.MODE_PRIVATE)
        val adapter = CredentialAdapter(PasswordController(preferences))
        recyclerView.adapter = adapter

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        credentialViewModel.allCredentials.observe(viewLifecycleOwner, Observer { credential ->
                credential?.let { adapter.setCredentials(it) }
        })

        val fab : FloatingActionButton = view.findViewById(R.id.floatingActionButton2)
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