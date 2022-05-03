import React from "react";
import {Card, Container, Row} from "react-bootstrap";
import * as stompClient from "../Utils/websocket-listener";
//import * as stompClient from "../serviceWorker";

export class InSession extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            thisMessage: '',
            date: new Date()};
    }
    componentDidMount() {
        stompClient.register([
            {route: '/insession', callback: this.refreshCurrentPage}
        ])


    }

    refreshCurrentPage(message) {
        this.setState({thisMessage: message})
    }
    render() {
        return (
            <div>
                <Container>
                    <Row>
                        <Card>
                            <Card.Body>
                                <Card.Title>In Session</Card.Title>
                                <p>{this.state.thisMessage}</p>
                            </Card.Body>
                        </Card>
                    </Row>
                </Container>

            </div>

        );
    }
}
