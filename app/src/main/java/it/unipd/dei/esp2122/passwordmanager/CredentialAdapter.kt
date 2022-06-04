package it.unipd.dei.esp2122.passwordmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.findNavController

class CredentialAdapter(private val passwordController: PasswordController) :
    RecyclerView.Adapter<CredentialAdapter.CredentialViewHolder>(), Filterable {
    private var credentials = emptyList<Credential>()
    private var allCredentials = emptyList<Credential>()    //for filtering

    private val mFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<Credential>()

            if(constraint == null || constraint.isEmpty())
                filteredList.addAll(allCredentials)
            else{
                val stringToFilter = constraint.toString().trim().lowercase()
                for(cred in allCredentials){
                    if(cred.domain.lowercase().contains(stringToFilter))
                        filteredList.add(cred)
                }
            }

            val results = FilterResults()
            results.values = filteredList

            return results

        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            credentials = results!!.values as List<Credential>
            notifyDataSetChanged()
        }
    }

    inner class CredentialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDomain: TextView = itemView.findViewById(R.id.tv_domain)
        private val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        private val tvPassword: TextView = itemView.findViewById(R.id.tv_password)

        fun bind(credential: Credential) {
            val decryptedPwd = passwordController.decrypt(credential.password)

            tvDomain.text = credential.domain

            if(credential.username.isEmpty()) {
                tvUsername.visibility = View.GONE
            }
            else{
                tvUsername.visibility = View.VISIBLE
                tvUsername.text = credential.username
            }

            tvPassword.text = decryptedPwd

            itemView.setOnClickListener { view ->
                val action = ListFragmentDirections.actionListFragmentToDetailFragment(credential.id, credential.domain, credential.username, decryptedPwd)
                view.findNavController().navigate(action)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CredentialViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.credential_item, parent, false)

        return CredentialViewHolder(view)
    }

    override fun getItemCount(): Int {
        return credentials.size
    }

    override fun onBindViewHolder(holder: CredentialViewHolder, position: Int) {
        holder.bind(credentials[position])
    }

    internal fun setCredentials(credentials: List<Credential>) {
        this.credentials = credentials
        this.allCredentials = credentials   //for filtering
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return mFilter
    }


}