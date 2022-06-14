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

/*
* La classe PMAutofillService è dedicata al riempimento delle caselle di testo di altre applicazioni
* con i dati dell'utente memorizzati. Purtroppo la realizzazione dell'Autofill si è rivelata più complicata
* di quanto previsto e si è deciso di sviluppare una piccola parte significativa in modo sicuro secondo
* le nostre conoscenze
* */
class PMAutofillService : AutofillService() {

    private val serviceScope = CoroutineScope(EmptyCoroutineContext)

    private companion object{
        const val AS_TAG = "AutofillService"
    }

    /*override fun onBind(intent: Intent): IBinder {
        //In un servizio di autofill la funzione onBind non può essere sovrascritta
    }*/

    override fun onFillRequest(request : FillRequest, cancellationSignal : CancellationSignal, callback : FillCallback) {
        val context  : MutableList<FillContext> = request.fillContexts
        val structure : AssistStructure = context[context.size - 1].structure
        val parsedData = AutofillStructure(structure)

        if(parsedData.domain == packageName){
            //Richieste di autofill da parte dell'applicazione stessa vengono ignorate
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
            * (limitato ma restrittivo) per capire la natura dell'activity dell'applicazione.
            * */
        }

        val heuristicClassification = parsedData.heuristicClassification()

        if(heuristicClassification == AutofillStructure.EMPTY_LOGIN_FORM){
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
        else if(heuristicClassification == AutofillStructure.LOGIN_FORM_WITH_DATA){
            /*
            * Questo ramo dell'autofill dovrebbe essere richiamato quando l'utente inserisce delle credenziali
            * ignorando il servizio di autofill. Se questo è il caso allora l'applicazione dovrebbe proseguire
            * a chiedere il salvataggio dei dati.
            * Purtroppo non siamo riusciti a stimolare questo comportamento all'interno delle applicazioni
            * testate per l'autofill
            * */
            callback.onSuccess(null)
        }
        else if(heuristicClassification == AutofillStructure.REGISTER_FORM_WITH_DATA){
            /*
            * Analogo al caso precedente, ma per un form di registrazione
            * */
            callback.onSuccess(null)
        }
        else{
            /*
            * Il comportamento di default è quello di non iniettare i dati dell'utente all'interno
            * delle applicazioni che richiedono il servizio a meno che non si sia sicuri di aver riconosciuto
            * l'activity che ha scatenato la richiesta di autofill
            * */
            callback.onSuccess(null)
        }

    }

    /*
    * Logica dedicata al salvataggio dei dati, non implementata
    * */
    override fun onSaveRequest(request : SaveRequest, callback : SaveCallback) {
        callback.onSuccess()
    }

    override fun onDestroy(){
        serviceScope.cancel()
    }

    /* Accesso al database */
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

    /* Classi interne ad uso del service */
    private data class Credential(val username : String, val password : String)
}
