package it.unipd.dei.esp2122.passwordmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.findNavController

class PasswordAdapter(private val passwordList: Array<String>) :
    RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder>() {

    private val passwordOnClickListener = View.OnClickListener{ view ->
        val password = view.findViewById<TextView>(R.id.password_text).text.toString()
        val action = ListFragmentDirections.actionListFragmentToDetailFragment(password)
        view.findNavController().navigate(action)
    }

    // Describes an item view and its place within the RecyclerView
    class PasswordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val passwordTextView: TextView = itemView.findViewById(R.id.password_text)

        fun bind(word: String) {
            passwordTextView.text = word
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.password_item, parent, false)

        view.setOnClickListener(passwordOnClickListener)

        return PasswordViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return passwordList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        holder.bind(passwordList[position])
    }
}