package fr.hornik.coinche.component

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.stereotype.Service

@Service
class FireApp {
    final val firebaseApp: FirebaseApp

    init {
        // /!\ Don't forget to set the env variable of GOOGLE_APPLICATION_CREDENTIALS
        // to the location of the firebase admin private key
        val options = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .setDatabaseUrl("https://coinche-47d27.firebaseio.com")
                .build()

        firebaseApp = FirebaseApp.initializeApp(options)
    }
}