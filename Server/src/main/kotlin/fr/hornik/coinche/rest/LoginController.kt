package fr.hornik.coinche.rest

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.FileInputStream


@RestController
class LoginController {
    @Deprecated("Use /loginToken instead")
    @PostMapping("/login")
    fun login(username: String, password: String) {
    }

    @PostMapping("/loginToken")
    fun loginToken(@RequestBody idToken: String) {
        val serviceAccount =
                FileInputStream("D:\\Documents\\Dev\\keystores\\coinche-47d27-firebase-adminsdk-s7ea6-6b8671839a.json")

        val options = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://coinche-47d27.firebaseio.com")
                .build()

        val firebaseApp = FirebaseApp.initializeApp(options)
        val database = FirebaseDatabase.getInstance(firebaseApp)
        val auth = FirebaseAuth.getInstance(firebaseApp)

        val decodedToken = auth.verifyIdToken(idToken)
        val uid = decodedToken.uid
        println("uid: $uid")
    }
}