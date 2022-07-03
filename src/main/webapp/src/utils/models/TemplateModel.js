export function validateInputModel(input_model) {
    const validation_errors = []
    const participants_limit = parseInt(input_model.limitOfParticipants)
    if (!isNaN(participants_limit)) {
        if (participants_limit === 0) validation_errors.push({
            value: 'limitOfParticipants',
            message: 'The limit of participants cannot be 0'
        })
        if (participants_limit > 200) validation_errors.push({
            value: 'limitOfParticipants',
            message: 'The limit of participants cannot exceed 200'
        })
        if (participants_limit < 0) validation_errors.push({
            value: 'limitOfParticipants',
            message: 'The limit of participants cannot be negative'
        })
    }
    if (input_model.geolocation !== '') {
        const radius = parseInt(input_model.radius)
        if (isNaN(radius)) validation_errors.push({value: 'radius', message: 'You need to specify a radius limit'})
        else {
            if (radius < 50) validation_errors.push({
                value: 'radius',
                message: 'The radius cannot be lower than 50 meters'
            })
        }
    }
    input_model.quizzes.forEach((elem, idx) => {
        switch(elem.answerType.trim()) {
            case 'SHORT': break
            case 'MULTIPLE_CHOICE': {
                if(elem.answerChoices == null || elem.answerChoices.length < 2) {
                    validation_errors.push({value: 'answerChoices', idx: idx, message: 'You need at least 2 choices'})
                    break
                }
                let correct_answer = false
                elem.answerChoices.forEach((elem, index) => {
                    if(elem.choiceRight === true) correct_answer = true
                    if(elem.choiceAnswer.trim() === '') {
                        validation_errors.push({value: 'answerChoices', idx: idx, message: `The answer ${index} is empty`})
                    }
                })
                if(correct_answer === false) {
                    validation_errors.push({value: 'answerChoices', idx: idx, message: 'You need a correct choice'})
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
    })
    return validation_errors
}

export function buildInputModel(values) {
    const participants_limit = parseInt(values.limitOfParticipants)
    let radius = null
    let geolocation = values.geolocation !== '' ? values.geolocation : null
    if (geolocation != null) {
        radius = parseInt(values.radius)
        const split = geolocation.split(",", 2)
        geolocation = `${split[0]},${split[1]}`
    }
    return {
        limitOfParticipants: isNaN(participants_limit) ? null : participants_limit,
        geolocation: geolocation,
        radius: radius,
        quizzes: values.quizzes
    }
}