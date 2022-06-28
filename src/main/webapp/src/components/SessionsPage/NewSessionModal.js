import * as React from "react"
import {Fragment, useCallback, useState} from "react";
import {Modal} from "react-bootstrap";
import {parse_body, request_no_content} from "../../utils/Request";
import {Notification} from "../Notification";
import {SessionForm} from "./SessionForm";

export const NewSessionModal = (props) => {

    const [state, setState] = useState({problem: null})

    const createSession = useCallback((input_model) => {
        const s_func = () => {
            props.reload()
            props.onClose()
        }
        const f_func = (problem) => {
            setState((prev) => {
                return {...prev, problem: problem}
            })
        }
        const func_obj = {success: s_func, failed: f_func}
        request_no_content(props.href, {method: 'POST', ...parse_body(input_model)}, func_obj)
    }, [props])

    const onCloseHandler = useCallback(() => {
        setState((prev) => {
            return {...prev, problem: null}
        })
    }, [])

    return (
        <Fragment>
            <Notification problem={state.problem} onClose={onCloseHandler}/>
            <Modal.Header closeButton onClick={props.onClose}>
                <Modal.Title>Create a new Session</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <SessionForm perform={createSession} session={{
                    name: '',
                    description: '',
                    limitOfParticipants: '',
                    geolocation: '',
                    radius: '',
                }} button={{content: "Create"}}/>
            </Modal.Body>
        </Fragment>
    )
}