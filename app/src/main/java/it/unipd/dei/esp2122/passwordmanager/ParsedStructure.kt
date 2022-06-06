package it.unipd.dei.esp2122.passwordmanager

import android.app.assist.AssistStructure
import android.content.pm.PackageManager
import android.util.Log
import android.widget.EditText
import android.widget.TextView

class ParsedStructure(private val structure : AssistStructure) {

    val textViewList : MutableList<AssistStructure.ViewNode> = mutableListOf()
    val editTextList : MutableList<AssistStructure.ViewNode> = mutableListOf()
    var domain : String = ""
    var autofillHintsDetected = 0

    init {
        domain = structure.activityComponent.packageName
        /*val pm = PackageManager()
        val appInfo = pm.getApplicationInfo()*/

        if(structure.windowNodeCount > 0) {
            for (index in 0 until structure.windowNodeCount) {
                parseNode(structure.getWindowNodeAt(index).rootViewNode) //Iterating throughout a forest of ViewNodes
            }
        }
    }

    /* It's a DFS that focuses on the leaves of the tree*/
    private fun parseNode(node : AssistStructure.ViewNode){
        //Step 1: We need just the leaves, the rest can be ignored
        if(node.childCount > 0){
            for(index in 0 until node.childCount){
                parseNode(node.getChildAt(index))
            }
        }
        else{
            //Step 2: Of all the leaves, we need EditTexts and TextViews
            if(node.className == TextView::class.qualifiedName){
                textViewList.add(node)
            }
            else if(node.className == EditText::class.qualifiedName){
                editTextList.add(node)
            }

            //Step 3: We check for the presence of autofillHints (Experiments shows that they are rarely used, making our work harder)
            if(node.autofillHints?.isNotEmpty() == true){ //boolean == true, and I'm forced to do this...
                autofillHintsDetected++
            }
        }
    }
}