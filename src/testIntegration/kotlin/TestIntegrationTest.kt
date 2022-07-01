import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import pt.isel.ps.qq.repositories.UserRepository

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = ["classpath:application-integration-tests.properties"])
class TestIntegrationTest {

    private var userRepo: UserRepository? = null

    @BeforeEach
    @kotlin.Throws(java.lang.Exception::class)
    fun setUp() {
        userRepo = UserRepository()
    }

    @AfterEach
    @kotlin.Throws(java.lang.Exception::class)
    fun tearDown() {
    }


    @Test
    fun testSave() {
        fail("Not yet implemented")
    }

    @Test
    fun testFindByKey() {
        fail("Not yet implemented")
    }

}