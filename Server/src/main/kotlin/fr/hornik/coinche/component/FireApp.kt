package fr.hornik.coinche.component

import com.google.api.core.ApiFuture
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.Transaction
import com.google.cloud.firestore.WriteResult
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.database.util.JsonMapper
import fr.hornik.coinche.dto.Table
import fr.hornik.coinche.dto.UserDto
import fr.hornik.coinche.model.SetOfGames
import fr.hornik.coinche.model.Statistic
import fr.hornik.coinche.model.User
import fr.hornik.coinche.serialization.JsonSerialize
import fr.hornik.coinche.util.dbgLevel
import fr.hornik.coinche.util.debugPrintln
import fr.hornik.coinche.util.traceLevel
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
        const val COLLECTION_STATISTIQUES = "statistics"
        var statisticLocale = true
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
                db.collection(COLLECTION_SETS).add(JsonMapper.parseJson(jsonSet))
        return addedDocRef.get().id
    }

    fun getStatistic(uid: String = ""): Statistic {
        if (statisticLocale) {
            //emulation of firebase
            return Statistic("uid", 0, 0, 0, 0)
        } else {
            // we need to look in firebase
            return Statistic()
        }
    }

    fun saveStatistics(setOfGames: SetOfGames) {
        val nameFunction = object {}.javaClass.enclosingMethod.name
        val oldTraceLevel = traceLevel
        traceLevel = dbgLevel.ALL
        // we should call this function everytime we end a game

        for (uid in setOfGames.players.map { it.uid }) {
            val aStatistic = getStatistic(uid)
            // update statistic with the right value

            // write statistics in the DB
            if (DataManagement.productionAction) {
                val future: ApiFuture<WriteResult> = db.collection(COLLECTION_STATISTIQUES).document(uid)
                        .set(aStatistic)
                // println("saveStatistique : " + future.get().getUpdateTime() + "future:" +future.toString())
                val arg = aStatistic.toString()
                debugPrintln(dbgLevel.DEBUG, "JSON from saveTable ${arg}")

            } else {
                debugPrintln(dbgLevel.REGULAR, "$nameFunction no saving mode")
            }
        }
        traceLevel = oldTraceLevel
    }

    fun deleteGame(setOfGames: SetOfGames) {
        for (uid in setOfGames.players.map { it.uid }) {
            db.collection(COLLECTION_PLAYERS_SETS).document(setOfGames.id)
                    .collection(COLLECTION_PLAYERS).document(uid).delete()
        }
        db.collection(COLLECTION_PLAYERS_SETS).document(setOfGames.id).delete()
        db.collection(COLLECTION_SETS).document(setOfGames.id).delete()
    }


    fun transactionSaveGame(setOfGames: SetOfGames) {
        val nameFunction = object {}.javaClass.enclosingMethod.name

        val docRef = db.collection(COLLECTION_SETS).document(setOfGames.id)
        val jsonTable = JsonSerialize.toJson(setOfGames.copy(lastModified = Date()))
        val tablesRef = setOfGames.players.filter { !it.uid.contains(DataManagement.AUTOMATEDPLAYERSID) }.map { e ->
            Pair(
                    db.collection(COLLECTION_PLAYERS_SETS).document(setOfGames.toTable(e.uid).id)
                            .collection(COLLECTION_PLAYERS).document(e.uid), setOfGames.toTable(e.uid).toFirebase())
        }

        // run an asynchronous transaction

        val futureTransaction = db.runTransaction<Void?> { transaction: Transaction ->
            // retrieve document and increment population field
            transaction.set(docRef, JsonMapper.parseJson(jsonTable))
            for (pair in tablesRef) {
                transaction.set(pair.first, pair.second)
            }
            null
        }

    }


    fun saveGame(setOfGames: SetOfGames, new: Boolean = false) {

        // save the game in sets
        if (new) {
            val id = saveNewGame(setOfGames)
            setOfGames.id = id


            // save the tables for players in playersSets
            for (uid in setOfGames.players.map { it.uid }.filter { !it.contains(DataManagement.AUTOMATEDPLAYERSID) }) {
                saveTable(setOfGames.toTable(uid), uid)
            }
        } else {
            transactionSaveGame(setOfGames)
        }
    }

    private fun saveTable(table: Table, userUID: String): String {
        val nameFunction = object {}.javaClass.enclosingMethod.name

        if (DataManagement.productionAction) {
            val future: ApiFuture<WriteResult> = db.collection(COLLECTION_PLAYERS_SETS).document(table.id)
                    .collection(COLLECTION_PLAYERS).document(userUID)
                    .set(table.toFirebase())
            // println("SaveTable : " + future.get().getUpdateTime() + "future:" +future.toString())
            val arg = table.toFirebase().toString()
            debugPrintln(dbgLevel.DEBUG, "JSON from saveTable ${arg}")

        } else {
            debugPrintln(dbgLevel.REGULAR, "$nameFunction no saving mode")
        }
        return userUID
    }

    private fun updateGame(setOfGames: SetOfGames) {
        val nameFunction = object {}.javaClass.enclosingMethod.name

        if (DataManagement.productionAction) {

            val jsonTable = JsonSerialize.toJson(setOfGames.copy(lastModified = Date()))

            val future: ApiFuture<WriteResult> = db.collection(
                    COLLECTION_SETS).document(setOfGames.id)
                    .set(JsonMapper.parseJson(jsonTable))


            // For debugging purpose only.
            val arg = JsonMapper.parseJson(jsonTable).toString()
            debugPrintln(dbgLevel.DEBUG, "JSON from updateGame $arg")

            /*
          try {
             println("updateGame time : " + future.get().getUpdateTime())//+ "future:" +future.toString())
         } catch (e: InvalidArgumentException) {
         println("********Exception $e with $arg")
     }
      */
        } else {
            debugPrintln(dbgLevel.REGULAR, "Debug mode - $nameFunction no saving")
        }
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

    fun getAllUsersUnused(sets: MutableList<SetOfGames>): List<User> {
        val uidUsedList = sets.map { e -> e.players.map { it.uid } }.flatten().asSequence()

        val documentUsersList =
                db.collection(COLLECTION_PLAYERS).listDocuments()
        val allUserList = documentUsersList.mapNotNull { v -> v.get().get().toObject(UserDto::class.java) }

        return allUserList.filter { e -> uidUsedList.none { it == e.uid } }.map { User(it.uid, it.nickname) }
    }

    fun deleteUnusedUsers(sets: MutableList<SetOfGames>) {
        val unusedUsers = getAllUsersUnused(sets).filter{it.uid.contains(DataManagement.AUTOMATEDPLAYERSID)}
        for (unusedUser in unusedUsers) {
            deleteUser(unusedUser.uid)
        }
        debugPrintln(dbgLevel.DEBUG,"All unused Users have been deleted")
    }
    fun deleteUser(uid: String) {
        val future = db.collection(COLLECTION_PLAYERS).document(uid).delete()
        debugPrintln(dbgLevel.REGULAR,"User $uid deleted at ${future.get().updateTime} : $future")

    }

    fun getAllGames(): List<SetOfGames> {
        val sets = mutableListOf<SetOfGames>()
        val listDocuments = db.collection(COLLECTION_SETS).listDocuments()
        for (docRef in listDocuments) {
            val documentSnapshot = docRef.get().get()
            if (documentSnapshot.exists()) {
                val jsonString = JsonMapper.serializeJson(documentSnapshot.data)
                JsonSerialize.fromJson<SetOfGames>(jsonString).let {
                    sets.add(it.copy(id = documentSnapshot.id))
                }
            }
        }
        return sets
    }
}