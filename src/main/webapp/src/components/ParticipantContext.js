import * as React from "react";
import {useCallback, useEffect, useState} from "react";

export const ParticipantContext = React.createContext(null)

const cookie_name = 'InSession'
export const ParticipantProvider = ({children}) => {

    const [context, setContext] = useState({participant_id: null})

    const obtain_cookie = useCallback((name) => {
        const cookie = name + "=";
        const decodedCookie = decodeURIComponent(document.cookie);
        const properties = decodedCookie.split(';');
        for(let i = 0; i < properties.length; i++) {
            let property = properties[i];
            while (property.charAt(0) === ' ') {
                property = property.substring(1);
            }
            if (property.indexOf(cookie) === 0) {
                return property.substring(cookie.length, property.length);
            }
        }
        return null;
    }, [])

    useEffect(() => {
        setContext((prev) => { return {...prev, participant_id: obtain_cookie(cookie_name)}})
    }, [obtain_cookie])

    return (
        <ParticipantContext.Provider value={[context, setContext]}>
            {children}
        </ParticipantContext.Provider>
    )
}