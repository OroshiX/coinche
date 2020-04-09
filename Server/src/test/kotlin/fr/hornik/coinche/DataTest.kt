package fr.hornik.coinche

import fr.hornik.coinche.component.DataManagement
import fr.hornik.coinche.component.FireApp
import fr.hornik.coinche.model.SetOfGames
import fr.hornik.coinche.model.User
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
}