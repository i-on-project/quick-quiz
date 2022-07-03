import * as React from "react"
import {Fragment, useEffect, useState} from "react";
import {request} from "../../utils/Request";
import {Container, Row, Spinner} from "react-bootstrap";
import {Notification} from "../Notification";
import {HistoryCard} from "./HistoryCard";

const uri = '/api/web/v1.0/auth/history'
export const History = () => {

    const [state, setState] = useState({data: null, loading: true, problem: null})

    useEffect(() => {
        const sf = (data) => setState(prev => { return {...prev, data: data, loading: false}})
        const ff = (problem) => setState(prev => { return {...prev, problem: problem, loading: false}})
        return request(uri, {method: 'GET'}, {success: sf, failed: ff}).cancel
    }, [])

    let content = <h3>You don't have a history</h3>
    if(state.loading === true) content = <div className="ms-3 text-center"><Spinner animation="border" style={{width: "3rem", height: "3rem"}}/></div>
    else if(state.data != null) content = state.data.entities.map(elem => <HistoryCard key={elem.fields[0].value} history={elem.properties}/>)

    return (
        <Fragment>
            <Container>
                <Row><h1>History</h1></Row>
                <Row><Notification /></Row>
            </Container>
            <Container className={"mb-3 mt-3 d-flex justify-content-center"}>
                <Row>
                    {content}
                </Row>
            </Container>
        </Fragment>
    )
}