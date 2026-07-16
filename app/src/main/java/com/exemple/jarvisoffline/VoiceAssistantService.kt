package com.exemple.jarvisoffline

import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.ContactsContract
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import java.util.Locale

class VoiceAssistantService : Service(), RecognitionListener {

    private lateinit var recognizer: SpeechRecognizer
    private lateinit var tts: TextToSpeech
    private var enEcoute = false

    // Change le mot d'activation ici si tu veux un autre nom
    private val motCle = "jarvis"

    override fun onCreate() {
        super.onCreate()
        creerCanalNotification()
        startForeground(1, construireNotification())

        tts = TextToSpeech(this) { statut ->
            if (statut == TextToSpeech.SUCCESS) {
                tts.language = Locale.FRENCH
            }
        }

        recognizer = SpeechRecognizer.createSpeechRecognizer(this)
        recognizer.setRecognitionListener(this)
        lancerEcoute()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // relance le service si le système le tue
    }

    private fun lancerEcoute() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR")
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true) // clé pour le mode hors-ligne
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        enEcoute = true
        recognizer.startListening(intent)
    }

    // --- Callbacks de reconnaissance vocale ---

    override fun onResults(results: android.os.Bundle?) {
        val phrases = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val phrase = phrases?.firstOrNull()?.lowercase(Locale.FRENCH) ?: ""
        traiterPhrase(phrase)
        redemarrerEcoute()
    }

    override fun onError(error: Int) {
        // En offline, il y a régulièrement des erreurs de timeout : on relance simplement l'écoute
        redemarrerEcoute()
    }

    private fun redemarrerEcoute() {
        enEcoute = false
        // petit délai pour éviter de spammer le moteur de reconnaissance
        android.os.Handler(mainLooper).postDelayed({ lancerEcoute() }, 500)
    }
