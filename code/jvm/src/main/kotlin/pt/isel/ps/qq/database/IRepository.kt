package pt.isel.ps.qq.database

interface IRepository<T,K> {

    fun getAll(): List<T>
    fun get(id: K): T
    fun create(entity: T): T
    fun update(entity: T): Int
    fun remove(id: K): Int
    fun query(): List<T> //Todo: Define parameters
}