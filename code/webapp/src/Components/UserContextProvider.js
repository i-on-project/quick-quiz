import React, {useEffect, useState} from 'react';
import {goPOST, goGET} from "../Services/FetchService";

const func = () => {
}


const appUser = {
    userName: null,
    displayName: null,
    isLoading: false,
    updateUser: func,
}

export const UserContext = React.createContext(appUser)

export const UserContextProvider = ({children}) => {
    const [context, setContext] = useState(appUser)

    const updateContext = (contextUpdates = {}) => {
        setContext(currentContext => ({...currentContext, ...contextUpdates}))
    }


    useEffect(() => {
        if (appUser.userName === null) {
            const setData = (data) => {
                if (data !== null) {
                    updateContext({
                        userName: data.properties.userName,
                        displayName: data.properties.displayName,
                        isLoading: false
                    })
                }
            }
            updateContext({isLoading: true})
            const setError = (error) => {
                console.log(`I'm Error Fetched in COntext: ${error}`)
                updateContext({ isLoading: false })
            }
            goGET("/api/web/v1.0/auth/checkuser", setData, setError)
        }

    }, [appUser.userName])

    useEffect(() => {
        if (context.updateUser === func) {
            updateContext({
                updateUser: (user, display) => updateContext({userName: user, displayName: display}),
            })
        }
    }, [context.updateUser])

    return (
        <UserContext.Provider value={context}>
            {children}
        </UserContext.Provider>
    )
}

