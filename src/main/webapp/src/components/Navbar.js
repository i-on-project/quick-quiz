import * as React from "react";
import {Fragment, useCallback, useContext, useEffect, useState} from 'react'
import {Container, Navbar, Button, Spinner} from "react-bootstrap";
import {UserContext} from "./UserContext";
import {Link, Navigate} from "react-router-dom";
import {request_no_content} from "../utils/Request";

const uri = '/api/web/v1.0/auth/logout'
export const NavigationBar = () => {

    const [context, setContext] = useContext(UserContext)
    const [redirect, setRedirect] = useState(false)

    const onClickHandler = useCallback(() => {
        const success_f = () => {
            setRedirect(true)
            setContext({
                username: null,
                display_name: null,
                loading: false,
                logged_in: false,
                problem: null
            })
        }
        const failed_f = (problem) => {
            alert(`Logout failed: ${problem.description == null ? problem.title : problem.description}`)
        }
        const functions_object = {success: success_f, failed: failed_f}
        request_no_content(uri, {method: 'POST'}, functions_object)
    }, [setContext])

    useEffect(() => {
        if(redirect === true) setRedirect(false)
    }, [redirect])

    const location = window.location.pathname
    if(location !== '/' && redirect === true) return <Navigate to="/"/>

    if(context.loading) return(
        <Navbar bg="light" expand="lg">
            <Container>
                <Navbar.Text>Loading User...</Navbar.Text>
                <Navbar.Brand><Spinner animation="border" role="status">
                    <span className="visually-hidden"></span>
                </Spinner></Navbar.Brand>
            </Container>
        </Navbar>
    )

    const {logged_in} = context

    let content
    if(logged_in) content = <Fragment>
        <Navbar.Brand><Link to="/sessions">Sessions</Link></Navbar.Brand>
        <Navbar.Brand><Link to="/templates">Templates</Link></Navbar.Brand>
        <Navbar.Brand><Link to="/history">History</Link></Navbar.Brand>
        <Navbar.Text>Welcome {context.display_name}</Navbar.Text>
        <Button variant="success" type="button" onClick={onClickHandler}>Logout</Button>
    </Fragment>
    else content = <Fragment>
        <Navbar.Brand><Link to="/register">Register</Link></Navbar.Brand>
        <Navbar.Brand><Link to="/login">Login</Link></Navbar.Brand>
    </Fragment>

    return (
        <Navbar bg="light" expand="lg">
            <Container>
                <Navbar.Text><Link to="/">Home</Link></Navbar.Text>
                {content}
            </Container>
        </Navbar>
    );
}