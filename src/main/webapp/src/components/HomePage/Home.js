import * as React from "react";
import {useCallback, useState} from "react";
import {Container, Row, Card, Button, Form, Spinner} from "react-bootstrap"
import {Navigate} from "react-router-dom"
import {parse_body, request} from "../../utils/Request";
import {Notification} from "../Notification";
import {PreviousSession} from "./PreviousSession";
import {stringOnlyDigits} from "../../utils/StringUtils";

const uri = '/api/web/v1.0/non_auth/join_session'
export const Home = () => {

    const [state, setState] = useState({code: "", loading: false, problem: null})

    const onChangeHandler = useCallback((event) => {
        const value = stringOnlyDigits(event.target.value)
        if(parseInt(value) > 2147483647) return
        setState((prev) => { return {...prev, code: value}})
    }, [])

    const onSubmitHandler = useCallback((event) => {
        event.preventDefault()
        setState((prev) => { return {...prev, loading: true}})
        const value = parseInt(event.target.code.value)

        const success_func = (data) => {
            setState((prev) => { return {...prev, loading: false, redirect: data.properties.participantId}})
        }

        const failed_func = (problem) => {
            setState((prev) => { return {...prev, loading: false, problem: problem}})
        }

        request(uri, {method: 'POST', ...parse_body({sessionCode: value})}, {success: success_func, failed: failed_func})
    }, [])

    const onCloseHandler = useCallback(() => {
        setState((prev) => { return {...prev, problem: null}})
    }, [])

    if(state.redirect != null) return <Navigate to={`/insession/${state.redirect}`}/>

    let submit_button = <Button variant="success" type="submit">Submit</Button>
    if(state.loading) submit_button = <Button variant="success" type="submit" disabled={true}>
        <Spinner animation="border" role="status"><span className="visually-hidden"></span></Spinner>
    </Button>

    return (
        <React.Fragment>
            <Container>
                <Row><Notification problem={state.problem} onClose={onCloseHandler}/></Row>
                <Row><PreviousSession/></Row>
                <Row><Card>
                    <Card.Title>Join a Quiz Session</Card.Title>
                    <Card.Body><Form onSubmit={onSubmitHandler}>
                        <Form.Group className="mb-3" controlId="code">
                            <Form.Label>Session Code</Form.Label>
                            <Form.Control type="text" value={state.code} maxLength={10} onChange={onChangeHandler}/>
                        </Form.Group>{submit_button}</Form>
                    </Card.Body>
                </Card></Row>
            </Container>
        </React.Fragment>
    );
}