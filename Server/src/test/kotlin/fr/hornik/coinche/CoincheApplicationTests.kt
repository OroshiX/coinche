package fr.hornik.coinche

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
                classes = [CoincheApplication::class])
@AutoConfigureMockMvc
class CoincheApplicationTests {

    @Test
    fun contextLoads() {
    }

}
