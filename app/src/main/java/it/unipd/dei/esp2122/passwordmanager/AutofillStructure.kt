package it.unipd.dei.esp2122.passwordmanager

import android.app.assist.AssistStructure
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.autofill.AutofillId
import android.widget.EditText
import android.widget.TextView

class AutofillStructure(private val structure : AssistStructure) {

    private val textViewList : MutableList<AssistStructure.ViewNode> = mutableListOf()
    private val editTextList : MutableList<AssistStructure.ViewNode> = mutableListOf()
    var domain : String = ""
    var autofillHintsDetected = 0

    companion object{
        private const val PS_TAG = "AutofillStructure"

        //Heuristic enumeration
        const val UNKNOWN_FORM = 0
        const val LOGIN_FORM_WITH_DATA = 1
        const val EMPTY_LOGIN_FORM = 2
        const val REGISTER_FORM_WITH_DATA = 3
    }

    init {
        domain = structure.activityComponent.packageName
        if(structure.windowNodeCount > 0) {
            for (index in 0 until structure.windowNodeCount) {
                parseNode(structure.getWindowNodeAt(index).rootViewNode) //In ingresso abbiamo una foresta di ViewNode
            }
        }
    }

    /*
    * Il parsing di un albero di ViewNode viene eseguito tramite Depth First Search. La logica
    * per il filtraggio è la seguente:
    *   1) Vengono considerate le foglie, non i nodi interni
    *   2) Delle foglie, vengono memorizzate EditText e TextView (queste ultime non sono poi usate nell'euristica)
    *   3) Si conta l'utilizzo degli autofillHints (se diverso da zero richiederebbe un parsing dedicato)
    * */
    private fun parseNode(node : AssistStructure.ViewNode){
        if(node.childCount > 0){
            for(index in 0 until node.childCount){
                parseNode(node.getChildAt(index))
            }
        }
        else{
            if(node.className == TextView::class.qualifiedName){
                textViewList.add(node)
            }
            else if(node.className == EditText::class.qualifiedName){
                editTextList.add(node)
            }
            if(node.autofillHints?.isNotEmpty() == true){
                autofillHintsDetected++
            }
        }
    }

    /* Funzioni euristiche */
    fun heuristicClassification() : Int {
        if(isLoginForm()){
            if(isLoginFormWithData()){
                return LOGIN_FORM_WITH_DATA
            }
            else{
                return EMPTY_LOGIN_FORM
            }
        }
        else if(isRegisterForm()){
            return REGISTER_FORM_WITH_DATA
        }
        else{
            return UNKNOWN_FORM
        }
    }

    /*
    * Determina se l'Activity che ha richiesto il servizio di autofill è dedicata al login (username e password).
    * I requisiti per un'activity di login sono i seguenti:
    *   1) Possedere due sole EditText
    *   2) La prima EditText è di classe testo o numero
    *   3) La seconda EditText è di qualsiasi classe, purché mascherata
    *   4) Le due EditText siano visibili all'utente
    * */
    private fun isLoginForm() : Boolean {
        val usernameInputTypeMask = InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_NUMBER
        val passwordInputTypeMask =
            InputType.TYPE_TEXT_VARIATION_PASSWORD or
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or
                    InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD or
                    InputType.TYPE_NUMBER_VARIATION_PASSWORD

        if(editTextList.count() == 2){
            val possibleUsernameEditText = editTextList.elementAt(0)
            val possiblePasswordEditText = editTextList.elementAt(1)

            if(((possibleUsernameEditText.inputType and usernameInputTypeMask) > 0) and ((possiblePasswordEditText.inputType and passwordInputTypeMask) > 0)){
                if((possibleUsernameEditText.visibility == View.VISIBLE) and (possiblePasswordEditText.visibility == View.VISIBLE)){
                    return true
                }
                else{
                    Log.d(PS_TAG, "Username visibility: ${possibleUsernameEditText.inputType == View.VISIBLE}")
                    Log.d(PS_TAG, "Password visibility: ${possiblePasswordEditText.inputType == View.VISIBLE}")
                }
            }
            else{
                Log.d(PS_TAG, "Username mask: ${(possibleUsernameEditText.inputType and usernameInputTypeMask) > 0}")
                Log.d(PS_TAG, "Password mask: ${(possiblePasswordEditText.inputType and passwordInputTypeMask) > 0}")
            }
        }
        else{
            Log.d(PS_TAG, "Number of editText: ${editTextList.count()}")
        }
        return false
    }

    private fun isLoginFormWithData() : Boolean {
        val possibleUsername = editTextList.elementAt(0).text.toString()
        val possiblePassword = editTextList.elementAt(1).text.toString()
        return (possibleUsername != "") or (possiblePassword != "")
    }

    fun getLoginStructure() : AutofillLogin{
        if(!isLoginForm()){
            Log.w(PS_TAG, "Forcing retrieve of a login structure")
        }
        val usernameId = editTextList.elementAt(0).autofillId!!
        val passwordId = editTextList.elementAt(1).autofillId!!
        return AutofillLogin(usernameId, passwordId)
    }

    /*
    * Non implementata
    * */
    fun isRegisterForm() : Boolean{
        return false
    }

    data class AutofillLogin(val usernameId : AutofillId, val passwordId : AutofillId)
}