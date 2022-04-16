package pt.isel.ps.qq

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import pt.isel.ps.qq.data.dto.ErrorDto
import pt.isel.ps.qq.data.dto.UserDto


@SpringBootTest
@AutoConfigureMockMvc
class QqApplicationTests {

/*	@Autowired
	private val controller: GuestController? = null

	@LocalServerPort
	private val port = 0*/



	/*@Test
	@Throws(Exception::class)
	fun contextLoads() {
		assertThat(controller).isNotNull
	}*/

	@Autowired
	private val mockMvc: MockMvc? = null

	@Autowired
	private val mapper: ObjectMapper? = null

	@Test
	@Throws(Exception::class)
	fun `when API-Key is missing from header validation is FORBIDEN`()  {
		val expected = ErrorDto(HttpStatus.FORBIDDEN.value(), "Bad api-key")

		mockMvc!!.post("/register") {
			contentType = MediaType.APPLICATION_JSON
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			status { isForbidden() }
			//status { isBadRequest() }
			//content { contentType(MediaType.APPLICATION_JSON) }
			//content { json(mapper!!.writeValueAsString(expected)) }
		}
	}

	@Test
	@Throws(Exception::class)
	fun `when info is missing for Register validation is BAD REQUEST`()  {
		mockMvc!!.post("/register") {
			contentType = MediaType.APPLICATION_JSON
			header("API-KEY", "qwertyuiopasdfghjkzxcvbnm")
			content = mapper!!.writeValueAsString(UserDto("test"))
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			status { isBadRequest() }
		}
	}
}




