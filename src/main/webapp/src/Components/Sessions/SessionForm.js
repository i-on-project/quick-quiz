import {Form, FormControl, InputGroup} from "react-bootstrap";
import React, {useReducer, useState} from "react";
import {useEffect} from "react";
import Button from "react-bootstrap/Button";
import {getCurrentLocation} from "../../Services/LocationService";

/*
const initialState = {
    name: '',
    status: [],
    description: '',
    guestCode: 1,
    limitOfParticipants: 1,
    useGeo: false,
    geolocation: '',
    radius: 50
};
*/

export const SessionForm = (props) => {
    const validPropsData = () => props.session !== undefined && props.session !== null

    const [name, setName] = useState(validPropsData() ? props.session.name : '')
    const [status, setStatus] = useState(validPropsData() ? props.session.status : '')
    const [description, setDescription] = useState(validPropsData() ? props.session.description : '')
    const [creationDate] = useState(validPropsData() ? props.session.creationDate : '')

    const [limitOfParticipants, setLimitOfParticipants] = useState(validPropsData() ? props.session.limitOfParticipants : '')
    const [geolocation, setGeolocation] = useState(validPropsData() ? props.session.geolocation : '')
    const [radius, setRadius] = useState(validPropsData() && props.session.radius > 50 ? props.session.radius : 50)

    const [edit, setEdit] = useState(false)
    const [create, setCreate] = useState(false)
    const [validated, setValidated] = useState(false)

    /*    const [state, dispatch] = useReducer(reducer, initialState)

        const reducer = (state, action) => {
            switch (action.type) {
                case 'set-mode': {
                    return state;
                }
                case 'set-images': {
                    return state;
                }
                case 'set-query': {
                    return state;
                }
                case 'fetch-next-page': {
                    return state;
                }
                case 'reset': {
                    return initialState;
                }
                default:
                    return initialState;
            }
        };*/


    useEffect(() => {
        if (props.updateSession === null || props.updateSession === undefined) {
            setEdit(true)
            setCreate(true)
        } else setCreate(false)
    }, [props])


    const createUpdateSession = () => {
        const updatedSession = {
            name: name, description: description, limitOfParticipants: limitOfParticipants, geolocation: geolocation, radius: radius
        }
        if (props.updateSession !== undefined && props.updateSession !== null) props.updateSession(updatedSession)
        else props.createSession(updatedSession)
    }


    const editButton = () => (<Button className="btn btn-success left"
                                      onClick={() => setEdit(true)}> Edit
    </Button>)

    const cancelEditButton = () => (<Button className="btn btn-success me-3"
                                            onClick={() => setEdit(false)}> Cancel
    </Button>)

    const saveButton = () => (<Button className="btn btn-success left" type="submit"> {!create ? "Update" : "Create"}
    </Button>)

    const getCreatedDate = () => {
        const d = new Date(0)
        d.setSeconds(creationDate)
        return d.getDate().toLocaleString()
    }

    const updateLocation = () => {
        const success = (pos) => {
            const crd = pos.coords;

            console.log('Your current position is:');
            console.log(`Latitude : ${crd.latitude}`);
            console.log(`Longitude: ${crd.longitude}`);
            console.log(`More or less ${crd.accuracy} meters.`);
            setGeolocation(`${crd.latitude},${crd.longitude},${crd.accuracy}`)
        }

        const error = (err) => {
            console.warn(`ERROR(${err.code}): ${err.message}`);
        }

        getCurrentLocation(success, error)
    }

    const handleSubmit = (event) => {
        const form = event.currentTarget;
        event.preventDefault();
        setValidated(true)
        if (form.checkValidity() === false) {
            event.stopPropagation();
        } else {
            createUpdateSession()
            setEdit(false)
            setValidated(false)
        }

    };

    return (<Form noValidate validated={validated} onSubmit={handleSubmit}>

        {!create && <div><Form.Label>Created on {getCreatedDate()}</Form.Label></div>}
        <Form.Label className={"mb-2 mt-3"}>Session Name:</Form.Label>
        <InputGroup>
            <FormControl
                required
                placeholder="Session Name"
                aria-label="Session Name"
                aria-describedby="session_name"
                disabled={!edit}
                value={name}
                onChange={(e) => setName(e.target.value)}
            />
        </InputGroup>
        <Form.Label className={"mb-2 mt-3"}>Session Description:</Form.Label>
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
        <Form.Label className={"mb-2 mt-3"}>Session Status:</Form.Label>
        <Form.Select value={status}
                     onChange={(e) => setStatus(e.target.value)}
                     disabled={!edit}>
            <option value='NOT_STARTED'>Not Started</option>
            <option value='STARTED'>Started</option>
            <option value='CLOSED'>Closed</option>
        </Form.Select>

        <Form.Label className={"mb-2 mt-3"}>Limit Of Participants:</Form.Label>
        <InputGroup>
            <FormControl
                required
                min={10}
                placeholder="Limit Of Participants"
                type="number"
                aria-label="Limit Of Participants:"
                aria-describedby="limit_participants"
                disabled={!edit}
                value={limitOfParticipants}
                onChange={(e) => setLimitOfParticipants(e.target.value)}
            />
        </InputGroup>

        <Form.Label className={"mb-2 mt-3"}>Geolocation:</Form.Label>
        <InputGroup className="mb-3">
            <FormControl
                key={"geo_location"}
                type="text"
                placeholder="Add Geolocation (optional)"
                value={geolocation}
                disabled={!edit}
                onChange={(e) => setGeolocation(e.target.value)}
            />
            {edit && <Button variant="outline-secondary" id="button-addon2" onClick={updateLocation}>
                Update location
            </Button>}
        </InputGroup>

        {geolocation !== null && geolocation !== '' && (<Form.Label className={"mb-2 mt-3"}>Radius (meters):</Form.Label>)}

        {geolocation !== null && geolocation !== '' && (
            <InputGroup className="mb-3">
                <FormControl
                    required={geolocation !== null && geolocation !== ''}
                    min={50}
                    key={"geo_radius"}
                    type="number"
                    placeholder="Add Radius (min 50m)"
                    value={radius}
                    disabled={!edit}
                    onChange={(e) => setRadius(e.target.value)}
                />
            </InputGroup>
        )}

        {!edit && !create && editButton()}
        {edit && !create && cancelEditButton()}
        {edit && !create && saveButton()}
    </Form>)
}