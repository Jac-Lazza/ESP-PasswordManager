package it.unipd.dei.esp2122.passwordmanager

import android.content.Context
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

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