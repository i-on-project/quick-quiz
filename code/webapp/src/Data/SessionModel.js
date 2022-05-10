module.exports = {

    Session: function Session(name, description, limitOfParticipants, geolocation, templateId) {
            this.name =	name
            this.description = description
            this.limitOfParticipants = limitOfParticipants
            this.geolocation = geolocation
            this.templateId = templateId
    }
}

/*
data class SessionInputModel(
    val name: String,
    val description: String? = null,
    val limitOfParticipants: Int?,
    val geolocation: String?,
    val templateId: String? = null
)*/
