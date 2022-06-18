
import {goFetchNoHook} from "./FetchService";

import {useContext} from "react";

export const authService = () => {

    const login = (username, displayname) => {

    }
    const isLoggedIn = () => {

        console.log(`isLoggedInHandler: ${isLoggedInHandler()}`)
        return isLoggedInHandler() != null
    }
    const logout =  () => {
    }

    return {
        login: login,
        isLoggedIn: isLoggedIn,
        logout: logout

    }

}
const isLoggedInHandler = () => {
    let data = null
    let error = null
    goFetchNoHook("/api/web/v1.0/auth/user/checkuser",null, data, error)
    return data
}

