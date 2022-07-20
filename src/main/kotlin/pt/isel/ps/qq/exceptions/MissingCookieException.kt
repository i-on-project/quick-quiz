package pt.isel.ps.qq.exceptions

class MissingCookieException(message: String? = null, val cookieName: String? = null): Exception(message)