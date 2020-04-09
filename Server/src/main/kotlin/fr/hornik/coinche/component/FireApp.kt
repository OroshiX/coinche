package fr.hornik.coinche.component

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import fr.hornik.coinche.DataManagement
import fr.hornik.coinche.dto.UserDto
import fr.hornik.coinche.model.SetOfGames
import fr.hornik.coinche.model.User
import fr.hornik.coinche.serialization.JsonSerialize
import org.springframework.stereotype.Service
import java.util.*

@Service
class FireApp {
    final val firebaseApp: FirebaseApp
    final val db: Firestore

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
     * Saves the table to firebase
     */
    fun saveNewGame(setOfGames: SetOfGames): String {
        val jsonSet = JsonSerialize.toJson(setOfGames)
        val addedDocRef =
                db.collection(DataManagement.COLLECTION_SETS)
                        .add(mapOf("gson" to jsonSet))
        return addedDocRef.get().id
    }

    fun updateGame(table: SetOfGames) {
        val jsonTable = JsonSerialize.toJson(table.copy(lastModified = Date()))
        db.collection(DataManagement.COLLECTION_SETS).document(table.id)
                .set(mapOf("gson" to jsonTable))
    }

    fun getOrSetUsername(user: User): String {
        var username: String = user.nickname
        val doc = db.collection(DataManagement.COLLECTION_PLAYERS)
                .document(user.uid)
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

    fun getAllGames(): List<SetOfGames> {
        val sets = mutableListOf<SetOfGames>()
        val listDocuments =
                db.collection(DataManagement.COLLECTION_SETS).listDocuments()
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