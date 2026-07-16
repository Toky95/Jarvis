package com.exemple.jarvisoffline

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val permissionsNecessaires = mutableListOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.READ_CONTACTS
    ).apply {
        // Sur Android 13+ il faut aussi la permission notif pour le service au premier plan
        if (Build.VERSION.SDK_INT >= 33) add(Manifest.permission.POST_NOTIFICATIONS)
    }.toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Interface minimaliste faite en code (pas besoin de fichier XML séparé)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 96, 48, 48)
        }

        val texte = TextView(this).apply {
            text = "Assistant vocal offline\n\n" +
                    "Mot-clé d'activation : \"Jarvis\"\n" +
                    "Commandes reconnues :\n" +
                    "- \"Jarvis réveille-toi\"\n" +
                    "- \"Jarvis sonne-toi\"\n" +
                    "- \"Jarvis appelle [nom du contact]\"\n\n" +
                    "Astuce : pense à télécharger le pack de reconnaissance vocale " +
                    "et de synthèse vocale FRANÇAIS en mode HORS LIGNE dans :\n" +
                    "Paramètres > Système > Langues > Synthèse vocale / Google > Reconnaissance vocale hors ligne."
            textSize = 16f
        }

        val boutonDemarrer = Button(this).apply {
            text = "Démarrer l'assistant"
            setOnClickListener { demarrerAssistant() }
        }

        layout.addView(texte)
        layout.addView(boutonDemarrer)
        setContentView(layout)

        demanderPermissions()
    }

    private fun demanderPermissions() {
        val manquantes = permissionsNecessaires.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (manquantes.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, manquantes.toTypedArray(), 101)
        }
    }

    private fun demarrerAssistant() {
        val intent = Intent(this, VoiceAssistantService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
