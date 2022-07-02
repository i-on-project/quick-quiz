package pt.isel.ps.qq

import com.mongodb.MongoClientSettings
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException


@Configuration
class TestMongoConfig {
    @Autowired
    private val properties: MongoProperties? = null

    @Autowired(required = false)
    private val options: MongoClientSettings? = null

    @Bean(destroyMethod = "close")
    @Throws(IOException::class)
    fun mongo(mongodProcess: MongodProcess): Mongo? {
        val net: Net = mongodProcess.config.net()
        properties!!.host = net.getServerAddress().getHostName()
        properties.port = net.getPort()
        return properties.createMongoClient(options)
    }

    @Bean(destroyMethod = "stop")
    @Throws(IOException::class)
    fun mongodProcess(mongodExecutable: MongodExecutable): MongodProcess? {
        return mongodExecutable.start()
    }

    @Bean(destroyMethod = "stop")
    @Throws(IOException::class)
    fun mongodExecutable(mongodStarter: MongodStarter, iMongodConfig: IMongodConfig): MongodExecutable? {
        return mongodStarter.prepare(iMongodConfig)
    }

    @Bean
    @Throws(IOException::class)
    fun mongodConfig(): IMongodConfig? {
        return MongodConfigBuilder().version(Version.Main.PRODUCTION).build()
    }

    @Bean
    fun mongodStarter(): MongodStarter? {
        return MongodStarter.getDefaultInstance()
    }

}
