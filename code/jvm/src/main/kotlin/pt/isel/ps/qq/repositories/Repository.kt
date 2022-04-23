package pt.isel.ps.qq.repositories

abstract class Repository<T>(): IRepository<T> {

    override fun getAll(): List<T> {
        TODO("Not yet implemented")
    }

    override fun get(id: String): T {
        TODO("Not yet implemented")
    }

    override fun create(entity: T): String {
        TODO("Not yet implemented")
    }

    override fun update(entity: T): Int {
        TODO("Not yet implemented")
    }

    override fun remove(id: String): Int {
        TODO("Not yet implemented")
    }
}