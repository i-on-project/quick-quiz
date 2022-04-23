package pt.isel.ps.qq.repositories

interface IRepository<T> {

    fun getAll(): List<T>
    fun get(id: String): T
    fun create(entity: T): String
    fun update(entity: T): Int
    fun remove(id: String): Int
   // fun query(): List<T> //Todo: Define parameters

}