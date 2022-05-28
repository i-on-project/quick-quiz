import React, {Fragment, useEffect, useState} from "react";
import {Card, Container, Row} from "react-bootstrap";
import {useParams} from "react-router";
import SockJsClient from 'react-stomp';
import {goGET} from "../../Services/FetchService";


import {QuizAnswerCardInSession} from "../Quizzes/QuizAnswerCardInSession";

export const InSession = () => {
    const [answer, setAnswer] = useState(null)
    const [quizzes, setQuizzes] = useState(null)
    const [client, setClient] = useState(null)
    const {id} = useParams()


    const getQuizzes = () => {
        const setError = (error) => {
            console.log(`Failed to get session with ID: ${id} with error ${error}`)
        }
        const setQuestionsData = (data) => {
            //const tmp = data.entities.map(e => e.properties)
            setQuizzes(data.entities)
        }
        goGET(`/api/web/v1.0/non_auth/quiz/session/${id}`, setQuestionsData, setError)
    }
    useEffect(() => {
        /*Prevent reset state*/
        const setSessionError = (error) => {
            console.log(`Failed to get session with ID: ${id} with error ${error}`)
        }

        const getMeSession = (data) => {
            setAnswer(data)
            sendTestMessage()
            console.log(data)
        }
        goGET(`/api/web/v1.0/non_auth/answer/${id}`, getMeSession, setSessionError)
    }, [id])

    useEffect(() => {
        getQuizzes()

    }, [answer])

    const getQuizAnswer = (id) => answer.answers.find(a => a.quizId === id)

    const sendTestMessage = () => {
        client.sendMessage(`/app/orginsession/${answer.sessionId}`, JSON.stringify({
            name: 'Test Name',
            message: 'TEst MEssage'
        }));
    }

    return (
        <Fragment>
            <h1 className={"text-center mb-5 mt-3"}>Participant: {id}</h1>
            <Container>
                <Row>
                    {quizzes !== null && quizzes.length > 0 && answer !== null && (
                        quizzes.map(q => <QuizAnswerCardInSession key={q.properties.id}
                                                                  name={q.properties.question}
                                                                  data={q.properties}
                                                                  quizHref={q.href}
                                                                  reloadQuizzes={getQuizzes}
                                                                  answer={getQuizAnswer(q.properties.id)}
                                                                  answerId={id}
                                                                  messageOrganizer={sendTestMessage}
                            //sendTestMessage={sendTestMessage}
                        />)
                    )}
                </Row>
            </Container>


            {answer !== null && <SockJsClient url='http://localhost:8080/insession/'
                                              topics={[`/topic/insession/${answer.sessionId}`]}
                                              onConnect={() => {
                                                  console.log("connected");
                                              }}
                                              onDisconnect={() => {
                                                  console.log("Disconnected");
                                              }}
                                              onMessage={(msg) => {
                                                  console.log(msg);
                                                  getQuizzes()
                                              }}
                                              ref={(client) => setClient(client)
                                              } />
            }
        </Fragment>

    );
}

