package pt.isel.ps.qq.exceptions

class IllegalStatusException(
    private val reasonForUser: String,
    private val moreDetails: String,
    private val whereDidTheErrorOccurred: ErrorInstance
): ProblemJsonException(
    type = "IllegalStatusException",
    title = reasonForUser,
    status = 403,
    detail = moreDetails,
    instance = whereDidTheErrorOccurred
)
