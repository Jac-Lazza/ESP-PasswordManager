package it.unipd.dei.esp2122.passwordmanager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class DetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        val passwordName = DetailFragmentArgs.fromBundle(requireArguments()).message
        val tv = view.findViewById<TextView>(R.id.password_name)
        tv.text = passwordName

        return view
    }
}

