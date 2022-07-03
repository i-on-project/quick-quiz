import * as React from "react";
import {useEffect, useState} from "react";
import {request} from "../utils/Request";

export const UserContext = React.createContext(null)

const uri = '/api/web/v1.0/auth/checkuser'
export const UserProvider = ({children}) => {

    const [context, setContext] = useState({
        username: null,
        display_name: null,
        loading: true,
        logged_in: false,
        error: null
    })

    useEffect(() => {

        const success_func = (data) => {
            setContext((prev) => {
                return{...prev,
                    username: data.properties.userName,
                    display_name: data.properties.displayName,
                    loading: false,
                    logged_in: true
                }
            })
        }

        const failed_func = (problem) => {
            setContext((prev) => {
                return{...prev,
                    loading: false,
                    error: problem
                }
            })
        }

        return request(uri, {method: 'GET'}, {success: success_func, failed: failed_func}).cancel
    }, [])

    return (
        <UserContext.Provider value={[context, setContext]}>
            {children}
        </UserContext.Provider>
    )
}