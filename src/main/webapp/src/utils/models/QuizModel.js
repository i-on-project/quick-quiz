export const questionTypeMapper = [
    {value: "Short Text", key: "SHORT"},
    {value: "Multiple Choice", key: "MULTIPLE_CHOICE"},
    {value: "Complex", key: "LONG"}
]

export const quizStateMapper = [
    {value: "Not started", key: "NOT_STARTED"},
    {value: "Live", key: "STARTED"},
    {value: "Closed", key: "CLOSED"}
]

export function validateInputModel(input_model) {
    const validation_errors = []
    if (input_model.question.trim() === '') {
        validation_errors.push({value: 'question', message: 'Question cannot be empty'})
    }
    const order = input_model.order !== '' ? parseInt(input_model.order) : 0
    if(isNaN(order)) validation_errors.push({value: 'order', message: 'Order should be a number'})
    else {
        if (order > 99) validation_errors.push({value: 'order', message: 'The order cannot exceed 99'})
        if (order < 0) validation_errors.push({value: 'order', message: 'The order cannot be negative'})
    }
    switch(input_model.questionType) {
        case 'SHORT': break
        case 'MULTIPLE_CHOICE': {
            if(input_model.choices == null || input_model.choices.length < 2) {
                validation_errors.push({value: 'choices', message: 'You need at least 2 choices'})
                break
            }
            let correct_answer = false
            input_model.choices.forEach((elem, idx) => {
                if(elem.choiceRight === true) correct_answer = true
                if(elem.choiceAnswer.trim() === '') {
                    validation_errors.push({value: 'choices', message: `The answer ${idx} is empty`})
                }
            })
            if(correct_answer === false) {
                validation_errors.push({value: 'choices', message: 'You need a correct choice'})
            }
            break
        }
        case 'LONG': break
        case '': {
            validation_errors.push({value: 'questionType', message: 'You need to specify a type of answer'})
            break
        }
        default: validation_errors.push({value: 'questionType', message: `${input_model.questionType} is not a valid type`})
    }
    return validation_errors
}

export function buildInputModel(values) {
    const order = parseInt(values.order)
    let choices = null
    if(values.choices != null) choices = values.choices.map((elem, idx) => {
        return {...elem, choiceNumber: idx}
    })
    return {
        question: values.question,
        order: isNaN(order) ? null : order,
        choices: choices,
        questionType: values.questionType
    }
}

export function SortQuizzesEntities(e1, e2) {
    return e1.properties.order - e2.properties.order
}