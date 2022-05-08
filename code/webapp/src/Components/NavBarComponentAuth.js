import React, {useContext} from 'react'
import {Container, FloatingLabel, Navbar} from "react-bootstrap";
import {Link} from "react-router-dom";
import Button from "react-bootstrap/Button";
import {goFetch} from "../Services/FetchService";
import {authService} from "../Services/AuthService";

import {UserContext} from "./UserContextProvider";

export const NavbarComponentAuth = () => {

    const userContext = useContext(UserContext)

    const logoutHandler = () => {
        goFetch('/api/web/v1.0/auth/logout',null, null, null)
        userContext.updateUser(null, null)
    }

    return (
        <div>
            <Navbar bg="light" expand="lg">
                <Container>
                    <Navbar.Text><Link to="/">Home</Link></Navbar.Text>
                    {userContext.userName ? <Navbar.Brand href="/sessions">Sessions</Navbar.Brand> : null}
                    {userContext.userName ? <Navbar.Brand href="/templates">Templates</Navbar.Brand> : null}
                    {userContext.userName ? <Navbar.Brand href="/history">History</Navbar.Brand> : null}
                    {userContext.userName ? <Navbar.Text>Welcome {userContext.displayName}</Navbar.Text>   : null}
                    {userContext.userName ? <Button className="btn btn-success" type="submit"
                                                    onClick={logoutHandler}> Logout
                    </Button> : null}
                    {!userContext.userName ? <Navbar.Brand href="/register">Register</Navbar.Brand> : null}
                    {!userContext.userName ? <Navbar.Brand href="/login">Login</Navbar.Brand> : null}

                </Container>
            </Navbar>
        </div>
    );
}