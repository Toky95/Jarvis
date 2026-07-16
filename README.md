# Jarvis Offline — Assistant vocal Android

## Installation
1. Ouvre ce dossier dans **Android Studio** (File > Open).
2. Laisse Gradle synchroniser (nécessite une connexion internet la première fois, pour télécharger les dépendances de compilation — l'app elle-même fonctionnera ensuite hors ligne).
3. Branche ton téléphone Android (mode débogage USB activé) ou utilise un émulateur.
4. Clique sur "Run".
5. Accepte toutes les permissions demandées (micro, appels, contacts).

## Avant utilisation — indispensable pour le mode hors ligne
Sur ton téléphone Android :
- **Reconnaissance vocale hors ligne** : Paramètres > Système > Langues et saisie > Reconnaissance vocale > Reconnaissance hors connexion > télécharge le pack **Français**.
- **Synthèse vocale (TTS) hors ligne** : Paramètres > Système > Langues et saisie > Sortie synthèse vocale > moteur Google > télécharge la voix **Français**.

Une fois ces deux packs installés, l'app n'a plus besoin d'internet.

## Utilisation
Lance l'app, appuie sur "Démarrer l'assistant". Il écoute alors en continu et réagit quand il entend le mot **"Jarvis"** suivi d'une commande :
- **"Jarvis réveille-toi"** → allume l'écran et tente de déverrouiller.
- **"Jarvis sonne-toi"** → monte le volume et joue la sonnerie (pratique pour retrouver son téléphone).
- **"Jarvis appelle [nom]"** → cherche le contact et lance l'appel.

## Limite importante à connaître
Si ton téléphone a un **code PIN, schéma ou empreinte digitale** configuré comme verrouillage, **aucune app ne peut le déverrouiller automatiquement** — c'est une protection de sécurité volontaire d'Android, impossible à contourner légalement, même avec les permissions ci-dessus. La commande "réveille-toi" allumera l'écran mais tu devras quand même entrer ton code toi-même.

Si tu veux un vrai déverrouillage automatique à la voix, la seule option est de retirer le verrouillage sécurisé du téléphone (Paramètres > Sécurité > Aucun / Glisser) — ce qui réduit évidemment la sécurité globale de l'appareil.

## Pour aller plus loin
- Remplacer le moteur de reconnaissance par **Vosk** (bibliothèque open-source, 100% offline, plus précise) si `SpeechRecognizer` d'Android n'est pas assez fiable sur ton appareil.
- Ajouter d'autres commandes dans `traiterPhrase()` du fichier `VoiceAssistantService.kt`.
- Changer le mot-clé d'activation en modifiant la variable `motCle`.
