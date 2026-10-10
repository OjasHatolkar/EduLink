import { useState } from 'react'
import { postJson } from './api'
import edImage from './assets/ed.png'

function Mascot({ nameA, notesA, nameB, notesB }) {
    const [open, setOpen] = useState(false)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const [summary, setSummary] = useState('')
    const [summarizedKey, setSummarizedKey] = useState('')

    const hasNotes = notesA.trim() !== '' && notesB.trim() !== ''
    const inputsKey = JSON.stringify([nameA, notesA, nameB, notesB])

    async function handleClick() {
        if (open) {
            setOpen(false)
            return
        }
        setOpen(true)

        // Skip the request if there's nothing to summarize, one is running,
        // or we already summarized exactly these notes.
        if (!hasNotes || loading || summarizedKey === inputsKey) return

        setLoading(true)
        setError('')
        try {
            const data = await postJson('/api/summary', {
                subjectA: { name: nameA, notes: notesA },
                subjectB: { name: nameB, notes: notesB },
            })
            setSummary(data.summary)
            setSummarizedKey(inputsKey)
        } catch (e) {
            setError(e.message)
        } finally {
            setLoading(false)
        }
    }

    let content
    if (loading) {
        content = <p>Ed is flipping through your notes...</p>
    } else if (!hasNotes) {
        content = <p>Add notes for both subjects and I'll sum them up for you!</p>
    } else if (error) {
        content = <p className="error">{error}</p>
    } else {
        content = summary
            .split(/\n+/)
            .filter((paragraph) => paragraph.trim() !== '')
            .map((paragraph, index) => <p key={index}>{paragraph}</p>)
    }

    return (
        <div className="mascot">
            {open && (
                <div className="mascot-bubble" role="status">
                    {content}
                </div>
            )}
            <button
                type="button"
                className="mascot-button"
                onClick={handleClick}
                aria-label="Ask Ed the T-Rex to summarize your notes"
                aria-expanded={open}
            >
                <img src={edImage} alt="Ed the T-Rex" />
            </button>
        </div>
    )
}

export default Mascot