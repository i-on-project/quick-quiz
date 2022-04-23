package pt.isel.ps.qq.data.elasticdocs

import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName = "guest_sessions")
data class SessionGuestDoc(
    val id: String,
    val sessionId: String,
    val guestId: String,
    val answers: List<String> = emptyList()
)