package pt.isel.ps.qq.data

data class User(
    val userName: String,
    val displayName: String,
    val id: String,
    val idExpiredDate: Long? = null
) {
    constructor(user: User, expiredDate: Long): this(user.userName, user.displayName, user.id, expiredDate)
    constructor(user: User, uid: String, expiredDate: Long): this(user.userName, user.displayName, uid, expiredDate)
}