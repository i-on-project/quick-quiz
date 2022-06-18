import React, {useState} from "react";
import {FormControl, InputGroup} from "react-bootstrap";
import Button from "react-bootstrap/Button";

export const EditableInput = (props) => {
    const [edit, setEdit] = useState(false)

    const clickHandler = () => {
        setEdit((e) => !e)
    }

    return (
        <InputGroup className="mb-3">
            <FormControl
                placeholder="this is a field"
                aria-label="Recipient's username"
                aria-describedby="basic-addon2"
                value={props.value}
            />
            <Button variant="outline-secondary" id="button-addon2" onClick={clickHandler}>
                {!edit && ("Edit")}
                {edit && ("Save")}
            </Button>
        </InputGroup>
    )
}