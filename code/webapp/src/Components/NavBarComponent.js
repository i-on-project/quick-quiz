import React, {useContext, useEffect, useState} from 'react'
import {Container, Navbar} from "react-bootstrap";
import {Link} from "react-router-dom";
import Button from "react-bootstrap/Button";
import {goPOST} from "../Services/FetchService";

import {UserContext} from "./UserContextProvider";

export const NavBarComponent = () => {

    const userContext = useContext(UserContext)

    const logoutHandler = () => {
        goPOST('/api/web/v1.0/auth/logout',null, null, null)
        userContext.updateUser(null, null)

    }

    const isLoggedIn = () => userContext.userName !== null && userContext.isLoading === false
    const isNotLoggedIn = () => userContext.userName === null && userContext.isLoading === false

    return (
        <div>
            <Navbar bg="light" expand="lg">
                <Container>
                    <Navbar.Text><Link to="/">Home</Link></Navbar.Text>
                    {isLoggedIn() ? <Navbar.Brand href="/sessions">Sessions</Navbar.Brand> : null}
                    {isLoggedIn() ? <Navbar.Brand href="/templates">Templates</Navbar.Brand> : null}
                    {isLoggedIn() ? <Navbar.Brand href="/history">History</Navbar.Brand> : null}
                    {isLoggedIn() ? <Navbar.Text>Welcome {userContext.displayName}</Navbar.Text>   : null}
                    {isLoggedIn() ? <Button className="btn btn-success" type="submit"
                                                    onClick={logoutHandler}> Logout
                    </Button> : null}
                    {isNotLoggedIn() ? <Navbar.Brand href="/register">Register</Navbar.Brand> : null}
                    {isNotLoggedIn() ? <Navbar.Brand href="/login">Login</Navbar.Brand> : null}

                </Container>
            </Navbar>
        </div>
    );
}