import React, {useState} from 'react'
import {goPOST} from "../Services/FetchService";
import Button from 'react-bootstrap/Button';
import {Card, Container, FormControl, InputGroup, Row} from "react-bootstrap";

export const RegisterUser = () => {
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    const [userName, setUserName] = useState(null);
    const [displayName, setDisplayName] = useState(null)

    const userNameChangeHandler = (event) => {
        setUserName(event.target.value);
    };

    const displayNameChangeHandler = (event) => {
        setDisplayName(event.target.value);
    };


    const toggleButtonState = () => {
        const postData = {userName: userName, displayName: displayName}
        console.log(postData)
        goPOST(`/api/web/v1.0/non_auth/register`, postData, setData, setError)
    };

    return (
        <div>

            <Container>
                <Row>
                    <Card>

                        <Card.Body>
                            <Card.Title>Quick Quiz Register Test</Card.Title>
                            <InputGroup className="mb-3">
                                <InputGroup.Text id="username"/>
                                <FormControl
                                    placeholder="Username"
                                    aria-label="Username"
                                    aria-describedby="basic-addon1"
                                    onChange={userNameChangeHandler}
                                />
                            </InputGroup>
                            <InputGroup className="mb-3">
                                <InputGroup.Text id="displayname"/>
                                <FormControl
                                    placeholder="Display Name"
                                    aria-label="Display Name"
                                    aria-describedby="basic-addon1"
                                    onChange={displayNameChangeHandler}
                                />
                            </InputGroup>


                            <Button className="btn btn-success" type="submit"
                                    onClick={toggleButtonState}> Save
                            </Button>
                            {/*                                <button className="btn btn-danger"
                                        onClick={this.cancel.bind(this)}
                                        style={{marginLeft: "10px"}}>Cancel
                                </button>*/}

                            <Card>

                                <Card.Body>
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
                                        <a href={`http://localhost:3000/logmein?user=${data.properties.userName}&token=${data.properties.token}`}> LogMeIn </a>)}
                                </Card.Body>
                            </Card>
                        </Card.Body>
                    </Card>
                </Row>

            </Container>
        </div>
    );
}