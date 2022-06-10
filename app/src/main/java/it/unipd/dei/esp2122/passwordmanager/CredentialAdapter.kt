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


class CredentialAdapter(private val passwordController: PasswordController, private val packageManager: PackageManager) :
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
                    if(cred.name.lowercase().contains(stringToFilter))
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
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        private val ivIcon: ImageView = itemView.findViewById(R.id.app_icon)

        fun bind(credential: Credential) {
            val icon = getIcon(credential.domain)
            if(icon != null)
                ivIcon.setImageDrawable(icon)
            else
                ivIcon.setImageResource(R.drawable.application)

            tvName.text = credential.name

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