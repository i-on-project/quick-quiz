import React from 'react'
import {Container, Navbar} from "react-bootstrap";
import {Link} from "react-router-dom";

export const NavbarComponentNotAuth = () => {

    return (
        <div>
            <Navbar bg="light" expand="lg">
                <Container>
                    <Navbar.Text><Link to="/">Home</Link></Navbar.Text>
                    <Navbar.Brand href="/register">Register</Navbar.Brand>
                    <Navbar.Brand href="/login">Login</Navbar.Brand>
                </Container>
            </Navbar>
        </div>
    );
}