package pt.isel.ps.qq

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import pt.isel.ps.qq.repositories.*
import pt.isel.ps.qq.utils.Uris


@AutoConfigureDataMongo
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@AutoConfigureMockMvc

//@WebMvcTest
@TestPropertySource(locations = ["classpath:integration-tests.properties"])
class ApiIntegrationTests {

    @Autowired
    val mockMvc: MockMvc? = null

    @MockBean
    private val userRepo: UserRepository? = null
    @MockBean
    private val templateRepo: TemplateRepository? = null
    @MockBean
    private val sessionRepo: SessionRepository? = null
    @MockBean
    private val quizRepo: QuizRepository? = null
    @MockBean
    private val histRepo: HistoryRepository? = null
    @MockBean
    private val ansRepo: AnswersRepository? = null
    @MockBean
    private val mongoTemplate: MongoTemplate? = null

    @Test
    @Throws(Exception::class)
    fun isInSession() {
        val builder = Uris.API.Web.V1_0.NonAuth.IsInSession.ENDPOINT
        mockMvc
            ?.perform(get(builder))
            ?.andExpect(MockMvcResultMatchers.status().isNoContent)
            //?.andExpect(jsonPath("$.person.name").value("Jason"))

    }
}