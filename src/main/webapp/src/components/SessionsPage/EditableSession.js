import * as React from "react";
import {Fragment, useCallback, useState} from "react";
import {Button} from "react-bootstrap";
import {parse_body, request_no_content} from "../../utils/Request";
import {SessionForm} from "./SessionForm";
import {Notification} from "../Notification";

export const EditableSession = ({session, href, reload}) => {

    const [state, setState] = useState({editable: false})

    const updateSession = useCallback((input_model) => {
        setState((prev) => { return {...prev, editable: false}})
        const s_func = () => {
            reload()
        }
        const f_func = (problem) => {
            setState((prev) => {
                return {...prev, problem: problem}
            })
        }
        const func_obj = {success: s_func, failed: f_func}
        request_no_content(href, {method: 'PUT', ...parse_body(input_model)}, func_obj)
    }, [href, reload])

    const onClickEditHandler = useCallback(() => {
        setState((prev) => { return {...prev, editable: true}})
    }, [])

    const onClickCloseHandler = useCallback(() => {
        setState((prev) => {return {...prev, editable: false}})
    }, [])

    const onCloseHandler = useCallback(() => {
        setState((prev) => { return {...prev, problem: null}})
    }, [])

    let buttons_content = null
    let close = null
    if(!state.editable) buttons_content = <Button variant="success" className="mt-3" onClick={onClickEditHandler}>Edit</Button>
    else close = {variant: "success", className: "mt-3", func: onClickCloseHandler, content: 'Close'}


    return (
        <Fragment>
            <Notification problem={state.problem} onClose={onCloseHandler}/>
            <SessionForm perform={updateSession} editable={state.editable} close={close}
                         session={session} button={{content: "SaveChanges", className: "ms-3 mt-3"}}/>
            {buttons_content}
        </Fragment>
    )
}