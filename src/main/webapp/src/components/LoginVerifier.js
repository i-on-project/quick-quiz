import * as React from "react"
import {useContext} from "react";
import {Navigate} from "react-router-dom";
import {UserContext} from "./UserContext";
import {Container, Spinner} from "react-bootstrap";

export const LoginVerifier = (props) => {

    const [context] = useContext(UserContext)

    if(context.loading) return <Container className="mt-3"><h4>Verifying user...</h4><Spinner animation="border"/></Container>
    if(context.logged_in === false) return <Navigate to="/login"/>

    return props.children
}