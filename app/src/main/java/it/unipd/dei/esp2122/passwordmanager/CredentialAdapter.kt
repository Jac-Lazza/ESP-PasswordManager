package it.unipd.dei.esp2122.passwordmanager

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.findNavController
import java.lang.Exception

/*
Classe custom derivata da Adapter, riceve le Credential attraverso il metodo setCredentials() e li mette in una forma visualizzabile
attraverso il CredentialViewHolder
*/
class CredentialAdapter(private val passwordController: PasswordController, private val packageManager: PackageManager) :
    RecyclerView.Adapter<CredentialAdapter.CredentialViewHolder>(), Filterable {
    private var credentials = emptyList<Credential>()     //reference alla lista di Credential della RecyclerView
    private var allCredentials = emptyList<Credential>()    //reference d'appoggio alla lista di Credential per il filtraggio

    //Oggetto Filter per compiere il filtraggio sulle Credential e mostrare i risultati nella RecyclerView
    private val mFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<Credential>()

            if(constraint == null || constraint.isEmpty())
                filteredList.addAll(allCredentials)
            else{
                val stringToFilter = constraint.toString().trim().lowercase()
                for(cred in allCredentials){
                    if(cred.name.lowercase().contains(stringToFilter))
                        filteredList.add(cred)
                }
            }

            //Salvataggio della lista con i dati filtrati nel FilterResults
            val results = FilterResults()
            results.values = filteredList

            return results

        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            credentials = results!!.values as List<Credential>
            notifyDataSetChanged()
        }
    }
    /*
    Classe custom derivata da ViewHolder, rappresenta il singolo elemento della RecyclerView
    */
    inner class CredentialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        private val ivIcon: ImageView = itemView.findViewById(R.id.app_icon)

        fun bind(credential: Credential) {
            val icon = getIcon(credential.domain)
            /*
            L'icona viene impostata solo per le Credential che hanno per domain un package esistente, altrimenti viene impostata
            l'icona di default
            */
            if(icon != null)
                ivIcon.setImageDrawable(icon)
            else
                ivIcon.setImageResource(R.drawable.application)

            tvName.text = credential.name

            /*
            Se l'username è vuoto, la textView viene resa invisibile e non occupa alcuno spazio nel layout, altrimenti viene resa
            visibile e conterrà l'username
            */
            if(credential.username.isEmpty()) {
                tvUsername.visibility = View.GONE
            }
            else{
                tvUsername.visibility = View.VISIBLE
                tvUsername.text = credential.username
            }

            val decryptedPwd = passwordController.decrypt(credential.password)

            itemView.setOnClickListener { view ->
                val action = ListFragmentDirections.actionListFragmentToDetailFragment(credential.id, credential.name, credential.domain, credential.username, decryptedPwd)
                view.findNavController().navigate(action)

            }
        }
    }

    //Metodo per creare e inizializzare il CredentialViewHolder e la View associata
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CredentialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.credential_item, parent, false)

        return CredentialViewHolder(view)
    }

    //Metodo per ottenere la dimensione dei dati disponibili
    override fun getItemCount(): Int {
        return credentials.size
    }

    //Metodo per associare ciascun CredentialViewHolder con i relativi dati e il relativo onClickListener
    override fun onBindViewHolder(holder: CredentialViewHolder, position: Int) {
        holder.bind(credentials[position])
    }

    //Metodo per impostare la lista di Credential nella RecyclerView
    internal fun setCredentials(credentials: List<Credential>) {
        this.credentials = credentials
        this.allCredentials = credentials
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return mFilter
    }

    //Metodo per ottenere dal package l'icona dell'applicazione
    private fun getIcon(domain: String): Drawable?{
        var icon: Drawable? = null
        try {
            val appInfo = packageManager.getApplicationInfo(domain, PackageManager.GET_META_DATA)
            icon = packageManager.getApplicationIcon(appInfo)
        }catch (e: Exception){
            Log.d(CredentialAdapter::class.java.simpleName, "Package not found")
        }
        return icon
    }


}