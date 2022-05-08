import React, {useEffect, useState} from 'react';

const func = () => {}

const appUser  = {
    userName: null,
    displayName: null,
    updateUser: func
}
export const UserContext = React.createContext(appUser)

export const UserContextProvider = ({children}) => {
    const [context, setContext] = useState(appUser)

    const updateContext = (contextUpdates = {}) => {
        setContext(currentContext => ({...currentContext, ...contextUpdates}))
    }

    useEffect(() => {
        if (context.updateUser === func) {
            updateContext({
                updateUser: (user, display ) => updateContext({ userName: user, displayName: display }),
            })
        }
    }, [context.updateUser])


    return (
        <UserContext.Provider value={context}>
            {children}
        </UserContext.Provider>
    )
}

