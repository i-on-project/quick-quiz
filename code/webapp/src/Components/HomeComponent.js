import {Card, Container, FormControl, InputGroup, Row} from "react-bootstrap";
import React, {useEffect, useState, useContext} from 'react'
import Button from "react-bootstrap/Button";
import {goFetch} from "../Services/FetchService";
import {InSession} from "./InSessionComponent";

import {UserContext} from "./UserContextProvider";


export const HomeComponent = () => {

    const userContext = useContext(UserContext)

    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    const [session, setSession] = useState(null);

    const sessionhangeHandler = (event) => {
        setSession(event.target.value);
    };

    useEffect(() => {
        console.log('How is context?')
        console.log(userContext.userName)
        console.log(userContext.displayName)
    }, [])

    function joinSessionAction () {
        const postData = {sessionCode: session}
        goFetch(`/api/web/v1.0/non_auth/join_session`, postData, setData, setError)
    }

    return (
        <div>
            <Container>
                <Row>
                    <Card>

                        <Card.Body>
                            <Card.Title>Join a Quiz Session</Card.Title>
                            <InputGroup className="mb-3">
                                <InputGroup.Text id="sessioncode"/>
                                <FormControl
                                    placeholder="Session Code"
                                    aria-label="Session"
                                    aria-describedby="basic-addon1"
                                    onChange={sessionhangeHandler}
                                />
                            </InputGroup>
                            <Button className="btn btn-success" type="submit"
                                    onClick={joinSessionAction}> Save
                            </Button>

                            {error && (
                                <div>{`There is a problem fetching the post data - ${error}`}</div>
                            )}
                            {data && (

                                <div>{`GuestId: ${data.id}`}</div>
                            )}

                            {data && (

                                <div>{`SessionId: ${data.sessionId}`}</div>
                            )}

                            {data && (<InSession props={data} /> )}
                        </Card.Body>
                    </Card>
                </Row>

            </Container>
        </div>
    );
}