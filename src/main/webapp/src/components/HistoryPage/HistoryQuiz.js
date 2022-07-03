import * as React from "react";
import {Fragment} from "react";
import {Card, ListGroup, ProgressBar} from "react-bootstrap";

export const HistoryQuiz = ({quiz}) => {

    let content = <p>No answers</p>

    if(quiz.answerType === "MULTIPLE_CHOICE") {
        const stats = new Array(quiz.answerChoices.length).fill(0);
        quiz.answers.forEach(elem => stats[elem.choiceNumber] += 1)

        content = quiz.answerChoices.map((elem, idx) => {
            return <Fragment key={idx}>
                <div className={"bg-light d-flex justify-content-between"}><p>{elem.choiceAnswer}</p><p>{stats[elem.choiceNumber] + ' of ' + quiz.answers.length}</p></div>
                <ProgressBar variant={elem.choiceRight === true ? "success" : "danger"} now={(stats[elem.choiceNumber] / quiz.answers.length) * 100}/>
            </Fragment>
        })
    } else if(quiz.answerType === 'SHORT' || quiz.answerType === 'LONG') {
        content = <ListGroup variant="flush">
            {quiz.answers.map((elem, idx) => <ListGroup.Item key={idx}>{elem.answer}</ListGroup.Item>)}
        </ListGroup>
    }

    return (
        <Fragment>
            <Card>
                <Card.Title>{quiz.question}</Card.Title>
                <Card.Body>{content}</Card.Body>
            </Card>
        </Fragment>
    )
}