import * as React from "react";
import {useCallback, useState} from "react";
import {Link} from "react-router-dom";
import {Button, Card, Container, Form, Row, Spinner} from "react-bootstrap";
import {Notification} from "./Notification";
import {parse_body, request} from "../utils/Request";

const uri = '/api/web/v1.0/non_auth/login'
export const Login = () => {

    const [state, setState] = useState({email: '', data: null, loading: false, problem: null})

    const onSubmitHandler = useCallback((event) => {
        event.preventDefault()
        setState(prev => { return {...prev, loading: true}})
        request(uri, {method: 'POST', ...parse_body({userName: state.email.trim()})}, {
            success: (data) => setState(prev => { return {...prev, data: data, loading: false}}),
            failed: (problem) => setState(prev => { return {...prev, problem: problem, loading: false}})
        })
    }, [state.email])
    const onChangeHandler = useCallback((event) => {
        setState((prev) => { return {...prev, email: event.target.value}})
    }, [])

    const onCloseHandler = useCallback(() => setState(prev => { return {...prev, problem: null}}), [])

    let debug_info = null
    if(state.data != null) {
        const data = state.data.properties
        debug_info = <Row><Card>
            <Card.Title>Debug Info</Card.Title>
            <Card.Body>
                <p><strong>Email: </strong>{data.userName}</p>
                <p><strong>Token: </strong>{data.token}</p>
                <Link to={`/logmein?user=${data.userName}&token=${data.token}`}>LogMeIn</Link>
            </Card.Body>
        </Card></Row>
    }

    return (
        <Container>
            <Row><Notification problem={state.problem} onClose={onCloseHandler}/></Row>
            <Row><Card>
                <Card.Title>Login</Card.Title>
                <Card.Body>
                    <Form onSubmit={onSubmitHandler}>
                        <Form.Control type="email" placeholder="Email" value={state.email} onChange={onChangeHandler}/>
                        <Button variant="success" className="mt-3" type="submit" disabled={state.loading}>
                            {state.loading === true ? <Spinner animation="border"/> : 'Login'}
                        </Button>
                    </Form>
                </Card.Body>
            </Card></Row>
            {debug_info}
        </Container>
    )
}