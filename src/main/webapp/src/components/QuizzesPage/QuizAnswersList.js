import * as React from "react";
import {ListGroup, ProgressBar} from "react-bootstrap";

export const QuizAnswersList = ({quiz, answers}) => {

    if(answers == null || answers.length === 0) return <h5 className="mb-3">No answers available</h5>

    if(quiz.answerType === 'MULTIPLE_CHOICE') {
        const stats = new Array(quiz.answerChoices.length).fill(0);
        let ans_counter = 0
        answers.forEach(e => {
            const answer = e.properties.answers.find(elem => elem.quizId === quiz.id)
            if(answer != null) {
                stats[answer.answerNumber] += 1
                ++ans_counter
            }
        })
        const content = quiz.answerChoices.map((choice, idx) => {
            return <ListGroup.Item key={idx}>
                <div className={"bg-light d-flex justify-content-between"}><p>{choice.choiceAnswer}</p><p>{stats[choice.choiceNumber] + ' of ' + ans_counter}</p></div>
                <ProgressBar variant={choice.choiceRight === true ? "success" : "danger"} now={(stats[choice.choiceNumber] / ans_counter) * 100}/>
            </ListGroup.Item>
        })
        return <ListGroup variant="flush">{content}</ListGroup>
    }
    else {
        let ans_counter = 0
        const content = answers.map((e, idx) => {
            const answer = e.properties.answers.find(elem => elem.quizId === quiz.id)
            if(answer != null) {
                ++ans_counter
                return <ListGroup.Item key={idx}>{ans_counter}. {answer.answer}</ListGroup.Item>
            } else return null
        })
        return <ListGroup className="mt-3 mb-3 col-10">{content}</ListGroup>
    }
}