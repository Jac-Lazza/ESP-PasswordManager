package it.unipd.dei.esp2122.passwordmanager

import android.app.Service
import android.app.assist.AssistStructure
import android.content.Context
import android.content.Intent
import android.os.CancellationSignal
import android.os.IBinder
import android.service.autofill.*
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PMAutofillService : AutofillService() {

    private val serviceScope = CoroutineScope(EmptyCoroutineContext)

    private companion object{
        const val AS_TAG = "AutofillService" //Used for debugging
    }

    /*override fun onBind(intent: Intent): IBinder {
        //In an autofill service this method cannot be overwritten
    }*/

    override fun onFillRequest(request : FillRequest, cancellationSignal : CancellationSignal, callback : FillCallback) {
        val context  : MutableList<FillContext> = request.fillContexts
        val structure : AssistStructure = context[context.size - 1].structure
        val parsedData = AutofillStructure(structure)

        /* Test code for obtaining more informations */
        try{
            val packMan = applicationContext.packageManager
            val appInfo = packMan.getApplicationInfo(parsedData.domain, 0) //Don't know what the zero stands for
            val appName = packMan.getApplicationLabel(appInfo)
            val appIcon = packMan.getApplicationIcon(appInfo)
            Log.e(AS_TAG, "Got appName: ${appName} and a drawable as an application icon :-)")
        }
        catch(e : Exception){
            Log.e(AS_TAG, "Wrong domain or application not installed")
        }
        /**/

        if(parsedData.autofillHintsDetected > 0){
            println("Finally an application that uses autofill hints!")
            /**
             * Questo necessita di un po' di spiegazioni:
             * Un servizio di autofill dovrebbe implementare un controllo per la ricerca di autofillHints,
             * se li trova dovrebbe interpretarli, altrimenti dovrebbe procedere con un approccio euristico.
             * Nella nostra esperienza, nessuna applicazione di uso comune dichiara anche un solo suggerimento
             * per l'autofill, e dunque siamo costretti ad usare (e implementare) un approccio euristico.
             * Dunque, per questioni di semplicità abbiamo deciso di implementare solo la parte euristica
             * e di eseguirla anche in caso venissero trovati degli autofillHints
             * **/
        }

        val heuristicClassification = parsedData.heuristicClassification()

        if(heuristicClassification == AutofillStructure.EMPTY_LOGIN_FORM){ //Proceed to fill the login editTexts
            Log.d(AS_TAG, "Login form detected")
            val loginStructure = parsedData.getLoginStructure()
            serviceScope.launch(){
                val credentials = queryCredentialsForDomain(parsedData.domain)
                Log.d(AS_TAG, "Got domain: ${parsedData.domain}")
                if(credentials.count() > 0){
                    val response  = FillResponse.Builder()
                    for(cred in credentials){
                        val usernamePresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
                        usernamePresentation.setTextViewText(android.R.id.text1, cred.username)

                        val passwordPresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
                        passwordPresentation.setTextViewText(android.R.id.text1, "Password for ${cred.username}")

                        val dataset = Dataset.Builder()
                        dataset.setValue(loginStructure.usernameId, AutofillValue.forText(cred.username), usernamePresentation)
                        dataset.setValue(loginStructure.passwordId, AutofillValue.forText(cred.password), passwordPresentation)

                        response.addDataset(dataset.build())
                    }

                    callback.onSuccess(response.build())
                }
                else{
                    Log.d(AS_TAG, "No suitable credentials found in the database")
                    callback.onSuccess(null)
                }
            }
            return
        }
        else if(heuristicClassification == AutofillStructure.LOGIN_FORM_WITH_DATA){
            val username = parsedData.editTextList.elementAt(0).text.toString()
            val password = parsedData.editTextList.elementAt(1).text.toString()
            Log.e(AS_TAG, "SAVE PATH TRIGGERED!")
            Log.e(AS_TAG, "Saving login state possibility: ${username} :: ${password}")
            callback.onSuccess(null)
            TODO("Next step (when it's triggered?)")
        }
        else if(heuristicClassification == AutofillStructure.REGISTER_FORM_WITH_DATA){ //Proceed to ask user to save credentials
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

    override fun onDestroy(){
        serviceScope.cancel()
    }

    /* Access to the database */
    private suspend fun queryCredentialsForDomain(domain : String) : List<Credential>{
        val databaseHandler = CredentialRoomDatabase.getDatabase(applicationContext)
        val passwordController = PasswordController(getSharedPreferences(packageName, Context.MODE_PRIVATE))
        val result : MutableList<Credential> = mutableListOf()
        return withContext(Dispatchers.IO){
            val databaseCredentials = databaseHandler.credentialDao().searchCredentials(domain) //Testing
            for(cred in databaseCredentials){
                result.add(Credential(cred.username, passwordController.decrypt(cred.password)))
            }
            result
        }
    }

    /* Classes for the service*/
    private data class Credential(val username : String, val password : String)

    private data class LoginAutofill(val usernameId : AutofillId, val passwordId : AutofillId)
}
