import {Alert, Collapse} from "react-bootstrap";
import {useState} from "react";

import "../css/ButtonAsLink.css"

export const Notification = (props) => {

    const [collapse, setCollapse] = useState(false)

    const {problem} = props
    if(problem == null) return

    const variant = props.variant == null ? "danger" : props.variant
    const onClose = props.onClose == null ? () => {} : props.onClose
    const dismissible = props.dismissible == null ? true : props.dismissible

    const reflection = Object.keys(problem)
    const more_details = reflection.map((elem, idx) => {
        return <p className={"mb-0"} key={idx}><strong>{reflection[idx]}: </strong>{Reflect.get(problem, reflection[idx])}</p>
    })

    return(
        <Alert variant={variant} onClose={onClose} dismissible={dismissible}>
            <Alert.Heading>{problem.type}</Alert.Heading>
            <p>{problem.detail == null ? problem.title : problem.detail}</p>
            <button className="button-as-link" onClick={() => setCollapse((prev) => !prev)} aria-controls="collapse-details" aria-expanded={collapse}>More details</button>
            <Collapse in={collapse}>
                <div id="collapse-details">{more_details}</div>
            </Collapse>
        </Alert>
    )
}