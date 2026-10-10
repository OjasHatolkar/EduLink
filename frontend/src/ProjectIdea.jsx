import { useRef, useState } from 'react'
import { postJson } from './api'

const SKILL_LEVELS = ['Beginner', 'Intermediate', 'Advanced']
const TIME_OPTIONS = ['A few hours', 'A weekend', '1-2 weeks', 'A month or more']
const TEAM_SIZES = ['Just me', '2-3 people', '4+ people']
const MAX_EXTRA_LENGTH = 500

function ProjectIdea({ connections, nameA, nameB }) {
    const dialogRef = useRef(null)
    const [skillLevel, setSkillLevel] = useState('Intermediate')
    const [timeAvailable, setTimeAvailable] = useState('A weekend')
    const [teamSize, setTeamSize] = useState('Just me')
    const [extra, setExtra] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')
    const [idea, setIdea] = useState(null)

    async function handleGenerate(event) {
        event.preventDefault()
        dialogRef.current.close()
        setLoading(true)
        setError('')
        setIdea(null)

        try {
            const data = await postJson('/api/project-idea', {
                subjectAName: nameA,
                subjectBName: nameB,
                concepts: connections.map((c) => ({
                    concept: c.concept,
                    explanation: c.explanation,
                })),
                constraints: { skillLevel, timeAvailable, teamSize, extra },
            })
            setIdea(data)
        } catch (e) {
            setError(e.message)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="project-idea">
            <button
                type="button"
                disabled={loading}
                onClick={() => dialogRef.current.showModal()}
            >
                {loading
                    ? 'Generating...'
                    : idea
                        ? 'Generate another idea'
                        : 'Generate a project idea'}
            </button>

            <dialog ref={dialogRef}>
                <form onSubmit={handleGenerate}>
                    <h3>Project constraints</h3>

                    <label>
                        Skill level
                        <select value={skillLevel} onChange={(e) => setSkillLevel(e.target.value)}>
                            {SKILL_LEVELS.map((option) => (
                                <option key={option}>{option}</option>
                            ))}
                        </select>
                    </label>

                    <label>
                        Time available
                        <select value={timeAvailable} onChange={(e) => setTimeAvailable(e.target.value)}>
                            {TIME_OPTIONS.map((option) => (
                                <option key={option}>{option}</option>
                            ))}
                        </select>
                    </label>

                    <label>
                        Team size
                        <select value={teamSize} onChange={(e) => setTeamSize(e.target.value)}>
                            {TEAM_SIZES.map((option) => (
                                <option key={option}>{option}</option>
                            ))}
                        </select>
                    </label>

                    <label>
                        Anything else? (optional)
                        <textarea
                            rows={3}
                            maxLength={MAX_EXTRA_LENGTH}
                            placeholder="Preferred tools, interests, things to avoid..."
                            value={extra}
                            onChange={(e) => setExtra(e.target.value)}
                        />
                    </label>

                    <div className="dialog-actions">
                        <button type="submit">Generate</button>
                        <button
                            type="button"
                            className="secondary"
                            onClick={() => dialogRef.current.close()}
                        >
                            Cancel
                        </button>
                    </div>
                </form>
            </dialog>

            {error && <p className="error">{error}</p>}

            {idea && (
                <article className="idea">
                    <h3>{idea.title}</h3>
                    <p>{idea.summary}</p>
                    {idea.techStack.length > 0 && (
                        <>
                            <h4>Tools and skills</h4>
                            <ul>
                                {idea.techStack.map((item, index) => (
                                    <li key={index}>{item}</li>
                                ))}
                            </ul>
                        </>
                    )}

                    {idea.gettingStarted && (
                        <>
                            <h4>Getting started</h4>
                            <p>{idea.gettingStarted}</p>
                        </>
                    )}
                    <h4>Steps</h4>
                    <ol>
                        {idea.steps.map((step, index) => (
                            <li key={index}>{step}</li>
                        ))}
                    </ol>
                    {idea.stretchGoal && (
                        <p>
                            <strong>Stretch goal:</strong> {idea.stretchGoal}
                        </p>
                    )}
                </article>
            )}
        </div>
    )
}

export default ProjectIdea