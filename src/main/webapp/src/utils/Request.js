import {ProblemJson} from "./ProblemJson";

export const cancellable_fetch = (uri, opts) => {
    const controller = new AbortController();
    const signal = controller.signal;

    return {
        cancel: () => controller.abort(),
        fetch: fetch(uri, { ...opts, signal }),
        signal: signal
    };
}

export function parse_body(body) {
    return {headers: {'Content-Type': 'application/json'}, body: JSON.stringify(body)}
}

export const aborted_problem = new ProblemJson('Aborted', 'This operation was aborted')
export const invalid_response = new ProblemJson('InvalidResponse', 'The API response was not valid')
export function request(uri, opts, {success, failed}) {
    const fetch_obj = cancellable_fetch(uri, opts)
    const promise = fetch_obj.fetch.then((response) => {
        if(fetch_obj.signal.aborted) { failed(aborted_problem); return }
        const content_type = response.headers.get('Content-Type')
        if(content_type == null || !content_type.includes('json')) { failed(invalid_response); return }
        response.json().then((data) => {
            if(fetch_obj.signal.aborted) { failed(aborted_problem); return }
            if(response.ok) success(data)
            else failed(data)
        })
    }).catch((error) => {failed(ProblemJson.error_constructor(error))})
    return {cancel: fetch_obj.cancel, fetch: promise, signal: fetch_obj.signal}
}

export function request_no_content(uri, opts, {success, failed}) {
    const fetch_obj = cancellable_fetch(uri, opts)
    const promise = fetch_obj.fetch.then((response) => {
        if(fetch_obj.signal.aborted) { failed(aborted_problem); return }
        if(response.ok) success()
        else response.json().then((data) => {
            if(fetch_obj.signal.aborted) { failed(aborted_problem); return }
            if(response.ok) success(data)
            else failed(data)
        })
    }).catch((error) => {failed(ProblemJson.error_constructor(error))})
    return {cancel: fetch_obj.cancel, fetch: promise, signal: fetch_obj.signal}
}

/*export function authentication_required_request(uri, opts, {success, failed}) {
    const fetch_obj = cancellable_fetch(uri, opts)
    fetch_obj.fetch.then((response) => {
        if(fetch_obj.signal.aborted) { failed(aborted_problem); return }
        const content_type = response.headers.get('Content-Type')
        if(content_type == null || !content_type.includes('json')) { failed(invalid_response); return }
        if(response.status === 403) { Logout(); return }
        response.json().then((data) => {
            if(fetch_obj.signal.aborted) { failed(aborted_problem); return }
            if(response.ok) success(data)
            else failed(data)
        })
    }).catch((error) => failed(ProblemJson.error_constructor(error)))
    return fetch_obj.cancel
}*/