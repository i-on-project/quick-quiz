import {Fragment} from "react";
import {FormControl, InputGroup} from "react-bootstrap";


export const LongQuiz = ((props) => {

    return (
        <Fragment>
            <h5>{props.question}</h5>
            <InputGroup>
                
                <FormControl as="textarea" aria-label="With textarea" onChange={props.onChangeHandler}/>
            </InputGroup>
        </Fragment>
    )
})