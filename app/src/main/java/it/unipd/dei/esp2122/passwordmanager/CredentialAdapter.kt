package it.unipd.dei.esp2122.passwordmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.findNavController

class CredentialAdapter() :
    RecyclerView.Adapter<CredentialAdapter.CredentialViewHolder>() {

    private var credentials = emptyList<Credential>() // Cached copy of words

    private val credentialOnClickListener = View.OnClickListener{ view ->
        val password = view.findViewById<TextView>(R.id.password_text).text.toString()
        val action = ListFragmentDirections.actionListFragmentToDetailFragment(password)
        view.findNavController().navigate(action)
    }

    // Describes an item view and its place within the RecyclerView
    class CredentialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val passwordTextView: TextView = itemView.findViewById(R.id.password_text)

        fun bind(word: String) {
            passwordTextView.text = word
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CredentialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.password_item, parent, false)

        view.setOnClickListener(credentialOnClickListener)

        return CredentialViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return credentials.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: CredentialViewHolder, position: Int) {
        holder.bind(credentials[position].domain)
    }

    internal fun setCredentials(credentials: List<Credential>) {
        this.credentials = credentials
        notifyDataSetChanged()
    }
}