import { useState, useEffect } from 'react'

function App() {
  const [message, setMessage] = useState('Loading...')

  useEffect(() => {
    fetch('/api/hello')
        .then((response) => response.text())
        .then((text) => setMessage(text))
        .catch(() => setMessage('Could not reach the backend'))
  }, [])

  return (
      <div>
        <h1>EduLink</h1>
        <p>Backend says: {message}</p>
      </div>
  )
}

export default App