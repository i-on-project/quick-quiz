package pt.isel.ps.qq.repositories.docs

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import pt.isel.ps.qq.data.EditSessionInputModel
import pt.isel.ps.qq.data.SessionInputModel
import pt.isel.ps.qq.utils.getCurrentTimeSeconds

@Document(collection  = "sessions")
data class SessionDoc(
    @Id
    val id: String,
    val name: String,
    val description: String? = null,
    val creationDate: Long = getCurrentTimeSeconds(),
    val owner: String,
    val guestCode: Int? = null,
    val limitOfParticipants: Int = 10,
    val geolocation: String? = null,
    val radius: Int? = null,
    val radiusUnit: String? = null,
    val liveDate: Long? = null,
    val status: QqStatus,
    val tags: MutableList<String> = mutableListOf()
) {
    constructor(session: SessionDoc, input: EditSessionInputModel): this(
        id = session.id,
        name = input.name ?: session.name,
        description = input.description ?: session.description,
        creationDate = session.creationDate,
        owner = session.owner,
        guestCode = session.guestCode,
        limitOfParticipants = input.limitOfParticipants ?: session.limitOfParticipants,
        geolocation = input.geolocation ?: session.geolocation,
        radius = input.radius,
        radiusUnit = session.radiusUnit,
        liveDate = session.liveDate,
        status = session.status,
        tags = input.tags.toMutableList()

    )

    constructor(template: TemplateDoc, id: String, input: SessionInputModel): this(
        id = id,
        name = input.name,
        description = input.description,
        creationDate = getCurrentTimeSeconds(),
        owner = template.owner,
        limitOfParticipants = input.limitOfParticipants ?: template.limitOfParticipants ?: 10,
        geolocation = input.geolocation ?: template.geolocation,
        status = QqStatus.NOT_STARTED,

    )

    constructor(session: SessionDoc, status: QqStatus, guestCode: Int?): this(
        id = session.id,
        name = session.name,
        liveDate = getCurrentTimeSeconds(),
        description = session.description,
        owner = session.owner,
        limitOfParticipants = session.limitOfParticipants,
        geolocation = session.geolocation ,
        status = status,
        guestCode = guestCode,
        tags = session.tags

    )
}

enum class QqStatus {
    NOT_STARTED, STARTED, CLOSED
}

