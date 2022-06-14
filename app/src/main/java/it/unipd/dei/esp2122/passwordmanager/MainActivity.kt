package it.unipd.dei.esp2122.passwordmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
/*
Activity che ospita il NavHostFragment: un contenitore vuoto che mostra le destinazioni, ovvero fragment, del navigation graph
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}