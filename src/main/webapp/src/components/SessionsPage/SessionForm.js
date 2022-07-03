import * as React from "react";
import {useCallback, useState} from "react";
import {Button, Form, Spinner} from "react-bootstrap";
import {getCurrentLocation} from "../../utils/LocationUtils";
import {stringOnlyDigits} from "../../utils/StringUtils";
import {buildInputModel, validateInputModel} from "../../utils/models/SessionModel";

function nullToEmptyStr(session) {
    return {
        name: session.name,
        description: session.description == null ? '' : session.description,
        status: session.status,
        limitOfParticipants: session.limitOfParticipants.toString(),
        geolocation: session.geolocation == null ? '' : session.geolocation,
        radius: session.radius == null ? '' : session.radius.toString()
    }
}

export const SessionForm = (props) => {

    const {perform} = props
    const editable = props.editable == null ? true : props.editable
    const {session} = props
    const {close} = props

    const initial_state = { session: nullToEmptyStr(session) }
    const [state, setState] = useState(initial_state)

    let button_initial_state
    if(props.button != null) {
        const button = props.button
        button_initial_state = {
            content: button.content == null ? 'Submit' : button.content,
            className: button.className == null ? 'mt-3' : button.className,
            variant: button.variant == null ? 'success' : button.variant,
            disabled: false
        }
    } else button_initial_state = {content: 'Submit', disabled: false}

    const [button, setButton] = useState(button_initial_state)

    const clearSessionState = useCallback(() => {
        setState({session: nullToEmptyStr(session)})
        if(close.func != null) close.func()
    }, [session, close])

    const onSubmitHandler = useCallback((event) => {
        setButton({content: <Spinner animation="border"/>, disabled: true})
        try {
            event.preventDefault();
            const warnings = validateInputModel(state.session)
            if (warnings.length !== 0) setState((prev) => {
                prev.warnings = warnings;
                return {...prev}
            })
            else {
                if (perform == null) return
                const input_model = buildInputModel(state.session)
                perform(input_model)
            }
        } finally {
            setButton(button_initial_state)
        }
    }, [state, perform, button_initial_state])

    const onClickUseLocation = useCallback(() => {
        const s_func = (location) => {
            const {latitude, longitude, accuracy} = location.coords;
            setState((prev) => {
                prev.session.geolocation = `${latitude},${longitude},${accuracy}`
                return {...prev}
            })
        }

        const f_func = (error) => {
            alert(`Couldn't use the device location\nError: ${error.toString()}`)
        }

        getCurrentLocation(s_func, f_func)
    }, [])

    const onClickRemoveLocation = useCallback(() => {
        setState((prev) => {
            prev.session.geolocation = ''
            return {...prev}
        })
    }, [])

    const onChangeNameHandler = useCallback((event) => {
        if(event.target.value.length > 50) return
        setState((prev) => {
            prev.session.name = event.target.value
            return {...prev}
        })
    }, [])

    const onChangeDescriptionHandler = useCallback((event) => {
        if(event.target.value.length > 250) return
        setState((prev) => {
            prev.session.description = event.target.value
            return {...prev}
        })
    }, [])

    const onChangeParticipantsHandler = useCallback((event) => {
        setState((prev) => {
            prev.session.limitOfParticipants = stringOnlyDigits(event.target.value)
            return {...prev}
        })
    }, [])

    const onChangeRadiusHandler = useCallback((event) => {
        setState((prev) => {
            prev.session.radius = stringOnlyDigits(event.target.value)
            return {...prev}
        })
    }, [])

    let name_warnings = []
    let limit_participants_warnings = []
    let radius_warnings = []
    if (state.warnings != null) {
        state.warnings.forEach((elem, idx) => {
            switch (elem.value) {
                case 'name': name_warnings.push(<Form.Text key={idx} className="text-danger">{elem.message}</Form.Text>); break;
                case 'limitOfParticipants': limit_participants_warnings.push(<Form.Text key={idx} className="text-danger">{elem.message}</Form.Text>); break;
                case 'radius': radius_warnings.push(<Form.Text key={idx} className="text-danger">{elem.message}</Form.Text>); break;
                default: break;
            }
        })
    }

    let radius_form = null
    if (state.session.geolocation != null && state.session.geolocation !== '') radius_form = <Form.Group>
        <Form.Label className="mb-2 mt-3">Radius (meters):</Form.Label>
        <Form.Control type="text" placeholder="Radius (min 50m)" disabled={!editable}
                      value={state.session.radius} maxLength="9" onChange={onChangeRadiusHandler}/>
        {radius_warnings}
    </Form.Group>

    return (
        <Form onSubmit={onSubmitHandler}>
            {state.session.name == null ? null : <Form.Group>
                <Form.Label>Session Name:</Form.Label>
                <Form.Control placeholder="Name" type="text" value={state.session.name}
                              onChange={onChangeNameHandler} disabled={!editable}/>
                {name_warnings}
            </Form.Group>}
            {state.session.description == null ? null : <Form.Group>
                <Form.Label>Session Description:</Form.Label>
                <Form.Control placeholder="Description" type="text" value={state.session.description}
                              onChange={onChangeDescriptionHandler} disabled={!editable}/>
            </Form.Group>}
            {state.session.status == null ? null : <Form.Group>
                <Form.Label>Session Status:</Form.Label>
                <Form.Control placeholder="Status" type="text" value={state.session.status}
                              readOnly disabled/>
            </Form.Group>}
            {state.session.limitOfParticipants == null ? null : <Form.Group>
                <Form.Label>Limit of Participants:</Form.Label>
                <Form.Control placeholder="Limit of Participants" type="text" maxLength="3"
                              value={state.session.limitOfParticipants} onChange={onChangeParticipantsHandler}
                              disabled={!editable}/>
                {limit_participants_warnings}
            </Form.Group>}
            {state.session.geolocation == null ? null : <Form.Group>
                <Form.Label className="mb-2 mt-3">Geolocation:</Form.Label>
                <Form.Control type="text" placeholder="Add Geolocation (optional)"
                              value={state.session.geolocation} readOnly disabled/>
                {
                    editable ?
                        state.session.geolocation === '' ?
                            <Button variant="outline-secondary" onClick={onClickUseLocation}>Use geolocation</Button> :
                            <Button variant="outline-secondary" onClick={onClickRemoveLocation}>Remove geolocation</Button>
                        : null
                }
            </Form.Group>}
            {radius_form}
            {
                props.close == null ? null :
                    <Button
                        variant={close.variant == null ? 'success' : close.variant}
                        className={close.className == null ? 'mt-3' : close.className}
                        onClick={clearSessionState}>{close.content == null ? 'Close' : close.content}</Button>
            }
            {
                !editable ? null :
                <Button variant={button.variant} type='submit' disabled={button.disabled} onClick={onSubmitHandler} className={button.className}>{button.content}</Button>
            }
        </Form>
    )
}