package it.unipd.dei.esp2122.passwordmanager

import android.app.assist.AssistStructure
import android.content.Context
import android.os.CancellationSignal
import android.service.autofill.*
import android.util.Log
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import kotlinx.coroutines.*
import kotlin.coroutines.EmptyCoroutineContext

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

        if(parsedData.domain == packageName){   //Per non autofillare l'applicazione stessa
            callback.onSuccess(null)
            return
        }

        if(parsedData.autofillHintsDetected > 0){
            Log.d(AS_TAG,"Finally an application that uses autofill hints!")
            /*
            * Un servizio di autofill ha due modi per capire come agire nel riempimento delle view:
            *   1) Analizza gli autofill hints messi a disposizione del client
            *   2) Procede con un approccio euristico
            * Dalla nostra esperienza possiamo dire che nessuna delle applicazioni che abbiamo testato
            * mette a disposizione anche un solo autofill hint. Dunque, per rimanere con una logica
            * di autofill abbastanza contenuta, il service utilizzerà sempre un approccio euristico
            * (abbastanza severo aggiungerei) per capire la natura dell'activity dell'applicazione
            * */
        }

        val heuristicClassification = parsedData.heuristicClassification()

        if(heuristicClassification == AutofillStructure.EMPTY_LOGIN_FORM){ //Proceed to fill the login editTexts
            Log.d(AS_TAG, "Login form detected")
            val loginStructure = parsedData.getLoginStructure()
            serviceScope.launch(){
                Log.d(AS_TAG, "Got domain: ${parsedData.domain}")
                val credentials = queryCredentialsForDomain(parsedData.domain)

                val response = FillResponse.Builder()
                for(cred in credentials){
                    if(cred.username != ""){
                        val usernamePresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
                        usernamePresentation.setTextViewText(android.R.id.text1, cred.username)

                        val passwordPresentation = RemoteViews(packageName, android.R.layout.simple_list_item_1)
                        passwordPresentation.setTextViewText(android.R.id.text1, "Password for ${cred.username}")

                        val dataset = Dataset.Builder()
                        dataset.setValue(loginStructure.usernameId, AutofillValue.forText(cred.username), usernamePresentation)
                        dataset.setValue(loginStructure.passwordId, AutofillValue.forText(cred.password), passwordPresentation)

                        response.addDataset(dataset.build())
                    }
                }
                callback.onSuccess(response.build())
            }
            return
        }
        else if(heuristicClassification == AutofillStructure.LOGIN_FORM_WITH_DATA){ //Login with different credentials => save them
            callback.onSuccess(null)
        }
        else if(heuristicClassification == AutofillStructure.REGISTER_FORM_WITH_DATA){ //Proceed to ask user to save credentials
            callback.onSuccess(null)
        }
        else{
            callback.onSuccess(null) //For security reason: when heuristics fail, don't fill
        }

    }

    /*
    * Logica dedicata al salvataggio dei dati.
    * Purtroppo la realizzazione di un autofill è più complicata di quanto avevamo previsto e non è
    * l'obiettivo primario della consegna. Dunque abbiamo deciso di lasciare la parte di riempimento
    * sviluppata con attenzione nei confronti della sicurezza dell'utente (basta vedere i controlli
    * euristici eseguiti in AutofillStructure per determinare un login)
    * */
    override fun onSaveRequest(request : SaveRequest, callback : SaveCallback) {
        callback.onSuccess()
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
}
