import { Fragment, useEffect, useState } from "react"
import { Container, Row, Spinner } from "react-bootstrap"
import { get } from "../../Services/RequestService"
import HistoryDetails from "./HistoryDetails"

const URI = "/api/web/v1.0/auth/history"

const History = () => {

    const [history, setHistory] = useState(null)
    const [error, setError] = useState(null)
    const [loading, setLoading] = useState(false)

    useEffect(() => {
        setLoading(true)
        return get(URI, setHistory, setError)
    }, [])

    useEffect(() => {
        if (history || error) setLoading(false)
    }, [history, error])

    let content = <h3>You don't have a history</h3>
    
    if(loading === true) {
        content = <Spinner animation="border" role="status"><span className="visually-hidden">Loading...</span></Spinner>
    }

    if(error !== null) {
        content = <h3>An error had occured</h3>
    }

    if(history !== null && history.entities.length !== 0) {
        content = history.entities.map(item => <HistoryDetails key={item.fields[0].value} item={item}/>)
    }

    return (
        <Fragment>
            <Container>
                <Row>
                    <h1>History</h1>
                </Row>
            </Container>
            <Container className={"d-flex justify-content-center"}>
                <Row>
                    {content}
                </Row>
            </Container>
        </Fragment>
    )
}

export default History