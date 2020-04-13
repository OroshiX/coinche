package fr.hornik.coinche

import fr.hornik.coinche.component.DataManagement
import fr.hornik.coinche.component.FireApp
import fr.hornik.coinche.model.*
import fr.hornik.coinche.model.values.BeloteValue
import fr.hornik.coinche.model.values.CardColor
import fr.hornik.coinche.model.values.PlayerPosition
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import fr.hornik.coinche.CalculusTest.dbgLevel

class DataTest {

    lateinit var dataManagement: DataManagement
    lateinit var set: SetOfGames

    var TraceLevel: dbgLevel = dbgLevel.MISC
    fun DBGprintln(wantedLevel: dbgLevel, Str: Any) {

        if ((wantedLevel and TraceLevel) != 0) {
            println("DATATEST_$wantedLevel : $Str")
        }


    }

    @BeforeEach
    fun initState() {
        dataManagement = DataManagement(FireApp())
        set = SetOfGames(id = "1")
        MockitoAnnotations.initMocks(this)
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
    fun testPlayCard() {
        DBGprintln(dbgLevel.MISC,"**************************************************************")
        val ptsRand = listOf(80, 90, 100, 110, 120, 130, 140, 150, 160)

        val ArmandH: User = User("AHID", "Armand")
        val Toto: User = User("ToId", "TOTO")
        val Tata: User = User("TaId", "TATA")
        val Tutu: User = User("TuId", "TUTU")
        var set = SetOfGames(id = "testGAMEID", name = "testGAME")


        dataManagement.createGame(set, ArmandH)
        dataManagement.joinGame(set, ArmandH, "NickArm")

        dataManagement.joinGame(set, Toto, "NickToto")
        dataManagement.joinGame(set, Tutu, "NickTutu")
        dataManagement.joinGame(set, Tata, "NickTata")

        var mylplayer = mutableListOf(Pair(ArmandH, PlayerPosition.NORTH), Pair(Toto, PlayerPosition.EAST), Pair(Tutu, PlayerPosition.SOUTH), Pair(Tata, PlayerPosition.WEST))
        var newlist: MutableList<Pair<User, PlayerPosition>> = mutableListOf()

        for (i in mylplayer) {
            newlist.add(Pair(i.first, set.players.first { it.uid == i.first.uid }.position))
        }

        DBGprintln(dbgLevel.MISC,"Newlist = $newlist*****\n\n")

        val aBid = SimpleBid(CardColor.values().random(), ptsRand.random(), set.whoseTurn)
        var aPass = Pass(set.whoseTurn + 1)

        DBGprintln(dbgLevel.MISC,"\n\n$set\n\n\n")
        DBGprintln(dbgLevel.MISC,"\nnext to play : ${set.whoseTurn}\n\n")

        dataManagement.announceBid(set, aBid, newlist.first { it.second == set.whoseTurn }.first)
        aPass = Pass(set.whoseTurn)

        dataManagement.announceBid(set, aPass, newlist.first { it.second == set.whoseTurn }.first)
        aPass = Pass(set.whoseTurn)
        dataManagement.announceBid(set, aPass, newlist.first { it.second == set.whoseTurn }.first)
        aPass = Pass(set.whoseTurn)

        dataManagement.announceBid(set, aPass, newlist.first { it.second == set.whoseTurn }.first)


        DBGprintln(dbgLevel.MISC,"BID is ${set.currentBid}")
        for (tour in 0..7) {
            DBGprintln(dbgLevel.MISC,"Tour $tour \n")
            for (i in 0..3) {
                DBGprintln(dbgLevel.MISC,"Joueur $i")
                val prevPosition = set.whoseTurn
                var Acard = set.players.first { it.position == set.whoseTurn }.cardsInHand.first { it.playable == true }
                var myCard = Card(value = Acard.value, color = Acard.color)
                DBGprintln(dbgLevel.MISC,"${set.whoseTurn} plays $Acard\n")
                dataManagement.playCard(set, myCard, newlist.first { it.second == set.whoseTurn }.first, beloteValue = BeloteValue.NONE)
                DBGprintln(dbgLevel.MISC,"Joueur $i has ${set.players.first { it.position == prevPosition }.cardsInHand.size} Cards ${set.players.first { it.position == prevPosition}.cardsInHand}")

            }

            DBGprintln(dbgLevel.MISC,"And now cards on table are ${set.onTable} \n and cards on Hand are ")
            for (play in 0..3) {
                DBGprintln(dbgLevel.MISC,"\n\nNBCARDS :${set.players[play].cardsInHand.size} : \n ${set.players[play].position}${set.players[play].cardsInHand}\n")
            }

        }
    }

}