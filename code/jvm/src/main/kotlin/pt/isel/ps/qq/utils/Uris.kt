package pt.isel.ps.qq.utils

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
                }

                object Auth {
                    const val ENDPOINT = "/auth"
                    const val PATH = "${V1_0.PATH}$ENDPOINT"
                    fun make() = PATH

                    object CreateSession {
                        const val ENDPOINT = "/create_session"
                        const val PATH = "${Auth.PATH}$ENDPOINT"
                        fun make() = PATH
                    }
                }
            }
        }
    }
}
