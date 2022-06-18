import { Fragment, useState } from "react";
import { ListGroup, Button, Collapse } from "react-bootstrap"
import { HistoryQuiz } from "./HistoryQuiz";

const HistoryDetails = (props) => {

    const [open, setOpen] = useState(false);

    let occurredTime =  props.item.properties.historyDate - props.item.properties.liveDate
    const hours = Math.floor(occurredTime / 3600);
    occurredTime %= 3600;
    const minutes = Math.floor(occurredTime / 60);
    const seconds = occurredTime % 60;

    let button = null;
    let collapse = null;

    if(props.item.properties.quizzes != null && props.item.properties.quizzes.length !== 0) {
        button = <Button onClick={() => setOpen((prevState) => !prevState)} aria-controls="collapse-quizzes" aria-expanded={open}>quizzes</Button> 
        collapse = <Collapse in={open}><div id="collapse-quizzes">{props.item.properties.quizzes.map((quiz) => <HistoryQuiz key={props.item.fields.find((elem) => elem.name === 'id').value} item={quiz}/>)}</div></Collapse>
    }

    return (
        <Fragment>
            <ListGroup horizontal>
                <ListGroup.Item className={"col-3"} key={0}>{props.item.properties.name}</ListGroup.Item>
                <ListGroup.Item className={"col-6"} key={1}>{props.item.properties.description}</ListGroup.Item>
                <ListGroup.Item className={"col-1"} key={2}>{props.item.properties.numberOfParticipants} of {props.item.properties.limitOfParticipants}</ListGroup.Item>
                <ListGroup.Item className={"col-1"} key={3}>{String(hours).padStart(2, '0')}:{String(minutes).padStart(2, '0')}:{String(seconds).padStart(2, '0')}</ListGroup.Item>
                {button}
            </ListGroup>
            {collapse}
        </Fragment>
    )
}

export default HistoryDetails