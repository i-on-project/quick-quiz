import {Card, Container, FormControl, InputGroup, Row} from "react-bootstrap";
import React, {Fragment, useEffect, useState, useContext} from 'react'
import Button from "react-bootstrap/Button";
import {goGET, goPOST} from "../Services/FetchService";
import {Navigate} from "react-router-dom";

import {UserContext} from "./UserContextProvider";


export const HomeComponent = () => {

    const userContext = useContext(UserContext)

    const [error, setError] = useState(null);
    const [session, setSession] = useState(null);
    const [goToParticipantSession, setGoToParticipantSession] = useState(false)
    const [inSession, setInSession] = useState(false)

    const sessionhangeHandler = (event) => {
        setSession(event.target.value);
    };

    useEffect(() => {

        const setError = (error) => {
            setInSession(false)
            console.log(error)
        }

        const thereIsCookie = (data) => {
            setInSession(true)
        }

        goGET(`/api/web/v1.0/non_auth/is_in_session`, thereIsCookie, setError)
    }, [])

    function joinSessionAction () {
        const postData = {sessionCode: session}
        const setMeSession = (data) => {
            setSession(data)
            console.log(data)
            setGoToParticipantSession(true)
        }
        goPOST(`/api/web/v1.0/non_auth/join_session`, postData, setMeSession, setError)
    }

    return (
        <Fragment>
            {goToParticipantSession && <Navigate to={`insession/${session.participantId}`} /> }
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
                                <div>{`There is a problem joining the session - ${error}`}</div>
                            )}
                            {/*{data && (

                                <div>{`GuestId: ${data.id}`}</div>
                            )}

                            {data && (

                                <div>{`SessionId: ${data.sessionId}`}</div>
                            )}

                            {data && (<InSession props={data} /> )}*/}
                        </Card.Body>
                    </Card>
                </Row>

            </Container>
        </Fragment>
    );
}