package fr.hornik.coinche.rest

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import fr.hornik.coinche.component.FireApp
import fr.hornik.coinche.dto.UserDto
import fr.hornik.coinche.exception.*
import fr.hornik.coinche.model.User
import fr.hornik.coinche.util.dbgLevel
import fr.hornik.coinche.util.debugPrintln
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*

@RestController
@Component
class LoginController(@Autowired val fire: FireApp, @Autowired
private val user: User) {
    private final val auth = FirebaseAuth.getInstance(fire.firebaseApp)

    @Deprecated("Use /loginToken instead",
                ReplaceWith("loginToken(@RequestBody idToken: String)",
                            "org.springframework.web.bind.annotation.RequestBody"))
    @PostMapping("/login")
    fun login(username: String, password: String) {
        throw DeprecatedException(
                "/login is deprecated, use /loginToken instead")
    }

    @PostMapping("/loginToken")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun loginToken(@RequestBody idToken: String) {
        try {
            debugPrintln(dbgLevel.LOGIN,"==============\nidToken: $idToken\n\n =======================")
            val decodedToken = auth.verifyIdToken(idToken)
            val uid = decodedToken.uid
            debugPrintln(dbgLevel.LOGIN,"uid: $uid")
            user.apply {
                this.uid = uid
                this.nickname =
                        if ((decodedToken.name == null ) || (decodedToken.name.isBlank())) decodedToken.email
                        else decodedToken.name
            }
            val saveUser = fire.saveUser(UserDto(user))
            user.nickname = saveUser.nickname
        } catch (e: FirebaseAuthException) {
            e.printStackTrace()
            // return 401 unauthorized
            throw LoginFailedException(e.message)
        }
    }

    @PostMapping("/logout")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun logout() {
        user.uid = ""
        user.nickname = ""
    }
}