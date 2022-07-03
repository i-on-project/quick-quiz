import * as React from "react";
import {useCallback, useState} from "react";
import {Link} from "react-router-dom";
import {Button, Card, Container, Form, Row, Spinner} from "react-bootstrap";
import {Notification} from "./Notification";
import {Countdown} from "./Countdown";
import {parse_body, request} from "../utils/Request";

const uri = '/api/web/v1.0/non_auth/register'
export const Register = () => {

    const [state, setState] = useState({
        values: {email: '', username: ''},
        data: null,
        loading: false,
        problem: null
    })

    const onSubmitHandler = useCallback((event) => {
        event.preventDefault()
        setState(prev => { return {...prev, loading: true}})
        const body = {userName: state.values.email.trim(), displayName: state.values.username.trim()}
        request(uri, {method: 'POST', ...parse_body(body)}, {
            success: (data) => setState(prev => { return {...prev, data: data, loading: false}}),
            failed: (problem) => setState(prev => { return {...prev, problem: problem, loading: false}})
        })
    }, [state.values.email, state.values.username])

    const onChangeEmailHandler = useCallback((event) => {
        setState((prev) => {
            prev.values.email = event.target.value
            return {...prev}
        })
    }, [])

    const onChangeUsernameHandler = useCallback((event) => {
        if(event.target.value.length >= 50) return
        setState((prev) => {
            prev.values.username = event.target.value.replace(/[^a-z0-9 _-]/gi,'')
            return {...prev}
        })
    }, [])

    const onCloseHandler = useCallback(() => setState(prev => { return {...prev, problem: null}}), [])

    let debug_info = null
    if(state.data != null) {
        const data = state.data.properties
        debug_info = <Row><Card>
            <Card.Title>Debug Info</Card.Title>
            <Card.Body>
                <p className="m-0 p-0"><strong>Email: </strong>{data.userName}</p>
                <p className="m-0 p-0"><strong>Username: </strong>{data.displayName}</p>
                <p className="m-0 p-0"><strong>Token: </strong>{data.token}</p>
                <p className="m-0 p-0"><strong>Timeout: </strong><Countdown deadline={data.timeout}/></p>
                <Link className="m-0 p-0" to={`/logmein?user=${data.userName}&token=${data.token}`}>LogMeIn</Link>
            </Card.Body>
        </Card></Row>
    }

    return (
        <Container>
            <Row><Notification problem={state.problem} onClose={onCloseHandler}/></Row>
            <Row><Card>
                <Card.Title>Register</Card.Title>
                <Card.Body>
                    {state.data == null ?
                    <Form onSubmit={onSubmitHandler}>
                        <Form.Control required type="email" placeholder="Email" value={state.values.email}
                                      onChange={onChangeEmailHandler}/>
                        <Form.Control required type="text" placeholder="Username" value={state.values.username}
                                      onChange={onChangeUsernameHandler}/>
                        <Button variant="success" className="mt-3" type="submit" disabled={state.loading}>
                            {state.loading === true ? <Spinner animation="border"/> : 'Register'}
                        </Button>
                    </Form> :
                    <p>Please check your email to confirm the registration and login.</p>
                    }
                </Card.Body>
            </Card></Row>
            {debug_info}
        </Container>
    )
}