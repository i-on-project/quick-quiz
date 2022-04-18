package pt.isel.ps.qq.database

import pt.isel.ps.qq.data.User

class RepositoryUser(private val db: IDatabase): IRepository<User, String> {

    override fun getAll(): List<User> {
        TODO("Not yet implemented")
    }

    override fun get(id: String): User {
        TODO("Not yet implemented")
    }

    override fun create(entity: User): User {
        TODO("Not yet implemented")
    }

    override fun update(entity: User): Int {
        TODO("Not yet implemented")
    }

    override fun remove(id: String): Int {
        TODO("Not yet implemented")
    }

    override fun query(): List<User> {
        TODO("Not yet implemented")
    }
}