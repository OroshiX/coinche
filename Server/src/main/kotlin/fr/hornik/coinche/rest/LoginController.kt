package fr.hornik.coinche.rest

import com.google.firebase.auth.FirebaseAuth
import fr.hornik.coinche.component.FireApp
import fr.hornik.coinche.exception.DeprecatedException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Component
class LoginController(@Autowired
                      val fire: FireApp) {

    @Deprecated("Use /loginToken instead",
                ReplaceWith("loginToken(@RequestBody idToken: String)",
                            "org.springframework.web.bind.annotation.RequestBody"))
    @PostMapping("/login")
    fun login(username: String, password: String) {
        throw DeprecatedException("/login is deprecated, use /loginToken instead")
    }

    @PostMapping("/loginToken")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun loginToken(@RequestBody idToken: String) {
        val firebaseApp = fire.firebaseApp
//        val database = FirebaseDatabase.getInstance(firebaseApp)
        val auth = FirebaseAuth.getInstance(firebaseApp)

        val decodedToken = auth.verifyIdToken(idToken)
        val uid = decodedToken.uid
        println("uid: $uid")
    }
}