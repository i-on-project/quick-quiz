export function buildInputModel(id, sessionId, quizId, values) {
    const intChoice = parseInt(values.answerChoice)
    const toReturn = {
        guestId: id,
        sessionId: sessionId,
        quizId: quizId,
        answer: values.answer,
        answerChoice: isNaN(intChoice) ? null : intChoice
    }
    return toReturn
}