import React, {useState} from 'react'
import {goPOST} from "../Services/FetchService";
import Button from 'react-bootstrap/Button';
import {Card, Container, FormControl, InputGroup, Row} from "react-bootstrap";

export const LoginUser = () => {
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const [userName, setUserName] = useState(null);


    const userNameChangeHandler = (event) => {
        setUserName(event.target.value);
    };

    const setDataForUrl = (fData) => {
        fData.properties.host = window.location.host;
        setData(fData);
    }

    const toggleButtonState = () => {
        const postData = {userName: userName}
        console.log(postData)
        goPOST(`/api/web/v1.0/non_auth/login`, postData, setDataForUrl, setError)
    };

    return (
        <div>
            <Container>
                <Row>
                    <Card>
                        <Card.Body>
                            <Card.Title>Login</Card.Title>
                            <InputGroup className="mb-3">
                                <InputGroup.Text id="username"/>
                                <FormControl
                                    placeholder="Username"
                                    aria-label="Username"
                                    aria-describedby="basic-addon1"
                                    onChange={userNameChangeHandler}
                                />
                            </InputGroup>

                            <Button className="btn btn-success" type="submit"
                                    onClick={toggleButtonState}> Save
                            </Button>

                        </Card.Body>
                    </Card>

                </Row>
                <Row>
                    {data || error ?  (
                        <Card>
                            <Card.Body>
                                Please check your email to login.
                            </Card.Body>
                        </Card>
                    ) : <div/>}
                </Row>
                <Row>
                    {data || error ?  (
                        <Card>
                            <Card.Body>
                                <strong>Debug Info:</strong>
                                {error && (
                                    <div>{`There is a problem fetching the post data - ${error}`}</div>
                                )}

                                {data && (
                                    <div>{data.properties.userName}</div>
                                )}
                                {data && (
                                    <div>{data.properties.token}</div>
                                )}

                                {data && (
                                    <a href={`http://${data.properties.host}/logmein?user=${data.properties.userName}&token=${data.properties.token}`}> LogMeIn </a>)}
                            </Card.Body>
                        </Card>
                    ) : <div/>}
                </Row>

            </Container>
        </div>
    );
}