package it.unipd.dei.esp2122.passwordmanager

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.view.autofill.AutofillManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getSystemService

/*
Fragment che ospita la Recycler View con la lista delle credenziali, viene permesso di aggiungere nuove credenziali premendo sul
floating button. Inoltre è presente una toolbar che permette, attraverso gli elementi dell'options menu, di compiere alcune azioni:
la ricerca delle credenziali basata sul nome, la rimozione di tutte le credenziali, l'abilitazione dell'autofill service e il
cambio della master password.
*/
class ListFragment : Fragment() {

    private lateinit var credentialViewModel : CredentialViewModel
    private lateinit var adapter : CredentialAdapter

    //Metodo con cui il fragment popola l'options menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_main, menu)
        val searchItem = menu.findItem(R.id.action_search)

        //Impostazione della SearchView
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{   //Implementazione del listener per gestire le azioni sulla SearchView
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            //Metodo per cercare il testo inserito, appena viene cambiato
            override fun onQueryTextChange(query: String?): Boolean {
                adapter.filter.filter(query)    //Viene avviata un'operazione di filtraggio
                return false
             }

        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    //Metodo per gestire la selezione degli elementi nell'options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_all -> showDialog(credentialViewModel)
            R.id.autofill -> {
                val am = getSystemService(requireContext(), AutofillManager::class.java)
                if(am!!.hasEnabledAutofillServices()){  //Verifica se l'autofill service è già stato abilitato
                    Toast.makeText(requireContext(), getString(R.string.enabled_autofill), Toast.LENGTH_LONG).show()
                }
                else {  //Richiesta di attivazione dell'autofill service
                    val autofillServiceIntent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE)
                    autofillServiceIntent.data =
                        Uri.parse("package:${requireActivity().packageName}")
                    startActivity(autofillServiceIntent)
                }
            }
            R.id.change_master -> {
                val changeMasterIntent = Intent(context, ChangeMasterActivity::class.java)
                startActivity(changeMasterIntent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        val toolbar : Toolbar = view.findViewById(R.id.toolbar_list)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        val fab : FloatingActionButton = view.findViewById(R.id.add_floating_btn)

        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar) //Impostazione della toolbar
        setHasOptionsMenu(true) //Il fragment contribuisce alla popolazione dell'options menu

        credentialViewModel = ViewModelProvider(this)[CredentialViewModel::class.java]

        val preferences = requireActivity().getSharedPreferences(requireActivity().packageName, Context.MODE_PRIVATE)
        adapter = CredentialAdapter(PasswordController(preferences), requireContext().packageManager)
        recyclerView.adapter = adapter

        /*
        La View del Fragment ha il ruolo di osservare il ViewModel (design pattern Observer), in particolare la lista di credenziali
        nel LiveData, per ottenere i dati con cui aggiornare la RecyclerView. Design pattern ModelView-ViewModel.
        */
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