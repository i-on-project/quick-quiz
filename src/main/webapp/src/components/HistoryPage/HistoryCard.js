import * as React from "react"
import {Fragment, useCallback, useState} from "react";
import {Button, ListGroup} from "react-bootstrap";
import {millisToTime} from "../../utils/TimeUtils";
import {HistoryQuiz} from "./HistoryQuiz";

export const HistoryCard = ({history}) => {

    const [collapse, setCollapse] = useState(false);
    const onclickHandler = useCallback(() => setCollapse(prev => !prev), [])

    return (
        <Fragment>
            <ListGroup horizontal>
                <ListGroup.Item className={"col-4"}>{history.name}</ListGroup.Item>
                <ListGroup.Item className={"col-6"}>{history.description}</ListGroup.Item>
                <ListGroup.Item className={"col-1"}>{history.numberOfParticipants} of {history.limitOfParticipants}</ListGroup.Item>
                <ListGroup.Item className={"col-1"}>{millisToTime((history.historyDate - history.liveDate) * 1000)}</ListGroup.Item>
                {history.quizzes != null && history.quizzes.length !== 0 ?
                    <Button className="col-1" variant="success" onClick={onclickHandler}>Quizzes {collapse === false ? "⮝" : "⮟"}</Button> :
                    <Button className="col-1" variant="success" disabled={true}>Quizzes ⮝</Button>
                }
            </ListGroup>
            {collapse === false ? null :
                history.quizzes.map((elem, idx) => <HistoryQuiz key={idx} quiz={elem}/>)
            }
        </Fragment>
    )
}
