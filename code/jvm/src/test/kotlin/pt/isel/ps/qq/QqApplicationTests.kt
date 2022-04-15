package pt.isel.ps.qq

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.util.Random
import java.util.UUID

//@SpringBootTest
class QqApplicationTests {

	@Test
	fun contextLoads() {
 		var uid = UUID.randomUUID().toString()
		val test = uid.hashCode()
	}

}
