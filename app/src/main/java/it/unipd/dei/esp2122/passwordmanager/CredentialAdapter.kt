package it.unipd.dei.esp2122.passwordmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.findNavController

class CredentialAdapter(private val passwordController: PasswordController) :
    RecyclerView.Adapter<CredentialAdapter.CredentialViewHolder>() {
    private var credentials = emptyList<Credential>()

    inner class CredentialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDomain: TextView = itemView.findViewById(R.id.tv_domain)
        private val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        private val tvPassword: TextView = itemView.findViewById(R.id.tv_password)

        fun bind(credential: Credential) {
            val decryptedPwd = passwordController.decrypt(credential.password)
            tvDomain.text = credential.domain
            tvUsername.text = credential.username
            tvPassword.text = decryptedPwd

            itemView.setOnClickListener { view ->
                val action = ListFragmentDirections.actionListFragmentToDetailFragment(credential.id, credential.domain, credential.username, decryptedPwd)
                view.findNavController().navigate(action)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CredentialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.credential_item, parent, false)

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
        notifyDataSetChanged()
    }
}