package fr.hornik.coinche

import fr.hornik.coinche.component.DataManagement
import fr.hornik.coinche.component.FireApp
import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.PlayerPosition
import fr.hornik.coinche.util.dbgLevel
import fr.hornik.coinche.util.debugPrintln
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations

class DataTest {

    lateinit var dataManagement: DataManagement
    lateinit var set: SetOfGames



    @BeforeEach
    fun initState() {
        dataManagement = DataManagement(FireApp())
        set = SetOfGames(id = "1")
        MockitoAnnotations.initMocks(this)
        //traceLevel = dbgLevel.NONE

    }


    @Test
    fun example() {
//        Mockito.`when`(fire.updateGame(set))
//        given(fireApp.saveNewGame(set)).willReturn("toto")
//        given(fireApp.getAllGames()).willReturn(listOf())
//        given(fireApp.getOrSetUsername(User())).willReturn("Tutu")
        dataManagement.createGame(set, User())
        print(dataManagement.getGameOrThrow(set.id))
    }

    @Test
    fun testSaveStat() {
        val ArmandH: User = User("AHID", "Armand")
        val Toto: User = User("ToId", "TOTO")
        val Tata: User = User("TaId", "TATA")
        val Tutu: User = User("TuId", "TUTU")
        var set = SetOfGames(id = "testGAMEID", name = "testGAME")
        dataManagement.joinGame(set, ArmandH, "firstnameArm")

        dataManagement.joinGame(set, Toto, "firstnameToto")
        dataManagement.joinGame(set, Tutu, "firstnameTutu")
        dataManagement.joinGame(set, Tata, "firstnameTata")
        dataManagement.statUpdate(set)
    }

    @Test
    fun testGetStat() {
        //get User in priority on UID

        val uid = "ToId"
        val uid1 = "TaId"
        val stat = dataManagement.getStat(uid="ToId")
        val user = User(uid,"TOTO")
        val stat1 = dataManagement.getStat(user=user)
        val stat2 = dataManagement.getStat(user=user,uid=uid)
        val stat3 = dataManagement.getStat(user=user,uid=uid1)
        val stat4 = dataManagement.getStat(uid=uid1)
        val stat5 = dataManagement.getStat(uid=uid)
        debugPrintln(dbgLevel.REGULAR, "Statistic for ToId is \n\tstat=$stat (uid=$uid), \n\tstat1=$stat1(user=$user), \n\tstat2=$stat2(user=$user,uid=$uid)\n\tstat3=$stat3((user=$user,uid=$uid1)\n\tstat4=$stat4(ui=$uid1)")
    }

    @Test
    fun testPlayCard() {
        debugPrintln(dbgLevel.MISC,"**************************************************************")
        val ptsRand = listOf(80, 90, 100, 110, 120, 130, 140, 150, 160)

        val ArmandH: User = User("AHID", "Armand")
        val Toto: User = User("ToId", "TOTO")
        val Tata: User = User("TaId", "TATA")
        val Tutu: User = User("TuId", "TUTU")
        var set = SetOfGames(id = "testGAMEID", name = "testGAME")


        dataManagement.createGame(set, ArmandH)
        dataManagement.joinGame(set, ArmandH, "firstnameArm")

        dataManagement.joinGame(set, Toto, "firstnameToto")
        dataManagement.joinGame(set, Tutu, "firstnameTutu")
        dataManagement.joinGame(set, Tata, "firstnameTata")

        var mylplayer = mutableListOf(Pair(ArmandH, PlayerPosition.NORTH), Pair(Toto, PlayerPosition.EAST), Pair(Tutu, PlayerPosition.SOUTH), Pair(Tata, PlayerPosition.WEST))
        var newlist: MutableList<Pair<User, PlayerPosition>> = mutableListOf()

        for (i in mylplayer) {
            newlist.add(Pair(i.first, set.players.first { it.uid == i.first.uid }.position))
        }

        debugPrintln(dbgLevel.MISC,"Newlist = $newlist*****\n\n")

        val aBid = SimpleBid(CardColor.values().random(), ptsRand.random(), set.whoseTurn)
        var aPass = Pass(set.whoseTurn + 1)

        debugPrintln(dbgLevel.MISC,"\n\n$set\n\n\n")
        debugPrintln(dbgLevel.MISC,"\nnext to play : ${set.whoseTurn}\n\n")

        dataManagement.announceBid(set, aBid, newlist.first { it.second == set.whoseTurn }.first)
        aPass = Pass(set.whoseTurn)

        dataManagement.announceBid(set, aPass, newlist.first { it.second == set.whoseTurn }.first)
        aPass = Pass(set.whoseTurn)
        dataManagement.announceBid(set, aPass, newlist.first { it.second == set.whoseTurn }.first)
        aPass = Pass(set.whoseTurn)

        dataManagement.announceBid(set, aPass, newlist.first { it.second == set.whoseTurn }.first)


        debugPrintln(dbgLevel.MISC,"BID is ${set.currentBid}")
        for (tour in 0..7) {
            debugPrintln(dbgLevel.MISC,"Tour $tour \n")
            for (i in 0..3) {
                debugPrintln(dbgLevel.MISC,"Joueur $i")
                val prevPosition = set.whoseTurn
                var Acard = set.players.first { it.position == set.whoseTurn }.cardsInHand.first { it.playable == true }
                var myCard = Card(value = Acard.value, color = Acard.color)
                debugPrintln(dbgLevel.MISC,"${set.whoseTurn} plays $Acard\n")
                dataManagement.playCard(set, myCard, newlist.first { it.second == set.whoseTurn }.first)
                debugPrintln(dbgLevel.MISC,"Joueur $i has ${set.players.first { it.position == prevPosition }.cardsInHand.size} Cards ${set.players.first { it.position == prevPosition}.cardsInHand}")

            }

            debugPrintln(dbgLevel.MISC,"And now cards on table are ${set.onTable} \n and cards on Hand are ")
            for (play in 0..3) {
                debugPrintln(dbgLevel.MISC,"\n\nNBCARDS :${set.players[play].cardsInHand.size} : \n ${set.players[play].position}${set.players[play].cardsInHand}\n")
            }

        }
    }

}