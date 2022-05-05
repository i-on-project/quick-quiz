package pt.isel.ps.qq.data.elasticdocs

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import pt.isel.ps.qq.data.EditSessionInputModel
import pt.isel.ps.qq.utils.getCurrentTimeSeconds

@Document(indexName = "sessions")
data class SessionDoc(
    @Id @Field(type = FieldType.Keyword, fielddata = true)
    val id: String,
    val name: String,
    val description: String? = null,
    val creationDate: Long = getCurrentTimeSeconds(),
    val owner: String,
    val guestCode: Int,
    val limitOfParticipants: Int = 0,
    val geolocation: String? = null,
    val radius: Double? = null,
    val radiusUnit: String? = null,
    val liveDate: Long? = null,
    val quizzes: List<String> = emptyList(),
    val status: QqStatus,
    var numberOfParticipants: Int = 0

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
        radius = session.radius,
        radiusUnit = session.radiusUnit,
        liveDate = session.liveDate,
        quizzes = session.quizzes,
        status = session.status,
        numberOfParticipants = session.numberOfParticipants
    )
}

enum class QqStatus {
    NOT_STARTED, STARTED, CLOSED
}

