import * as React from "react"
import {useContext, useEffect} from "react";
import {Navigate} from "react-router-dom";
import {UserContext} from "./UserContext";
import {Container, Spinner} from "react-bootstrap";
import {request} from "../utils/Request";

export const LoginVerifier = (props) => {

    const [context] = useContext(UserContext)

    if(context.loading) return <Container className="mt-3"><h4>Verifying user...</h4><Spinner animation="border"/></Container>
    if(context.logged_in === false) return <Navigate to="/login"/>

    return <CheckUser children={props.children}/>
}

const uri = '/api/web/v1.0/auth/checkuser'
const CheckUser = (props) => {

    const [context, setContext] = useContext(UserContext)

    useEffect(() => {
        if(context.logged_in) return
        setContext(prev => { return {...prev, loading: true}})
        request(uri, {method: 'GET'}, {
            success: data => setContext(prev => {
                return {...prev,
                    username: data.properties.userName,
                    display_name: data.properties.displayName,
                    loading: false,
                    logged_in: true
                }
            }),
            failed: problem => setContext(prev => {
                return {...prev,
                    loading: false,
                    error: problem
                }
            })
        })
    }, [context, setContext])

    return props.children
}