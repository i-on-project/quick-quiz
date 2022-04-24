package pt.isel.ps.qq.exceptions

open class NotFoundException(
    private val notFoundWhat: String,
    private val reasonForUser: String,
    private val moreDetails: String,
    private val whereDidTheErrorOccurred: ErrorInstance
): ProblemJsonException(
    type = "${notFoundWhat}NotFoundException",
    title = reasonForUser,
    status = 404,
    detail = moreDetails,
    instance = whereDidTheErrorOccurred
)