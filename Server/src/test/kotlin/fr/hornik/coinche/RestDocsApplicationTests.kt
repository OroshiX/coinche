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
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
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
        classes = [DataManagement::class, FireApp::class, CorsConfiguration::class])
@AutoConfigureRestDocs(outputDir = "target/snippets")
class RestDocsApplicationTests {
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var data: DataManagement

    private var user: User = User("vqKV9dWrkIe64NvS4c53vB10kMh2", "Jess")

    @Autowired
    private lateinit var fire: FireApp

    private val bid: List<FieldDescriptor> = listOf(
            fieldWithPath("type").description("Type of the bid"),
            fieldWithPath("position").description(
                    "Position of the player who took the bid"),
            fieldWithPath("color").optional().description("Color of the bid")
                    .type(JsonFieldType.STRING),
            fieldWithPath("points").optional()
                    .description("Number of points of the bid")
                    .type(JsonFieldType.NUMBER),
            fieldWithPath("surcoinche").optional()
                    .description("Whether this is coinche or surcoinche")
                    .type(JsonFieldType.BOOLEAN)
    )
    private val score: List<FieldDescriptor> = listOf(
            fieldWithPath("northSouth").description(
                    "Score of the camp North/South"),
            fieldWithPath("eastWest").description("Score of the camp East/West")
    )
    private val nicknames: List<FieldDescriptor> = listOf(
            fieldWithPath("north").description("nickname of North"),
            fieldWithPath("south").description("nickname of south"),
            fieldWithPath("east").description("nickname of east"),
            fieldWithPath("west").description("nickname of west")
    )

    @BeforeEach
    fun setup(restDocumentation: RestDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.standaloneSetup(
                GameController(data, user, fire))
                .apply<StandaloneMockMvcBuilder>(
                        documentationConfiguration(restDocumentation))
                .build()
    }

    @Test
    fun homeRequest() {
        this.mockMvc.perform(
                get("/games/home").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().string(
                        containsString("SimpleBid")))
                .andDo(document("home", responseFields(
                        subsectionWithPath("[]").description(
                                "The list of bids"))
                        .andWithPrefix("[].", bid)
                ))
    }

    @Test
    fun allGames() {
        this.mockMvc.perform(
                get("/games/ZhrlWIAsjZ6nS4fEiaZY/getTable").accept(
                        MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
                .andDo(document("getTable", responseFields(
                        subsectionWithPath("id").description("id of the game"),
                        fieldWithPath("nicknames").description(
                                "Nicknames of all the players at this table"),
                        fieldWithPath("cards").description("my cards in hand"),
                        fieldWithPath("onTable").description(
                                "what is played on the table"),
                        fieldWithPath("state").description(
                                "State of the table"),
                        fieldWithPath("nextPlayer").description(
                                "Whose turn is it"),
                        fieldWithPath("myPosition").description(
                                "What is my position"),
                        fieldWithPath("bids").description(
                                "What bids were already bid"),
                        fieldWithPath("currentBid").description(
                                "What bid are we playing"),
                        fieldWithPath("score").description("The current score"),
                        fieldWithPath("winnerLastTrick").description(
                                "Who won the last trick"),
                        fieldWithPath("lastTrick").description(
                                "See the last trick")
                ).andWithPrefix("currentBid.", bid)
                        .andWithPrefix("score.", score)
                        .andWithPrefix("nicknames.", nicknames)
                ))
    }
}