package pt.isel.ps.qq

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [EmbeddedMongoAutoConfiguration::class])
class QqApplication
fun main(args: Array<String>) {
	runApplication<QqApplication>(*args)
}
