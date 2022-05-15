import {goPOST, goGET} from "./FetchService";
import {Session} from "../Data/SessionModel";

const GETALLSESSIONS_URI = "/api/web/v1.0/auth/sessions"
const GETSESSION_URI = ""
const CREATESESSION_URI = "/api/web/v1.0/auth/session"

/*data class SessionInputModel(
    val name: String,
    val description: String? = null,
    val limitOfParticipants: Int?,
    val geolocation: String?,
    val templateId: String? = null
)*/

export const getAllSessions = (setData, setError) => {
    goGET(GETALLSESSIONS_URI, setData, setError)
}


export const getSession = (uri, setData, setError) => {
    goGET(uri, setData, setError)
}

export const createSession = (data, setData, setError) => {
    const submitData = new Session(data.name,data.description,data.limitOfParticipants, data.geolocation, data.templateId)
    //const submitData = {name: data.name,description: data.description, limitOfParticipants: data.limitOfParticipants)
    goPOST(CREATESESSION_URI, submitData, setData, setError)
}