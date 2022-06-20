import React, {Fragment, useContext, useEffect, useState} from 'react'
import {Container, Navbar} from "react-bootstrap";
import {Navigate, Link} from "react-router-dom";
import Button from "react-bootstrap/Button";
import {goPOST} from "../Services/FetchService";

import {UserContext} from "./UserContextProvider";

export const NavBarComponent = () => {

    //console.log("render")

    const userContext = useContext(UserContext)
    const [logout, setLogout] = useState(false)

    const logoutHandler = () => {
        goPOST('/api/web/v1.0/auth/logout',null, null, null)
        userContext.updateUser(null, null)
        setLogout(true)
    }
    let redirect = null

    if(logout) {
        redirect = <Navigate to={"/"}  />
        //console.log("logout")
        setLogout(false)
    }

    const isLoggedIn = () => userContext.userName !== null && userContext.isLoading === false
    const isNotLoggedIn = () => userContext.userName === null && userContext.isLoading === false
/*
    console.log(logout)
    console.log(redirect)*/

    return (
        <Fragment>
            {redirect }

            <Navbar bg="light" expand="lg">
                <Container>
                    <Navbar.Text><Link to="/">Home</Link></Navbar.Text>
                    {isLoggedIn() ? <Navbar.Brand href="/sessions">Sessions</Navbar.Brand> : null}
                    {isLoggedIn() ? <Navbar.Brand href="/templates">Templates</Navbar.Brand> : null}
                    {isLoggedIn() ? <Navbar.Brand href="/history">History</Navbar.Brand> : null}
                    {isLoggedIn() ? <Navbar.Text>Welcome {userContext.displayName}</Navbar.Text>   : null}
                    {isLoggedIn() ? <Button className="btn btn-success" type="submit"
                                                    onClick={logoutHandler}><Link to={"/"}/> Logout
                    </Button> : null}
                    {isNotLoggedIn() ? <Navbar.Brand href="/register">Register</Navbar.Brand> : null}
                    {isNotLoggedIn() ? <Navbar.Brand href="/login">Login</Navbar.Brand> : null}

                </Container>
            </Navbar>
            {userContext.inSession !== null && <Navigate to={`/insession/${userContext.inSession}`}/>}

        </Fragment>
    );
}