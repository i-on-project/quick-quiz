import React, {Fragment, useEffect, useState} from 'react';
import {goPOST, goGET} from "../Services/FetchService";

const func = () => {
}


const appUser = {
    userName: null,
    displayName: null,
    isLoading: false,
    inSession: null,
    updateUser: func,
}

export const UserContext = React.createContext(appUser)

export const UserContextProvider = ({children}) => {
    const [context, setContext] = useState(appUser)

    const updateContext = (contextUpdates = {}) => {
        setContext(currentContext => ({...currentContext, ...contextUpdates}))
    }
    const checkCookie = () => {
        const answerCookie = getCookie('InSession')
        console.log(`Answer Cookie: ${answerCookie}`)
        if (answerCookie !== null) {

            updateContext({
                inSession: answerCookie
            })
        }
    }
//https://www.w3schools.com/js/js_cookies.asp
    const getCookie = (cname) => {
        let name = cname + "=";
        let decodedCookie = decodeURIComponent(document.cookie);
        let ca = decodedCookie.split(';');
        for (let i = 0; i < ca.length; i++) {
            let c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length, c.length);
            }
        }
        return null;
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
                //console.log(`I'm Error Fetched in COntext: ${error}`)
                updateContext({isLoading: false})
            }
            checkCookie()
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

