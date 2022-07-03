import * as React from "react"
import {useEffect, useContext} from 'react'
import {Link, Navigate, useSearchParams} from "react-router-dom";
import {Card, Container, Row, Spinner} from "react-bootstrap";
import {UserContext} from "./UserContext";
import {parse_body, request} from "../utils/Request";

const uri = '/api/web/v1.0/non_auth/logmein'
export const LogMeIn = () => {

    const [searchParams] = useSearchParams()
    const [context, setContext] = useContext(UserContext)

    useEffect(() => {
        setContext(prev => {return {...prev, loading: true}})
        const body = {
            userName: searchParams.get("user"),
            loginToken: searchParams.get("token")
        }
        const success_func = (data) => {
            setContext({
                username: data.properties.userName,
                display_name: data.properties.displayName,
                logged_in: true,
                loading: false,
                error: null
            })
        }
        const failed_func = (problem) => {
            setContext({
                username: null,
                display_name: null,
                logged_in: false,
                loading: false,
                error: problem
            })
        }
        const functions_obj = {success: success_func, failed: failed_func}
        return request(uri, {method: 'POST', ...parse_body(body)}, functions_obj).cancel
    }, [searchParams, setContext])

    if(context.loading === true) return <Container className="mt-3"><h4>Verifying user...</h4><Spinner animation="border"/></Container>
    if(context.logged_in === true) return <Navigate to='/'/>
    else if(context.problem != null) return <Container className="mt-3">
        <Row><Card>
            <Card.Title>Something unexpected happened!</Card.Title>
            <Card.Body>
                The link used to login seems to have expired.
                Please request a new <Link to="/login">Login</Link> and try again.
            </Card.Body>
        </Card></Row>
    </Container>
}