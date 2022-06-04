package it.unipd.dei.esp2122.passwordmanager

import android.app.Service
import android.app.assist.AssistStructure
import android.content.Intent
import android.os.CancellationSignal
import android.os.IBinder
import android.service.autofill.*
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.autofill.AutofillValue
import android.widget.RemoteViews

class PMAutofillService : AutofillService() {

    /*override fun onBind(intent: Intent): IBinder {
        //In an autofill service this method cannot be overwritten
    }*/

    override fun onFillRequest(request : FillRequest, cancellationSignal : CancellationSignal, callback : FillCallback) {
        val context  : MutableList<FillContext> = request.fillContexts
        val structure : AssistStructure = context[context.size - 1].structure
        val parsedData = ParsedStructure(structure)

        if(parsedData.autofillHintsDetected > 0){
            println("Finally an application that uses autofill Hints!")
            /**
             * Questo necessita di un po' di spiegazioni:
             * Un servizio di autofill dovrebbe implementare un controllo per la ricerca di autofillHints,
             * se li trova dovrebbe interpretarli, altrimenti dovrebbe procedere con un approccio euristico.
             * Nella nostra esperienza, nessuna applicazione di uso comune dichiara anche un solo suggerimento
             * per l'autofill, e dunque siamo costretti ad usare (e implementare) un approccio euristico.
             * In aggiunta, non sapendo nemmeno come siano fatti questi suggerimenti e siccome la documentazione
             * Android al riguardo è leggermente carente, abbiamo deciso di ignorare per questioni di semplicità
             * lo sviluppo di una parte che controlli anche gli autofillHints e di proseguire in ogni caso
             * con un approccio euristico.
             * **/
        }

        if(isLoginForm(parsedData)){ //Proceed to fill the login editTexts
            Log.e("AutofillService", "Login form detected")

            val credentials = getCredentialsForDomain(parsedData.domain)

            if(credentials.count() == 0){
                callback.onSuccess(null)
                return
            }

            val response  = FillResponse.Builder()
            for(cred in credentials){
                val usernamePresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
                usernamePresentation.setTextViewText(android.R.id.text1, cred.username)

                val passwordPresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
                passwordPresentation.setTextViewText(android.R.id.text1, "Password for ${cred.username}")

                val dataset = Dataset.Builder()
                Log.e("DEBUG", "${parsedData.editTextList.elementAt(0).autofillId}")
                Log.e("DEBUG", "${parsedData.editTextList.elementAt(1).autofillId}")
                dataset.setValue(parsedData.editTextList.elementAt(0).autofillId!!, AutofillValue.forText(cred.username), usernamePresentation)
                dataset.setValue(parsedData.editTextList.elementAt(1).autofillId!!, AutofillValue.forText(cred.password), passwordPresentation)

                response.addDataset(dataset.build())
            }

            callback.onSuccess(response.build())
        }
        else if(isRegisterForm(parsedData)){ //Proceed to ask user to save credentials
            callback.onSuccess(null)
            TODO("First the filling, then the saving")
        }
        else{
            callback.onSuccess(null) //For security reason: when heuristics fail, don't fill
        }

    }

    override fun onSaveRequest(request : SaveRequest, callback : SaveCallback) {
        TODO("Not yet implemented")
    }

    /* Private functions for heuristic */
    private fun isLoginForm(data : ParsedStructure) : Boolean {
        val usernameInputTypeMask = InputType.TYPE_CLASS_TEXT or InputType.TYPE_CLASS_NUMBER
        val passwordInputTypeMask =
                InputType.TYPE_TEXT_VARIATION_PASSWORD or
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or
                InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD or
                InputType.TYPE_NUMBER_VARIATION_PASSWORD

        if(data.editTextList.count() == 2){
            val possibleUsernameEditText = data.editTextList.elementAt(0)
            val possiblePasswordEditText = data.editTextList.elementAt(1)

            if(((possibleUsernameEditText.inputType and usernameInputTypeMask) > 0) and ((possiblePasswordEditText.inputType and passwordInputTypeMask) > 0)){
                if((possibleUsernameEditText.visibility == View.VISIBLE) and (possiblePasswordEditText.visibility == View.VISIBLE)){
                    Log.e("AutofillService", "Username autofillId: ${possibleUsernameEditText.autofillId}")
                    Log.e("AutofillService", "Password autofillId: ${possiblePasswordEditText.autofillId}")
                    return true
                }
            }
        }
        return false
    }

    private fun isRegisterForm(data : ParsedStructure) : Boolean{
        return false
    }

    /* Access to the database */
    private fun getCredentialsForDomain(domain : String) : MutableList<Credential>{
        val test : MutableList<Credential> = mutableListOf()
        test.add(Credential("user", "password"))
        test.add(Credential("admin", "toor"))
        return test
    }



    private data class Credential(val username : String, val password : String)
}
