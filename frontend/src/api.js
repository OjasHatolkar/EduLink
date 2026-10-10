export async function postJson(url, body) {
    let response
    try {
        response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
        })
    } catch {
        throw new Error('Could not reach the server. Is the backend running?')
    }

    if (!response.ok) {
        let message = 'Something went wrong. Please try again.'
        try {
            const data = await response.json()
            if (data.error) message = data.error
        } catch {
            // The error body wasn't JSON, so keep the default message.
        }
        throw new Error(message)
    }

    return response.json()
}