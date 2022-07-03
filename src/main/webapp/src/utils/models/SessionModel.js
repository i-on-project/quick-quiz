export function validateInputModel(input_model) {
    const validation_errors = []
    if (input_model.name.trim() === '') validation_errors.push({value: 'name', message: 'Name cannot be empty'})
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
        name: values.name,
        description: values.description,
        limitOfParticipants: isNaN(participants_limit) ? null : participants_limit,
        geolocation: geolocation,
        radius: radius
    }
}
