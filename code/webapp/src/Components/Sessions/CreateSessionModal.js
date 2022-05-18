import {SessionForm} from "./SessionForm";
import {Button, Form, FormControl, InputGroup, Modal} from "react-bootstrap";
import React, {useState, Fragment} from "react";


export const CreateSessionModal = ((props) => {
    const validPropsData = () => props.session !== undefined && props.session !== null

    const [name, setName] = useState(validPropsData() ? props.session.name : '')
    const [description, setDescription] = useState(validPropsData() ? props.session.description : '')
    const [limitOfParticipants, setLimitOfParticipants] = useState(validPropsData() ? props.session.limitOfParticipants : '')
    const [geolocation, setGeolocation] = useState(validPropsData() ? props.session.geolocation : '')
    const [radius, setRadius] = useState(validPropsData() ? props.session.radius : '')
    const [useGeo, setUseGeo] = useState(false)
    const [validated, setValidated] = useState(false);

    const handleSubmit = (event) => {
        const form = event.currentTarget;

        event.preventDefault();

        if (form.checkValidity() === false) {
           event.stopPropagation();
        }
        else {
            const newSession = {
                name: name,
                description: description,
                limitOfParticipants: limitOfParticipants,
            }
            if (props.createSession != null) {
                props.createSession(newSession)
            }
        }
        setValidated(true);
    };


    const handleClose = () => {
        if(props.handleClose != null) {
            props.handleClose()
        }
    }

    return (
        <Fragment>
            <Form noValidate validated={validated} onSubmit={handleSubmit}>
                <Modal.Header closeButton onClick={handleClose}>
                    <Modal.Title>Quiz Session</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form.Label>Session Name:</Form.Label>
                    <InputGroup>
                        <FormControl
                            required
                            placeholder="Session Name"
                            aria-label="Session Name"
                            aria-describedby="session_name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                        />
                    </InputGroup>
                    <Form.Label>Session Description:</Form.Label>
                    <InputGroup>
                        <FormControl
                            required
                            placeholder="Session description"
                            aria-label="Session description"
                            aria-describedby="session_description"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                        />
                    </InputGroup>
                    <Form.Label>Limit Of Participants:</Form.Label>
                    <InputGroup>
                        <FormControl
                            placeholder="Limit Of Participants"
                            type="number"
                            aria-label="Limit Of Participants:"
                            aria-describedby="limit_participants"
                            defaultValue="10"
                            value={limitOfParticipants}
                            onChange={(e) => setLimitOfParticipants(e.target.value)}
                        />
                    </InputGroup>

                    <InputGroup>
                        <InputGroup.Checkbox aria-label="Checkbox for following text input"
                                             onChange={(e) => setUseGeo(e.target.value)}
                                             />
                        <Form.Label> Use Geolocation</Form.Label>
                    </InputGroup>

                </Modal.Body>

                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}>Close</Button>
                    <Button variant="primary" type="submit">Save changes</Button>
                </Modal.Footer>
            </Form>
        </Fragment>
    )

})