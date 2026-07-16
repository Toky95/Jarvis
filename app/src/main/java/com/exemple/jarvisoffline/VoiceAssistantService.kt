package com.exemple.jarvisoffline

import android.app.*
import android.app.KeyguardManager
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
        return START_STICKY
    }

    private fun lancerEcoute() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR")
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        enEcoute = true
        recognizer.startListening(intent)
    }

    override fun onResults(results: android.os.Bundle?) {
        val phrases = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val phrase = phrases?.firstOrNull()?.lowercase(Locale.FRENCH) ?: ""
        traiterPhrase(phrase)
        redemarrerEcoute()
    }

    override fun onError(error: Int) {
        redemarrerEcoute()
    }

    private fun redemarrerEcoute() {
        enEcoute = false
        android.os.Handler(mainLooper).postDelayed({ lancerEcoute() }, 500)
    }

    private fun traiterPhrase(phrase: String) {
        if (!phrase.contains(motCle)) return

        when {
            phrase.contains("réveille") || phrase.contains("reveille") -> reveillerEcran()
            phrase.contains("sonne") -> faireSonner()
            phrase.contains("appelle") -> {
                val nomContact = extraireNomApresMot(phrase, "appelle")
                if (nomContact.isNotBlank()) appelerContact(nomContact)
                else parler("Quel est le nom du contact ?")
            }
            else -> parler("Je n'ai pas compris la commande.")
        }
    }

    private fun extraireNomApresMot(phrase: String, mot: String): String {
        val index = phrase.indexOf(mot)
        return if (index >= 0) phrase.substring(index + mot.length).trim() else ""
    }

    private fun reveillerEcran() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = pm.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "JarvisOffline:reveil"
        )
        wakeLock.acquire(10_000)

        val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
