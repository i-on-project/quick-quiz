package pt.isel.ps.qq.exceptions

open class AlreadyExistsException(
    private val alreadyExistsWhat: String,
    private val reasonForUser: String,
    private val moreDetails: String,
    private val whereDidTheErrorOccurred: ErrorInstance
): ProblemJsonException(
    type = "${alreadyExistsWhat}AlreadyExistsException",
    title = reasonForUser,
    status = 400,
    detail = moreDetails,
    instance = whereDidTheErrorOccurred
)