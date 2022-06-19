package pt.isel.ps.qq.exceptions

import pt.isel.ps.qq.data.docs.QqStatus

open class ServiceException(message: String? = null): Exception(message)

open class BadInputModelException(message: String? = null): ServiceException(message)
open class QuizBadInputModelException(message: String? = null): BadInputModelException(message)
class SessionBadInputModelException(message: String? = null): BadInputModelException(message)
class UserBadInputModelException(message: String? = null): BadInputModelException(message)
class GuestSessionBadInputModelException(message: String? = null): BadInputModelException(message)

class AtLeast2Choices(message: String? = null): QuizBadInputModelException(message)
class AtLeast1CorrectChoice(message: String? = null): QuizBadInputModelException(message)

open class AlreadyExistsException(message: String? = null): ServiceException(message)
class UserAlreadyExistsException(message: String? = null): AlreadyExistsException(message)
class SessionAlreadyExistsException(message: String? = null): AlreadyExistsException(message)
class QuizAlreadyExistsException(message: String? = null): AlreadyExistsException(message)
class GuestSessionAlreadyExistsException(message: String? = null): AlreadyExistsException(message)

open class NotFoundException(message: String? = null): ServiceException(message)
class UserNotFoundException(message: String? = null): NotFoundException(message)
class SessionNotFoundException(message: String? = null): NotFoundException(message)
class QuizNotFoundException(message: String? = null, val session: String? = null): NotFoundException(message)
class TemplateNotFoundException(message: String? = null): NotFoundException(message)
class GuestSessionNotFoundException(message: String? = null): NotFoundException(message)

open class AuthenticationException(message: String? = null): ServiceException(message)
class InvalidTokenException(message: String? = null): AuthenticationException(message)
class TokenExpiredException(message: String? = null): AuthenticationException(message)
class UserDisabledException(message: String? = null): AuthenticationException(message)
class PendingValidationException(message: String? = null): AuthenticationException(message)

class ImpossibleGenerationException(message: String? = null): ServiceException(message)

open class AuthorizationException(message: String? = null): ServiceException(message)
class SessionAuthorizationException(message: String? = null): AuthorizationException(message)
class QuizAuthorizationException(message: String? = null): AuthorizationException(message)
class TemplateAuthorizationException(message: String? = null): AuthorizationException(message)

open class IllegalStatusOperationException(val status: QqStatus, message: String? = null): ServiceException(message)
open class SessionIllegalStatusOperationException(status: QqStatus, message: String? = null): IllegalStatusOperationException(status, message)

