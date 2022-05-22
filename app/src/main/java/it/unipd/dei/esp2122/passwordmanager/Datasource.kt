package it.unipd.dei.esp2122.passwordmanager

import android.content.Context

class Datasource(private val context: Context) {
    fun getPasswordList(): Array<String> {

        return context.resources.getStringArray(R.array.password_array)
    }
}