package it.unipd.dei.esp2122.passwordmanager

import android.content.Context
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_items, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        //val toolbar : Toolbar = view.findViewById(R.id.toolbar_list)
       // (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        //setHasOptionsMenu(true)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        val preferences = requireActivity().getSharedPreferences(requireActivity().packageName, Context.MODE_PRIVATE)
        val adapter = CredentialAdapter(PasswordController(preferences))
        recyclerView.adapter = adapter

        val credentialViewModel = ViewModelProvider(this)[CredentialViewModel::class.java]

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
}