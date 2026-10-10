import { useState } from 'react'
import './App.css'

const MAX_NOTES_LENGTH = 8000

function SubjectInput({ label, name, notes, onNameChange, onNotesChange }) {
    return (
        <div className="subject">
            <h2>{label}</h2>
            <input
                type="text"
                placeholder="Subject name (optional)"
                value={name}
                onChange={(e) => onNameChange(e.target.value)}
            />
            <textarea
                placeholder="Paste or type your notes here..."
                rows={12}
                maxLength={MAX_NOTES_LENGTH}
                value={notes}
                onChange={(e) => onNotesChange(e.target.value)}
            />
            <p className="counter">
                {notes.length} / {MAX_NOTES_LENGTH}
            </p>
        </div>
    )
}

function App() {
    const [nameA, setNameA] = useState('')
    const [notesA, setNotesA] = useState('')
    const [nameB, setNameB] = useState('')
    const [notesB, setNotesB] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const [result, setResult] = useState(null)

    async function handleSubmit(event) {
        event.preventDefault()
        setLoading(true)
        setError('')
        setResult(null)

        try {
            const response = await fetch('/api/connect', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    subjectA: { name: nameA, notes: notesA },
                    subjectB: { name: nameB, notes: notesB },
                }),
            })

            if (!response.ok) {
                let message = 'Something went wrong. Please try again.'
                try {
                    const body = await response.json()
                    if (body.error) message = body.error
                } catch {
                    // The error body wasn't JSON, so keep the default message.
                }
                setError(message)
                return
            }

            setResult(await response.json())
        } catch {
            setError('Could not reach the server. Is the backend running?')
        } finally {
            setLoading(false)
        }
    }

    return (
        <main className="app">
            <h1>EduLink</h1>
            <p className="tagline">
                Paste notes on two subjects and find research papers that connect them.
            </p>

            <form onSubmit={handleSubmit}>
                <div className="subjects">
                    <SubjectInput
                        label="Subject A"
                        name={nameA}
                        notes={notesA}
                        onNameChange={setNameA}
                        onNotesChange={setNotesA}
                    />
                    <SubjectInput
                        label="Subject B"
                        name={nameB}
                        notes={notesB}
                        onNameChange={setNameB}
                        onNotesChange={setNotesB}
                    />
                </div>
                <button type="submit" disabled={loading}>
                    {loading ? 'Finding connections...' : 'Find connections'}
                </button>
            </form>

            {error && <p className="error">{error}</p>}

            {result && (
                <section className="results">
                    <h2>Connections</h2>

                    {result.warnings?.length > 0 && (
                        <p className="warning">{result.warnings.join(' ')}</p>
                    )}

                    {result.connections.map((connection) => (
                        <article key={connection.concept} className="connection">
                            <h3>{connection.concept}</h3>
                            <p>{connection.explanation}</p>
                            <p className="queries">
                                Searched: {connection.queries.join(' · ')}
                            </p>

                            {connection.papers.length === 0 ? (
                                <p className="meta">No papers found for this concept.</p>
                            ) : (
                                <ul>
                                    {connection.papers.map((paper) => (
                                        <li key={paper.url}>
                                            <a href={paper.url} target="_blank" rel="noreferrer">
                                                {paper.title}
                                            </a>
                                            <div className="meta">
                                                {paper.authors.join(', ')} · {paper.published}
                                            </div>
                                            <p className="summary">{paper.summary}</p>
                                        </li>
                                    ))}
                                </ul>
                            )}
                        </article>
                    ))}
                </section>
            )}
        </main>
    )
}

export default App