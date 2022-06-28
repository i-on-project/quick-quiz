import * as React from "react";
import {Fragment, useContext} from 'react'
import {Container, Navbar, Button, Spinner} from "react-bootstrap";
import {UserContext} from "./UserContext";
import {Link} from "react-router-dom";

export const NavigationBar = () => {

    const [context] = useContext(UserContext)

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
        <Button variant="success" type="button" onClick={() => {}}>Logout</Button>
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