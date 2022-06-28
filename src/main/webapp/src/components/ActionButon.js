import * as React from "react"
import {useCallback, useState} from "react";
import {Button, Spinner} from "react-bootstrap";

export const ActionButton = (props) => {

    const {perform} = props
    const initial_content = props.content

    const [state, setState] = useState({content: initial_content, loading: false, disabled: false})

    const onClickHandler = useCallback(() => {
        setState({content: <Spinner animation={"border"}/>, loading: true, disabled: true})
        perform().then(() => setState({content: initial_content, loading: false, disabled: false}))
    }, [initial_content, perform])

    return <Button style={props.style} variant={props.variant} type={props.type} onClick={onClickHandler}
                   disabled={state.disabled} className={props.className}>{state.content}</Button>
}