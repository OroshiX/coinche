package fr.hornik.coinche

import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import fr.hornik.coinche.component.FireApp
import fr.hornik.coinche.model.Player
import fr.hornik.coinche.model.SetOfGames
import fr.hornik.coinche.model.User
import fr.hornik.coinche.model.values.TableState
import fr.hornik.coinche.serialization.JsonSerialize
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class DataManagement(@Autowired fire: FireApp) {

    private final val sets: MutableList<SetOfGames> = mutableListOf()
    private final val db: Firestore = FirestoreClient.getFirestore(fire.firebaseApp)

    init {
        getAllGamesFromFirebase()
    }

    companion object {
        const val COLLECTION_SETS = "sets"
        const val COLLECTION_PLAYERS = "players"
    }

    fun allMyGames(uid: String): List<SetOfGames> = sets.filter { setOfGames ->
        !setOfGames.isFull() || setOfGames.players.map { it.uid }.contains(uid)
    }

    /**
     * Create a new table with the info given, and save it to firebase,
     * and return a new table object
     */
    fun createGame(setOfGames: SetOfGames): SetOfGames {
        val id = saveNewGame(setOfGames)
        val tableAdded = setOfGames.copy(id = id, lastModified = Date())
        sets.add(tableAdded)
        return tableAdded
    }

    /**
     * Saves the table to firebase
     */
    fun saveNewGame(setOfGames: SetOfGames): String {
        val jsonSet = JsonSerialize.toJson(setOfGames)
        val addedDocRef = db.collection(COLLECTION_SETS).add(mapOf("gson" to jsonSet))
        return addedDocRef.get().id
    }

    /**
     * Precondition: the game is not full
     */
    fun joinGame(set: SetOfGames, user: User): Player {

        // Always called when it is not full
        var username: String = user.nickname
        val playerDoc = db.collection(COLLECTION_PLAYERS).document(user.uid).get().get()
        if (playerDoc.exists()) {
            playerDoc.toObject(Player::class.java)?.let {
                username = it.nickname
            }
        }
        val player = set.addPlayer(user.uid, username)
        if (set.isFull()) {
            // Time to distribute
            set.state = TableState.DISTRIBUTING
        }
        return player

    }

    fun getGame(setId: String): SetOfGames? = sets.firstOrNull { it.id == setId }

    fun updateGame(table: SetOfGames) {
        val jsonTable = JsonSerialize.toJson(table)
        db.collection(COLLECTION_SETS).document(table.id).set(mapOf("gson" to jsonTable))
    }

    private final fun getAllGamesFromFirebase() {
        val listDocuments = db.collection(COLLECTION_SETS).listDocuments()
        for (docRef in listDocuments) {
            val documentSnapshot = docRef.get().get()
            if (documentSnapshot.exists()) {
                val data: String = documentSnapshot.data?.getValue("gson") as String? ?: ""
                JsonSerialize.fromJson<SetOfGames>(data).let {
                    sets.add(it.copy(id = documentSnapshot.id))
                }
            }
        }
    }

}