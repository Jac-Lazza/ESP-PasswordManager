package it.unipd.dei.esp2122.passwordmanager

import android.app.Service
import android.app.assist.AssistStructure
import android.content.Intent
import android.os.CancellationSignal
import android.os.IBinder
import android.service.autofill.*

class PMAutofillService : AutofillService() {

    /*override fun onBind(intent: Intent): IBinder {
        //In an autofill service this method cannot be overwritten
    }*/

    override fun onFillRequest(request : FillRequest, cancellationSignal : CancellationSignal, callback : FillCallback) {
        val context  : MutableList<FillContext> = request.fillContexts
        val structure : AssistStructure = context[context.size - 1].structure
        val parsedData = ParsedStructure(structure)
        println("Number of child nodes: ${parsedData.counter}")
        callback.onSuccess(null) //This is the end of the function

    }

    override fun onSaveRequest(request : SaveRequest, callback : SaveCallback) {
        TODO("Not yet implemented")
    }
}