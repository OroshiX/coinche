package fr.hornik.coinche.rest

import com.google.firebase.auth.FirebaseAuth
import fr.hornik.coinche.component.FireApp
import org.apache.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse

@RestController
@Component
class LoginController(@Autowired
                      val fire: FireApp) {

    @Deprecated("Use /loginToken instead",
                ReplaceWith("loginToken(@RequestBody idToken: String)",
                            "org.springframework.web.bind.annotation.RequestBody"))
    @PostMapping("/login")
    fun login(username: String, password: String, response: HttpServletResponse) {
        response.sendError(HttpStatus.SC_BAD_REQUEST, "/login is deprecated, use /loginToken instead")
    }

    @PostMapping("/loginToken")
    fun loginToken(@RequestBody idToken: String, response: HttpServletResponse) {
        val firebaseApp = fire.firebaseApp
//        val database = FirebaseDatabase.getInstance(firebaseApp)
        val auth = FirebaseAuth.getInstance(firebaseApp)

        val decodedToken = auth.verifyIdToken(idToken)
        val uid = decodedToken.uid
        println("uid: $uid")
        response.status = HttpStatus.SC_NO_CONTENT
    }
}