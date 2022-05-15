import {Form, FormControl, InputGroup} from "react-bootstrap";
import React, {useState} from "react";
import {useEffect} from "react";
import Button from "react-bootstrap/Button";


export const SessionForm = (props) => {
    const validPropsData = () => props.session !== undefined && props.session !== null

    const [name, setName] = useState(validPropsData() ? props.session.name : '')
    const [status, setStatus] = useState(validPropsData() ? props.session.status : '')
    const [description, setDescription] = useState(validPropsData() ? props.session.description : '')
    const [creationDate] = useState(validPropsData() ? props.session.creationDate : '')
    const [guestCode, setGuestCode] = useState(validPropsData() ? props.session.guestCode : '')
    const [limitOfParticipants, setLimitOfParticipants] = useState(validPropsData() ? props.session.limitOfParticipants : '')
    const [geolocation, setGeolocation] = useState(validPropsData() ? props.session.geolocation : '')
    const [radius, setRadius] = useState(validPropsData() ? props.session.radius : '')
    const [useGeo, setUseGeo] = useState(false)
    const [edit, setEdit] = useState(false)
    const [create, setCreate] = useState(false)

    useEffect(() => {
        if(props.updateSession === null || props.updateSession === undefined) {
            setEdit(true)
            setCreate(true)
        } else
            setCreate(false)
    }, [props])


    const createUpdateSession = () => {
        const updatedSession = {
            name: name,
            description: description,
            limitOfParticipants: limitOfParticipants,
            //missing geolocation info
        }
        if(props.updateSession !== undefined && props.updateSession !== null)
            props.updateSession(updatedSession)
        else
            props.createSession(updatedSession)
    }



    const editButton = () => (
        <Button className="btn btn-success left"
                onClick={() => setEdit(true)}> Edit
        </Button>
    )

    const cancelEditButton = () => (
        <Button className="btn btn-success left"
                type="submit"
                /*onClick={cancelEdit}*/> Cancel
        </Button>
    )

    const saveButton = () => (
        <Button className="btn btn-success left" type="submit"
                onClick={createUpdateSession}> {!create ? "Update" : "Create" }
        </Button>
    )

    const getCreatedDate = () => {
        const d = new Date(0)
        d.setSeconds(creationDate)
        return d.getDate().toLocaleString()
    }

    return (
        <Form>

            {!create && <div><Form.Label>Created on {getCreatedDate()}</Form.Label></div>}
            <Form.Label>Session Name:</Form.Label>
            <InputGroup>
                <FormControl
                    placeholder="Session Name"
                    aria-label="Session Name"
                    aria-describedby="session_name"
                    disabled={!edit}
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                />
            </InputGroup>
            <Form.Label>Session Description:</Form.Label>
            <InputGroup>
                <FormControl
                    placeholder="Session description"
                    aria-label="Session description"
                    aria-describedby="session_description"
                    disabled={!edit}
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                />
            </InputGroup>
            <Form.Label>Session Status:</Form.Label>
            <Form.Select value={status}
                         onChange={(e) => setStatus(e.target.value)}
                         disabled={!edit}>
                <option value='NOT_STARTED'>Not Started</option>
                <option value='STARTED'>Started</option>
                <option value='CLOSED'>Closed</option>
            </Form.Select>

            <Form.Label>Limit Of Participants:</Form.Label>
            <InputGroup>
                <FormControl
                    placeholder="Limit Of Participants"
                    type="number"
                    aria-label="Limit Of Participants:"
                    aria-describedby="limit_participants"
                    disabled={!edit}
                    value={limitOfParticipants}
                    onChange={(e) => setLimitOfParticipants(e.target.value)}
                />
            </InputGroup>

            <InputGroup>
                <InputGroup.Checkbox aria-label="Checkbox for following text input"
                                     onChange={(e) => setUseGeo(e.target.value)}
                                     disabled={!edit}/>
                <Form.Label> Use Geolocation</Form.Label>
            </InputGroup>
            {!edit && !create && editButton()}
            {edit && !create && cancelEditButton()}
            {edit && !create && saveButton()}
        </Form>
    )
}