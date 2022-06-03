package it.unipd.dei.esp2122.passwordmanager

import android.app.assist.AssistStructure

class ParsedStructure(private val structure : AssistStructure) {

    var counter = 0

    init {
        for(index in 0 until structure.windowNodeCount){
            parseNode(structure.getWindowNodeAt(index).rootViewNode)
        }
    }

    private fun parseNode(node : AssistStructure.ViewNode){
        println(node.className)
        if(node.autofillHints?.isNotEmpty() == true){
            //The node has autofill hints
            val hints = node.autofillHints.contentToString()
            println("Autofill hints: $hints") //DEBUG (What is going on?)
            //println("Entered Autofill hints")
        }
        else{
            //The node has no autofill hints, ignore them for now
            val hint = node.hint
            println("Heuristic: $hint")
        }

        counter++

        //Parse children
        for(index in 0 until node.childCount){
            parseNode(node.getChildAt(index))
        }
    }
}