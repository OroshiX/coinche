package fr.hornik.coinche.rest

import fr.hornik.coinche.component.DataManagement
import fr.hornik.coinche.component.FireApp
import fr.hornik.coinche.model.User
import fr.hornik.coinche.util.dbgLevel
import fr.hornik.coinche.util.debugPrintln
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*

//@RestController
//@Component
//@RequestMapping("/maintenance")
class MaintenanceController(@Autowired val data: DataManagement, @Autowired val fire: FireApp) {

    // Security Hole +  Very long call - just for developers
    @GetMapping("/listLogin", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun listLogin(): List<User> {
        return fire.getAllUsersUnused(data.sets)
    }

    @DeleteMapping("/deleteUser")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun removeUser(@RequestBody uid: String) {
        debugPrintln(dbgLevel.DEBUG, "Removing User with uid : $uid")
        fire.deleteUser(uid)
    }

    @DeleteMapping("/deleteAllUsers")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun deleteAllUnusedUsers() {
        debugPrintln(dbgLevel.DEBUG, "Removing Unused Users")
        fire.deleteUnusedUsers(sets = data.sets)
    }

}