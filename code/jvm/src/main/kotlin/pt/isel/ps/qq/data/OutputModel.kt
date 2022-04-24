package pt.isel.ps.qq.data

data class RegisterOutputModel(
    val userName: String,
    val displayName: String,
    val loginToken: String,
    val tokenExpireDate: Long,
    val status: String
)

/*data class RequestLoginOutputModel(

)*/
