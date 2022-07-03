import * as React from "react";
import {Link, useParams} from "react-router-dom";
import {Container, Row} from "react-bootstrap";

export const Template = () => {

    const {id} = useParams()
    console.log(id)

    /*TODO implement the visual of this page*/
    return(
        <Container><Row className="text-canter">
            <h1>Not yet implemented</h1>
            <Link to='/templates'>Go back</Link>
        </Row></Container>
    )
}