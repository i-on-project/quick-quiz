package pt.isel.ps.qq.exceptions

class InvalidCredentialsException(val credentials: String? = null, message: String? = null): Exception(message)