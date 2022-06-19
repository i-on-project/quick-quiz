package pt.isel.ps.qq.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import pt.isel.ps.qq.data.docs.UserDoc

@Repository
interface UserRepository: MongoRepository<UserDoc, String>




