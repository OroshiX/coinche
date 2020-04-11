package fr.hornik.coinche

import fr.hornik.coinche.component.DataManagement
import fr.hornik.coinche.component.FireApp
import fr.hornik.coinche.model.User
import fr.hornik.coinche.rest.CorsConfiguration
import fr.hornik.coinche.rest.GameController
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
@WebMvcTest(GameController::class)
@ContextConfiguration(
        classes = [DataManagement::class, FireApp::class, User::class,
            CorsConfiguration::class
        ])
@AutoConfigureRestDocs(outputDir = "target/snippets")
class RestDocsApplicationTests {
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var data: DataManagement

    @Autowired
    private lateinit var user: User

    @Autowired
    private lateinit var fire: FireApp

    @BeforeEach
    fun setup(restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.standaloneSetup(
                GameController(data, user, fire))
                .apply<StandaloneMockMvcBuilder>(
                        documentationConfiguration(restDocumentation))
                .build()
    }

    @Test
    fun shouldReturnDefaultMessage() {
        this.mockMvc.perform(get("/games/home"))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().string(
                        containsString("SimpleBid")))
                .andDo(document("home"))
    }
}