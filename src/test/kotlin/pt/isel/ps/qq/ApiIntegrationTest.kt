package pt.isel.ps.qq

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import pt.isel.ps.qq.data.SirenModel
import pt.isel.ps.qq.utils.Uris
import javax.servlet.http.Cookie


@AutoConfigureDataMongo
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ApiIntegrationTest() {
    @Autowired
    private lateinit var mongo: MongoTemplate

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper


    data class TestUser(
        var user: String?,
        var registrationToken: String?,
        var loginToken: String? = null,
        var userCookie: Cookie? = null,
        var sessionId: String? = null
    )

    companion object {
        var testUser: TestUser? = null
    }

    @Order(99)
    @Test
    @Throws(Exception::class)
    fun isNotInSessionAndBodyIsEmpty() {
        val builder = Uris.API.Web.V1_0.NonAuth.IsInSession.PATH
        mockMvc.perform(get(builder))
            .andExpect(cookie().doesNotExist("InSession"))
            .andExpect(status().isNoContent)
            .andExpect(content().string(""))
    }

    @Order(100)
    @Test
    @Throws(Exception::class)
    fun isInSessionAndReceivesSessionId() {
        val builder = Uris.API.Web.V1_0.NonAuth.IsInSession.PATH

        val cookie = Cookie("InSession", "TestSessionId")

        mockMvc.perform(get(builder).cookie(cookie))
            .andExpect(status().isOk)
            .andExpect(content().string("TestSessionId"))
    }


    @Order(1)
    @Test
    @Throws(Exception::class)
    fun isAbleToRegisterUser() {
        val path = Uris.API.Web.V1_0.NonAuth.Register.PATH
        val body: MutableMap<String, Any> = HashMap()
        body["userName"] = "apitest@test.org"
        body["displayName"] = "Tester"

        mongo.db.drop()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(SirenModel.MEDIA_TYPE))
            .andExpect(jsonPath("$.class[0]").value("Register"))
            .andExpect(jsonPath("$.title").value("Check your email"))
            .andExpect(jsonPath("$.actions[0].name").value("Logmein"))
            .andExpect(jsonPath("$.properties.userName").value("apitest@test.org"))
            .andReturn()

        val responseObj = objectMapper.readValue(result.response.contentAsByteArray, SirenModel::class.java)
        val properties = responseObj.properties as Map<String, String>
        testUser = TestUser(user = properties["userName"], registrationToken = properties["token"])
    }


    @Order(2)
    @Test
    @Throws(Exception::class)
    fun isAbleToConfirmRegistration() {
        val path = Uris.API.Web.V1_0.NonAuth.Logmein.PATH
        val body: MutableMap<String, Any> = HashMap()
        body["userName"] = testUser?.user.toString()
        body["loginToken"] = testUser?.registrationToken.toString()

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(cookie().exists("Authorization"))
            .andReturn()

        val responseObj = objectMapper.readValue(result.response.contentAsByteArray, SirenModel::class.java)
        testUser?.userCookie = result.response.getCookie("Authorization")
        testUser?.registrationToken = null
        testUser?.loginToken = result.response.getCookie("Authorization")?.value.toString()
    }

    @Order(3)
    @Test
    @Throws(Exception::class)
    fun getSessionsNoSessions() {
        val path = Uris.API.Web.V1_0.Auth.Session.PATH
        mockMvc.perform(get(path).cookie(testUser?.userCookie))
            .andExpect(status().isOk)
            .andExpect(content().contentType(SirenModel.MEDIA_TYPE))
            .andExpect(jsonPath("$.properties.size").value("0"))

    }

    @Order(4)
    @Test
    @Throws(Exception::class)
    fun createSession() {
        val path = Uris.API.Web.V1_0.Auth.Session.PATH
        val body: MutableMap<String, Any> = HashMap()
        body["name"] = "Session1"
        body["description"] = "Session1"
        body["limitOfParticipants"] = "100"
        body["tags"] = arrayOf("session1_tags")

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .accept(MediaType.APPLICATION_JSON)
                .cookie(testUser?.userCookie)
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(SirenModel.MEDIA_TYPE))
            .andExpect(jsonPath("$.class[0]").value("CreateSession"))
            .andExpect(jsonPath("$.properties.name").value(body["name"]))
            .andExpect(jsonPath("$.properties.description").value(body["description"]))
            .andExpect(jsonPath("$.properties.limitOfParticipants").value(body["limitOfParticipants"]))
            .andExpect(jsonPath("$.properties.owner").value(testUser?.user))
            .andExpect(jsonPath("$.properties.tags[0]").value("session1_tags"))
            .andReturn()

        val responseObj = objectMapper.readValue(result.response.contentAsByteArray, SirenModel::class.java)
        val properties = responseObj.properties as Map<String, String>
        testUser?.sessionId = properties["id"]
    }

    @Order(5)
    @Test
    @Throws(Exception::class)
    fun getSessionsOneSessions() {
        val path = Uris.API.Web.V1_0.Auth.Session.PATH
        mockMvc.perform(get(path).cookie(testUser?.userCookie))
            .andExpect(status().isOk)
            .andExpect(content().contentType(SirenModel.MEDIA_TYPE))
            .andExpect(jsonPath("$.properties.size").value("1"))
    }
}