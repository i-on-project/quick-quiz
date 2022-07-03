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
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import pt.isel.ps.qq.data.SirenModel
import pt.isel.ps.qq.repositories.docs.QqStatus
import pt.isel.ps.qq.repositories.docs.QuestionType
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
        var sessionId: String? = null,
        var shortQuizId: String? = null,
        var longQuizId: String? = null,
        var multiQuizId: String? = null,
        var guestCode: String? = null,
        var participantId: String? = null
    )

    companion object {
        var testUser: TestUser? = null
    }

    fun postRequestWithCookie(path: String, body: MutableMap<String, Any?>, cookie: Cookie?): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders.post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
    }

    fun putRequestWithCookie(path: String, body: MutableMap<String, Any?>, cookie: Cookie?): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders.put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
    }

    fun deleteRequestWithCookie(path: String, cookie: Cookie?): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders.delete(path)
                .contentType(MediaType.APPLICATION_JSON)

                .accept(MediaType.APPLICATION_JSON)
                .cookie(cookie)
        )
    }

    fun postRequestNoCookie(path: String, body: MutableMap<String, Any?>): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders.post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
                .accept(MediaType.APPLICATION_JSON)
        )
    }

    fun getRequestNoCookie(path: String): ResultActions {
        return mockMvc.perform(get(path))
    }

    fun getRequestWithCookie(path: String, cookie: Cookie?): ResultActions {
        return mockMvc.perform(get(path).cookie(cookie))
    }

    fun expectSirenMediaType(resultActions: ResultActions): ResultActions{
        return resultActions.andExpect(content().contentType(SirenModel.MEDIA_TYPE))
    }

    @Order(99)
    @Test
    @Throws(Exception::class)
    fun isNotInSessionAndBodyIsEmpty() {
        val path = Uris.API.Web.V1_0.NonAuth.IsInSession.PATH

        getRequestNoCookie(path)
            .andExpect(cookie().doesNotExist("InSession"))
            .andExpect(status().isNoContent)
            .andExpect(content().string(""))
    }

    @Order(100)
    @Test
    @Throws(Exception::class)
    fun isInSessionAndReceivesSessionId() {
        val path = Uris.API.Web.V1_0.NonAuth.IsInSession.PATH

        val cookie = Cookie("InSession", "TestSessionId")

        getRequestWithCookie(path, cookie)
            .andExpect(status().isOk)
            .andExpect(content().string("TestSessionId"))
    }


    @Order(1)
    @Test
    @Throws(Exception::class)
    fun isAbleToRegisterUser() {
        val path = Uris.API.Web.V1_0.NonAuth.Register.PATH
        val body: MutableMap<String, Any?> = HashMap()
        body["userName"] = "apitest@test.org"
        body["displayName"] = "Tester"

        mongo.db.drop()

        val result = expectSirenMediaType(postRequestNoCookie(path, body))
            .andExpect(status().isOk)
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
        val body: MutableMap<String, Any?> = HashMap()
        body["userName"] = testUser?.user.toString()
        body["loginToken"] = testUser?.registrationToken.toString()

        val result = expectSirenMediaType(postRequestNoCookie(path, body))
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
        expectSirenMediaType(getRequestWithCookie(path, testUser?.userCookie))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.properties.size").value("0"))

    }


    @Order(4)
    @Test
    @Throws(Exception::class)
    fun createSession() {
        val path = Uris.API.Web.V1_0.Auth.Session.PATH
        val body: MutableMap<String, Any?> = HashMap()
        body["name"] = "Session1"
        body["description"] = "Session1"
        body["limitOfParticipants"] = "100"
        body["tags"] = arrayOf("session1_tags")

        val result = expectSirenMediaType(postRequestWithCookie(path, body, testUser?.userCookie))
            .andExpect(status().isCreated)
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
    fun getSessionsOneSession() {
        val path = Uris.API.Web.V1_0.Auth.Session.PATH
        expectSirenMediaType(getRequestWithCookie(path, testUser?.userCookie))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.properties.size").value("1"))
    }

    @Order(6)
    @Test
    @Throws(Exception::class)
    fun createShortAnswerQuizForSession() {
        val path = "/api/web/v1.0/auth/sessions/${testUser?.sessionId}/quiz"
        val body: MutableMap<String, Any?> = HashMap()
        body["order"] = "1"
        body["question"] = "Short answer question?"
        body["questionType"] = QuestionType.SHORT.toString()


        val result = expectSirenMediaType(postRequestWithCookie(path, body, testUser?.userCookie))
            .andExpect(status().isCreated)
            .andReturn()

        val responseObj = objectMapper.readValue(result.response.contentAsByteArray, SirenModel::class.java)
        val properties = responseObj.properties as Map<String, String>
        testUser?.shortQuizId = properties["id"]
    }

    @Order(7)
    @Test
    @Throws(Exception::class)
    fun createLongAnswerQuizForSession() {
        val path = "/api/web/v1.0/auth/sessions/${testUser?.sessionId}/quiz"
        val body: MutableMap<String, Any?> = HashMap()
        body["order"] = "2"
        body["question"] = "Long answer question?"
        body["questionType"] = QuestionType.LONG.toString()


        val result = expectSirenMediaType(postRequestWithCookie(path, body, testUser?.userCookie))
            .andExpect(status().isCreated)
            .andReturn()

        val responseObj = objectMapper.readValue(result.response.contentAsByteArray, SirenModel::class.java)
        val properties = responseObj.properties as Map<String, String>
        testUser?.longQuizId = properties["id"]
    }

    @Order(8)
    @Test
    @Throws(Exception::class)
    fun createMultiAnswerQuizForSession() {
        val path = "/api/web/v1.0/auth/sessions/${testUser?.sessionId}/quiz"
        val body: MutableMap<String, Any?> = HashMap()
        body["order"] = "1"
        body["question"] = "Multi answer question?"
        body["questionType"] = QuestionType.MULTIPLE_CHOICE.toString()
        body["choices"] = listOf(
            mapOf( "choiceNumber" to "1", "choiceAnswer" to "yes", "choiceRight" to "true"),
            mapOf( "choiceNumber" to "2", "choiceAnswer" to "no", "choiceRight" to "false"),
        )


        val result = expectSirenMediaType(postRequestWithCookie(path, body, testUser?.userCookie))
            .andExpect(status().isCreated)
            .andReturn()

        val responseObj = objectMapper.readValue(result.response.contentAsByteArray, SirenModel::class.java)
        val properties = responseObj.properties as Map<String, String>
        testUser?.multiQuizId = properties["id"]
    }

    @Order(9)
    @Test
    @Throws(Exception::class)
    fun getAllQuizzesForSession() {
        val path = "/api/web/v1.0/auth/quiz/session/${testUser?.sessionId}"
        expectSirenMediaType(getRequestWithCookie(path, testUser?.userCookie))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.properties.total").value("3"))

    }

    @Order(10)
    @Test
    @Throws(Exception::class)
    fun editShortQuiz() {
        val path = "/api/web/v1.0/auth/quiz/${testUser?.shortQuizId}"
        val body: MutableMap<String, Any?> = HashMap()
        body["order"] = "4"
        body["question"] = "Short answer question edited?"

        val result = expectSirenMediaType(putRequestWithCookie(path, body, testUser?.userCookie))
            .andExpect(status().isOk)
            .andReturn()
    }

    @Order(11)
    @Test
    @Throws(Exception::class)
    fun removeLongQuiz() {
        val path = "/api/web/v1.0/auth/quiz/${testUser?.longQuizId}"

        val result = expectSirenMediaType(deleteRequestWithCookie(path,  testUser?.userCookie))
            .andExpect(status().isOk)
            .andReturn()

        testUser?.longQuizId = null
    }

    @Order(12)
    @Test
    @Throws(Exception::class)
    fun updateRemainingQuizzesStatus() {
        var path = "/api/web/v1.0/auth/quiz/${testUser?.shortQuizId}/updatestatus"
        val body: MutableMap<String, Any?> = HashMap()
        body["quizState"] = QqStatus.STARTED.toString()

        expectSirenMediaType(putRequestWithCookie(path, body, testUser?.userCookie))
            .andExpect(status().isOk)
            .andReturn()

        path = "/api/web/v1.0/auth/quiz/${testUser?.multiQuizId}/updatestatus"
        expectSirenMediaType(putRequestWithCookie(path, body, testUser?.userCookie))
            .andExpect(status().isOk)
            .andReturn()
    }

    @Order(13)
    @Test
    @Throws(Exception::class)
    fun getShortQuizDetails() {
        val path = "/api/web/v1.0/auth/quiz/${testUser?.shortQuizId}"

        val result = expectSirenMediaType(getRequestWithCookie(path,  testUser?.userCookie))
            .andExpect(status().isOk)
            .andReturn()
    }

    @Order(14)
    @Test
    @Throws(Exception::class)
    fun updateSessionStatus() {
        var path = "/api/web/v1.0/auth/sessions/${testUser?.sessionId}/live"
        val body: MutableMap<String, Any?> = HashMap()

        val result = expectSirenMediaType(postRequestWithCookie(path, body, testUser?.userCookie))
            .andExpect(status().isOk)
            .andReturn()

        val responseObj = objectMapper.readValue(result.response.contentAsByteArray, SirenModel::class.java)
        val properties = responseObj.properties as Map<String, String>
        testUser?.guestCode = properties["guestCode"]
    }

    @Order(15)
    @Test
    @Throws(Exception::class)
    fun editSessionDetails() {
        val path = "/api/web/v1.0/auth/sessions/${testUser?.sessionId}"
        val body: MutableMap<String, Any?> = HashMap()
        body["name"] = "Session1 edited"
        body["description"] = "Session1 edited"
        body["limitOfParticipants"] = "99"
        body["tags"] = arrayOf("session1_tags", "One_More_Tag")

        expectSirenMediaType(putRequestWithCookie(path, body, testUser?.userCookie))
            .andExpect(status().isOk)
            .andReturn()
    }
    @Order(16)
    @Test
    @Throws(Exception::class)
    fun getSessionDetails() {
        val path =  "/api/web/v1.0/auth/sessions/${testUser?.sessionId}"

        val result = expectSirenMediaType(getRequestWithCookie(path,  testUser?.userCookie))
            .andExpect(status().isOk)
            .andReturn()
    }

    @Order(17)
    @Test
    @Throws(Exception::class)
    fun `get all participants and its answers`() {
        val path =  "/api/web/v1.0/auth/sessions/${testUser?.sessionId}/answers"

        expectSirenMediaType(getRequestWithCookie(path,  testUser?.userCookie))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.properties.total").value("0"))
            .andReturn()

    }

    @Order(18)
    @Test
    @Throws(Exception::class)
    fun participantJoinsSession() {
        val path = Uris.API.Web.V1_0.NonAuth.JoinSession.PATH
        val body: MutableMap<String, Any?> = HashMap()
        body["sessionCode"] = testUser?.guestCode
        val result = expectSirenMediaType(postRequestNoCookie(path, body))
            .andExpect(status().isOk)
            .andExpect(cookie().exists("InSession"))
            .andReturn()

        val responseObj = objectMapper.readValue(result.response.contentAsByteArray, SirenModel::class.java)
        val properties = responseObj.properties as Map<String, String>
        testUser?.participantId = properties["participantId"]
    }

    @Order(19)
    @Test
    @Throws(Exception::class)
    fun getParticipantDetails() {
        val path =  "/api/web/v1.0/non_auth/answer/${testUser?.participantId}"

        val result = expectSirenMediaType(getRequestNoCookie(path))
            .andExpect(status().isOk)
            .andReturn()
    }

    @Order(20)
    @Test
    @Throws(Exception::class)
    fun getQuizzesForParticipantSession() {
        val path =  "/api/web/v1.0/non_auth/quiz/session/${testUser?.participantId}"

        val result = expectSirenMediaType(getRequestNoCookie(path))
            .andExpect(status().isOk)
            .andReturn()
    }

    @Order(21)
    @Test
    @Throws(Exception::class)
    fun isAbleToAnswerQuizzes() {
        val path = Uris.API.Web.V1_0.NonAuth.GiveAnswer.PATH
        val body: MutableMap<String, Any?> = HashMap()
        body["guestId"] = testUser?.participantId
        body["sessionId"] = testUser?.sessionId
        body["quizId"] = testUser?.shortQuizId
        body["answer"] = "A short answer"

        expectSirenMediaType(postRequestNoCookie(path, body))
            .andExpect(status().isOk)
            .andReturn()

        body["guestId"] = testUser?.participantId
        body["sessionId"] = testUser?.sessionId
        body["quizId"] = testUser?.multiQuizId
        body["answer"] = null
        body["answerChoice"] = "1"

        expectSirenMediaType(postRequestNoCookie(path, body))
            .andExpect(status().isOk)
            .andReturn()
    }

    @Order(22)
    @Test
    @Throws(Exception::class)
    fun getAllAnswersForSessionWithParticipant() {
        val path =  "/api/web/v1.0/auth/sessions/${testUser?.sessionId}/answers"

        val result = expectSirenMediaType(getRequestWithCookie(path,  testUser?.userCookie))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.properties.total").value("1"))
            .andExpect(jsonPath("$.entities[0].properties.answers").isArray )
            .andReturn()
    }

    @Order(23)
    @Test
    @Throws(Exception::class)
    fun isAbleToCloseSession() {
        val path =  "/api/web/v1.0/auth/sessions/${testUser?.sessionId}/close"
        val body: MutableMap<String, Any?> = HashMap()

        val result = expectSirenMediaType(postRequestWithCookie(path, body ,testUser?.userCookie))
            .andExpect(status().isOk)
            .andReturn()
    }


}




















































