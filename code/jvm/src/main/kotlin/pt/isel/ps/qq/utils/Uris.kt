package pt.isel.ps.qq.utils

import org.springframework.web.util.UriTemplate
import java.net.URI
import javax.servlet.http.HttpServletRequest

fun getBaseUrlHostFromRequest(request: HttpServletRequest): String {
    return "${request.scheme}://${request.serverName}:${request.serverPort}"
}

object Uris {

    object API {
        const val ENDPOINT = "/api"
        const val PATH = ENDPOINT

        object Web {
            const val ENDPOINT = "/web"
            const val PATH = "${API.PATH}$ENDPOINT"

            object V1_0 {
                const val ENDPOINT = "/v1.0"
                const val PATH = "${Web.PATH}$ENDPOINT"

                object NonAuth {
                    const val ENDPOINT = "/non_auth"
                    const val PATH = "${V1_0.PATH}$ENDPOINT"

                    object Register {
                        const val ENDPOINT = "/register"
                        const val PATH = "${NonAuth.PATH}$ENDPOINT"
                        fun make(): URI = URI.create(API.PATH)
                    }

                    object Login {
                        const val ENDPOINT = "/login"
                        const val PATH = "${NonAuth.PATH}$ENDPOINT"
                        fun make(): URI = URI.create(API.PATH)
                    }

                    object Logmein {
                        const val ENDPOINT = "/logmein"
                        const val PATH = "${NonAuth.PATH}$ENDPOINT"
                        fun make(): URI = URI.create(API.PATH)
                        fun url(host: String) = "${host}${PATH}"
                    }

                    object JoinSession {
                        const val ENDPOINT = "/join_session"
                        const val PATH = "${NonAuth.PATH}$ENDPOINT"
                    }
                    /*TODO: TESTING ONLY*/
                    object GetAllSession {
                        const val ENDPOINT = "/testgetallsessions"
                        const val PATH = "${NonAuth.PATH}$ENDPOINT"
                        fun make() = PATH
                    }

                    object GiveAnswer {
                        const val ENDPOINT = "/give_answer"
                        const val PATH = "${NonAuth.PATH}$ENDPOINT"
                    }
                }

                object Auth {
                    const val ENDPOINT = "/auth"
                    const val PATH = "${V1_0.PATH}$ENDPOINT"

                    object Session {
                        const val ENDPOINT = "/session"
                        const val PATH = "${Auth.PATH}$ENDPOINT"
                        fun url(host: String, page: Int) = "${host}${PATH}?page=${page}"

                        object Id {
                            const val ENDPOINT = "/{id}"
                            const val PATH = "${Session.PATH}$ENDPOINT"
                            const val CONTROLLER_ENDPOINT = "${Session.ENDPOINT}${ENDPOINT}"
                            private val TEMPLATE = UriTemplate(PATH)
                            fun make(id: String): URI = TEMPLATE.expand(mapOf("id" to id))
                            fun url(host: String, id: String) = "${host}${make(id)}"

                            object Live {
                                const val ENDPOINT = "/live"
                                const val PATH = "${Session.Id.PATH}$ENDPOINT"
                                const val CONTROLLER_ENDPOINT = "${Session.ENDPOINT}${Id.ENDPOINT}${ENDPOINT}"
                                private val TEMPLATE = UriTemplate(PATH)
                                fun make(id: String): URI = TEMPLATE.expand(mapOf("id" to id))
                                fun url(host: String, id: String) = "${host}${make(id)}"
                            }

                            object Close {
                                const val ENDPOINT = "/close"
                                const val PATH = "${Session.Id.PATH}$ENDPOINT"
                                const val CONTROLLER_ENDPOINT = "${Session.ENDPOINT}${Id.ENDPOINT}${ENDPOINT}"
                                private val TEMPLATE = UriTemplate(PATH)
                                fun make(id: String): URI = TEMPLATE.expand(mapOf("id" to id))
                                fun url(host: String, id: String) = "${host}${make(id)}"
                            }

                            object Quiz {
                                const val ENDPOINT = "/quiz"
                                const val PATH = "${Session.Id.PATH}$ENDPOINT"
                                const val CONTROLLER_ENDPOINT = "${Session.ENDPOINT}${Id.ENDPOINT}${ENDPOINT}"
                                private val TEMPLATE = UriTemplate(PATH)
                                fun make(id: String): URI = TEMPLATE.expand(mapOf("id" to id))
                                fun url(host: String, id: String) = "${host}${make(id)}"
                            }
                        }
                    }

                    object User {
                        const val ENDPOINT = "/user"
                        const val PATH = "${Auth.PATH}$ENDPOINT"

                        object Id {
                            const val ENDPOINT = "/{id}"
                            const val PATH = "${User.PATH}$ENDPOINT"
                            private val TEMPLATE = UriTemplate(PATH)
                            fun make(id: String): URI = TEMPLATE.expand(mapOf("id" to id))
                            fun url(host: String, id: String) = "${host}${make(id)}"
                        }
                    }

                    object Quiz {
                        const val ENDPOINT = "/quiz"
                        const val PATH = "${Auth.PATH}$ENDPOINT"
                        fun url(host: String) = "${host}${PATH}"

                        object Id {
                            const val ENDPOINT = "/{id}"
                            const val PATH = "${Quiz.PATH}$ENDPOINT"
                            const val CONTROLLER_ENDPOINT = "${Quiz.ENDPOINT}${ENDPOINT}"
                            private val TEMPLATE = UriTemplate(PATH)
                            fun make(id: String): URI = TEMPLATE.expand(mapOf("id" to id))
                            fun url(host: String, id: String) = "${host}${make(id)}"
                        }
                    }
                }
            }
        }
    }
}
