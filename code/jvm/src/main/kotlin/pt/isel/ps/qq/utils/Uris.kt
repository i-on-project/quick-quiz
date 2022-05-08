package pt.isel.ps.qq.utils

import org.springframework.web.util.UriTemplate

object Uris {

    object API {
        const val ENDPOINT = "/api"
        const val PATH = ENDPOINT
        fun make() = PATH

        object Web {
            const val ENDPOINT = "/web"
            const val PATH = "${API.PATH}$ENDPOINT"
            fun make() = PATH

            object V1_0 {
                const val ENDPOINT = "/v1.0"
                const val PATH = "${Web.PATH}$ENDPOINT"
                fun make() = PATH

                object NonAuth {
                    const val ENDPOINT = "/non_auth"
                    const val PATH = "${V1_0.PATH}$ENDPOINT"
                    fun make() = PATH

                    object Register {
                        const val ENDPOINT = "/register"
                        const val PATH = "${NonAuth.PATH}$ENDPOINT"
                        fun make() = PATH
                    }

                    object Login {
                        const val ENDPOINT = "/login"
                        const val PATH = "${NonAuth.PATH}$ENDPOINT"
                        fun make() = PATH
                    }

                    object Logmein {
                        const val ENDPOINT = "/logmein"
                        const val PATH = "${NonAuth.PATH}$ENDPOINT"
                        fun make() = PATH
                    }

                    object JoinSession {
                        const val ENDPOINT = "/join_session"
                        const val PATH = "${NonAuth.PATH}$ENDPOINT"
                        fun make() = PATH
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
                        fun make() = PATH
                    }
                }

                object Auth {
                    const val ENDPOINT = "/auth"
                    const val PATH = "${V1_0.PATH}$ENDPOINT"
                    fun make() = PATH

                    object Session {
                        const val ENDPOINT = "/session"
                        const val PATH = "${Auth.PATH}$ENDPOINT"
                        fun make() = PATH

                        object Id {
                            const val CONTROLLER_ENDPOINT = "/session/{id}"
                            const val ENDPOINT = "/{id}"
                            const val PATH = "${Session.PATH}$ENDPOINT"
                            private val TEMPLATE = UriTemplate(PATH)
                            fun make(id: String) = TEMPLATE.expand(mapOf("id" to id))
                        }
                    }

                    object User {
                        const val ENDPOINT = "/user"
                        const val PATH = "${Auth.PATH}$ENDPOINT"
                        fun make() = PATH

                        object Id {
                            const val ENDPOINT = "/{id}"
                            const val PATH = "${User.PATH}$ENDPOINT"
                            private val TEMPLATE = UriTemplate(PATH)
                            fun make(id: String) = TEMPLATE.expand(mapOf("id" to id))
                        }
                    }

                    object Quiz {
                        const val ENDPOINT = "/quiz"
                        const val PATH = "${Auth.PATH}$ENDPOINT"
                        fun make() = PATH

                        object Id {
                            const val CONTROLLER_ENDPOINT = "/quiz/{id}"
                            const val ENDPOINT = "/{id}"
                            const val PATH = "${Quiz.PATH}$ENDPOINT"
                            private val TEMPLATE = UriTemplate(PATH)
                            fun make(id: String) = TEMPLATE.expand(mapOf("id" to id))
                        }
                    }













                    object CreateSession {
                        const val ENDPOINT = "/create_session"
                        const val PATH = "${Auth.PATH}$ENDPOINT"
                        fun make() = PATH
                    }

                    object DeleteSession {
                        const val ENDPOINT = "/delete_session/{id}"
                        const val PATH = "${Auth.PATH}$ENDPOINT"
                        private val TEMPLATE = UriTemplate(PATH)
                        fun make(id: String) = TEMPLATE.expand(mapOf("id" to id))
                    }
                }
            }
        }
    }
}
