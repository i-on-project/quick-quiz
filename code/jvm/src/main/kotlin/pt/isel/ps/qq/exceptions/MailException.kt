package pt.isel.ps.qq.exceptions

class ServerMailException(emailTo: String, whereDidTheErrorOccurred: ErrorInstance): ProblemJsonException(
    type = "ServerMailException",
    title = "It was not possible to send an email to: $emailTo.",
    status = 500,
    detail = "Our email service is currently unavailable, please try again later. If the issue remains contact our support team.",
    instance = whereDidTheErrorOccurred
)

class InvalidMailException(emailTo: String, whereDidTheErrorOccurred: ErrorInstance): ProblemJsonException(
    type = "InvalidMailException",
    title = "It was not possible to send an email to: $emailTo.",
    status = 400,
    detail = "Verify if your email is correct and try again",
    instance = whereDidTheErrorOccurred
)