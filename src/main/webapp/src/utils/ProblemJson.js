export class ProblemJson {
    constructor(type, title) {
        this.status = -1
        this.title = title
        this.type = type
    }

    static error_constructor(error) {
        return new ProblemJson(error.name, error.message)
    }
}