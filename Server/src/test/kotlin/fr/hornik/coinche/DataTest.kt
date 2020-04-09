package fr.hornik.coinche

import fr.hornik.coinche.component.FireApp
import fr.hornik.coinche.model.SetOfGames
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class DataTest(@Mock val fireApp: FireApp) {

    lateinit var dataManagement: DataManagement
    lateinit var set: SetOfGames

    @BeforeEach
    fun initState() {

        dataManagement = DataManagement(fireApp)
        set = SetOfGames(id = "1")
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun example() {
//        Mockito.`when`(fire.updateGame(set))
//        given(fireApp.saveNewGame(set)).willReturn("toto")
//        given(fireApp.getAllGames()).willReturn(listOf())
//        given(fireApp.getOrSetUsername(User())).willReturn("Tutu")
        dataManagement.createGame(set)
        print(dataManagement.getGameOrThrow("1"))
    }
}