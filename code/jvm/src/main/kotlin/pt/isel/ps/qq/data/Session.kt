package pt.isel.ps.qq.data

import pt.isel.ps.qq.data.dto.input.GeolocationInputModel
import pt.isel.ps.qq.data.dto.input.SessionInputModel

data class Quizzes(
    val todo: String
)

data class SessionState(
    val todo: String
) {
    companion object {
        @JvmStatic
        fun getInitialState(): SessionState {
            return SessionState("initialState")
        }
    }
}

data class Session(
    val guestCode: Int,
    val sessionOwner: String,
    val sessionId: String,
    val name: String,
    val limitOfParticipants: Int,
    val useGeolocation: Boolean,
    val geolocation: GeolocationInputModel?,
    val endDate: Long, //seconds
    val templates: List<Quizzes>,
    val state: SessionState
) {
    constructor(input: SessionInputModel, guestCode: Int, sessionOwner: String, sessionId: String, templates: List<Quizzes>, state: SessionState): this(
        name = input.name,
        limitOfParticipants = input.limitOfParticipants,
        useGeolocation = input.useGeolocation,
        geolocation = input.geolocation,
        endDate = input.endDate,
        guestCode = guestCode,
        sessionOwner = sessionOwner,
        sessionId = sessionId,
        templates = templates,
        state = state
    )
}