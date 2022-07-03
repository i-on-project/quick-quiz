import * as React from "react";
import {Container, Row} from "react-bootstrap";
import {Link} from "react-router-dom";

export const PageNotFound = () => {
    return(
        <Container className="mt-3 text-center"><Row>
            <h1>404 - Page not found 🚫</h1>
            <Link to="/">Go home</Link>
        </Row></Container>
    )
}