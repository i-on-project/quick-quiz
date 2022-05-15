import {Fragment} from "react";


export const ShortQuiz = ((props) => {

    return (
        <Fragment>
            <h5>{props.question}</h5>
            <input onChange={props.onChangeHandler}/>
        </Fragment>
    )
})