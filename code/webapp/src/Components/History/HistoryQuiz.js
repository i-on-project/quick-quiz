import { Fragment } from "react";
import { Card, ListGroup, ProgressBar, Row } from "react-bootstrap";

export const HistoryQuiz = (props) => {
    
    let content = <p>No answers</p>

    if(props.item.answerType === "MULTIPLE_CHOICE") {
        const stats = new Array(props.item.answerChoices.length).fill(0);
        props.item.answers.forEach((ans) => stats[ans.choiceNumber] += 1)

        content = props.item.answerChoices.map((choice, idx) => {
            let color = "danger"
            if(choice.choiceRight) color = "success"
            
            return <ListGroup variant="flush"><ListGroup.Item key={idx}>
                <div className={"bg-light d-flex justify-content-between"}><p>{choice.choiceAnswer}</p><p>{stats[choice.choiceNumber] + ' of ' + props.item.answers.length}</p></div>
                <ProgressBar variant={color} now={(stats[choice.choiceNumber] / props.item.answers.length) * 100}/>    
            </ListGroup.Item></ListGroup>
        })
    }

    if(props.item.answerType === "SHORT" || props.item.answerType === "LONG") {
        content = <ListGroup variant="flush">{props.item.answers.map((ans, idx) => <ListGroup.Item key={idx}>{ans.answer}</ListGroup.Item>)}</ListGroup>
    }

    return (
        <Fragment>
            <Card>
                <Card.Header>Question</Card.Header>
                <Card.Body>{props.item.question}</Card.Body>
            </Card>
            <Card>
                <Card.Header>Answers</Card.Header>
                <Card.Body>{content}</Card.Body>
            </Card>
        </Fragment>
    )
}