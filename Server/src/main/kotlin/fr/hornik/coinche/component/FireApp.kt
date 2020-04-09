package fr.hornik.coinche.component

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import fr.hornik.coinche.dto.Table
import fr.hornik.coinche.dto.UserDto
import fr.hornik.coinche.model.SetOfGames
import fr.hornik.coinche.model.User
import fr.hornik.coinche.serialization.JsonSerialize
import org.springframework.stereotype.Service
import java.util.*

@Service
class FireApp {
    final val firebaseApp: FirebaseApp
    private final val db: Firestore

    companion object {
        const val COLLECTION_SETS = "sets"
        const val COLLECTION_PLAYERS_SETS = "playersSets"
        const val COLLECTION_PLAYERS = "players"
    }

    init {
        // /!\ Don't forget to set the env variable of GOOGLE_APPLICATION_CREDENTIALS
        // to the location of the firebase admin private key
        val options = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .setDatabaseUrl("https://coinche-47d27.firebaseio.com")
                .build()

        firebaseApp = FirebaseApp.initializeApp(options)

        db = FirestoreClient.getFirestore(firebaseApp)
    }

    /**
     * Saves the new game to firebase
     */
    private fun saveNewGame(setOfGames: SetOfGames): String {
        val jsonSet =
                JsonSerialize.toJson(setOfGames.copy(lastModified = Date()))
        val addedDocRef =
                db.collection(COLLECTION_SETS).add(mapOf("gson" to jsonSet))
        return addedDocRef.get().id
    }

    fun saveGame(setOfGames: SetOfGames, new: Boolean = false) {
        // save the game in sets
        if (new) {
            val id = saveNewGame(setOfGames)
            setOfGames.id = id
        } else {
            updateGame(setOfGames)
        }

        // save the tables for players in playersSets
        for (uid in setOfGames.players.map { it.uid }) {
            saveTable(setOfGames.toTable(uid), uid)
        }
    }

    private fun saveTable(table: Table, userUID: String): String {
        db.collection(COLLECTION_PLAYERS_SETS).document(table.id)
                .collection(COLLECTION_PLAYERS).document(userUID)
                .set(table.toFirebase())
        return userUID
    }

    private fun updateGame(table: SetOfGames) {
        val jsonTable = JsonSerialize.toJson(table.copy(lastModified = Date()))
        db.collection(
                COLLECTION_SETS).document(table.id)
                .set(mapOf("gson" to jsonTable))
    }

    fun getOrSetUsername(user: User): String {
        var username: String = user.nickname
        val doc = db.collection(COLLECTION_PLAYERS).document(user.uid)
        val playerDoc = doc.get().get()
        if (playerDoc.exists()) {
            if (username.isBlank()) {
                // if the user has not given a name, but had formerly given it
                playerDoc.toObject(User::class.java)?.let {
                    username = it.nickname
                }
            } else {
                // If the user just gave a nickname, put it in firebase
                doc.set(UserDto(user))
            }
        }
        return username
    }

    fun setNewUsername(user: User) {
        val doc = db.collection(COLLECTION_PLAYERS).document(user.uid)
        doc.set(UserDto(user))
    }

    fun saveUser(userDto: UserDto): UserDto {
        // check if the user exists in firestore. If it exists, then get their nickname.
        val documentUser =
                db.collection(COLLECTION_PLAYERS).document(userDto.uid)
        val userDoc = documentUser.get().get()
        if (userDoc.exists()) {
            userDoc.toObject(UserDto::class.java)?.let {
                if (it.nickname.isNotBlank()) {
                    return userDto.copy(nickname = it.nickname)
                } else {
                    documentUser.set(userDto)
                }
            } ?: documentUser.set(userDto)
        } else {
            documentUser.set(userDto)
        }
        return userDto
    }
    fun getAllGames(): List<SetOfGames> {
        val sets = mutableListOf<SetOfGames>()
        val listDocuments = db.collection(COLLECTION_SETS).listDocuments()
        for (docRef in listDocuments) {
            val documentSnapshot = docRef.get().get()
            if (documentSnapshot.exists()) {
                val data: String =
                        documentSnapshot.data?.getValue("gson") as String? ?: ""
                JsonSerialize.fromJson<SetOfGames>(data).let {
                    sets.add(it.copy(id = documentSnapshot.id))
                }
            }
        }
        return sets
    }
}