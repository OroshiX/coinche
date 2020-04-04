package fr.hornik.coinche.rest

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.apache.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse


@RestController
class LoginController {
    @Deprecated("Use /loginToken instead",
                ReplaceWith("loginToken(@RequestBody idToken: String)",
                            "org.springframework.web.bind.annotation.RequestBody"))
    @PostMapping("/login")
    fun login(username: String, password: String, response: HttpServletResponse) {
        response.sendError(HttpStatus.SC_BAD_REQUEST, "/login is deprecated, use /loginToken instead")
    }

    @PostMapping("/loginToken")
    fun loginToken(@RequestBody idToken: String) {
        // /!\ Don't forget to set the env variable of GOOGLE_APPLICATION_CREDENTIALS
        // to the location of the firebase admin private key
        val options = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
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