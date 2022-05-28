package it.unipd.dei.esp2122.passwordmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.findNavController

class CredentialAdapter() :
    RecyclerView.Adapter<CredentialAdapter.CredentialViewHolder>() {

    private var credentials = emptyList<Credential>()
/*
    private val credentialOnClickListener = View.OnClickListener{ view ->
        val password = view.findViewById<TextView>(R.id.tv_domain).text.toString()
        val action = ListFragmentDirections.actionListFragmentToDetailFragment(password)
        view.findNavController().navigate(action)
    }*/

    // Describes an item view and its place within the RecyclerView
    class CredentialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDomain: TextView = itemView.findViewById(R.id.tv_domain)
        private val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        private val tvPassword: TextView = itemView.findViewById(R.id.tv_password)

        fun bind(credential: Credential) {
            tvDomain.text = credential.domain
            tvUsername.text = credential.username
            tvPassword.text = credential.password

            itemView.setOnClickListener { view ->
                val action = ListFragmentDirections.actionListFragmentToDetailFragment(credential.id, credential.domain, credential.username!!, credential.password)
                view.findNavController().navigate(action)

            }
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CredentialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.credential_item, parent, false)

        //view.setOnClickListener(credentialOnClickListener)

        return CredentialViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return credentials.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: CredentialViewHolder, position: Int) {
        holder.bind(credentials[position])
    }

    internal fun setCredentials(credentials: List<Credential>) {
        this.credentials = credentials
        notifyDataSetChanged()
    }
}