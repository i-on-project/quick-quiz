import React, {useState, useEffect, useContext} from 'react'
import {goFetch} from "../Services/FetchService";
import {Card, Container, Row} from "react-bootstrap";
import {Navigate, useSearchParams} from "react-router-dom";
import {UserContext} from "./UserContextProvider";


export const LogMeIn = () => {
    const [searchParams] = useSearchParams() //read query parameters from URI

    /*Data from Success or InSuccess credentials validation*/
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    /*Context User*/
    const userContext = useContext(UserContext)
    const tempUser = {
        userName: null,
        displayName: null,
        token: null
    }

    /*********************************************************************************/
    useEffect(() => {
        /*Assign query values*/
        tempUser.userName = searchParams.get("user")
        tempUser.token = searchParams.get("token")


        if(!data) {
            console.log(`useEffect 2: ${tempUser.userName}`)
            const postData = {userName: tempUser.userName, loginToken: tempUser.token}
            goFetch(`/api/web/v1.0/non_auth/logmein`, postData, setData, setError)
        }

    }, [tempUser.userName,tempUser.token, data])



    useEffect(() => {
        if (data) {
            tempUser.displayName = data.properties.displayName
            userContext.updateUser(tempUser.userName, tempUser.displayName)
        }
    }, [data, userContext, tempUser.userName, tempUser.displayName])

    return (<div>
            {data && (<Navigate to={"/"}/>)}
            {error && (<Container>
                    <Row>
                        <Card>
                            <Card.Body>
                                <Card.Text>
                                    <h3>The link used seems to have expired. Please request new a new <a
                                        href="/login">Login</a> and try again.</h3>
                                </Card.Text>
                            </Card.Body>
                        </Card>
                    </Row>
                </Container>)}
        </div>

    );
}
