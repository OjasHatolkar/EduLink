# EduLink

Paste your notes on two different subjects and EduLink finds the ideas that connect them, then pulls up real research papers from arXiv about those connections.

## What it does

- **Finds connections.** An LLM reads your notes on both subjects and proposes bridging concepts, each with a short explanation grounded in what you wrote.
- **Finds papers.** Each concept becomes an arXiv search, and the matching papers (title, authors, date, abstract, link) are shown under that concept.
- **Project Generator.** Pick your skill level, time, and team size (plus any extra preferences) and get a project idea that uses both subjects, with a tool list, a getting-started section, and concrete steps.
- **Ed the T-Rex.** Click Ed in the corner for a short, friendly, pun-filled summary of the key concepts in your notes.

## Quick start (run the packaged JAR)

**Requirements:** Java 21 or newer, and a [Featherless AI API key](https://featherless.ai/account/api-keys).

1. Download the latest JAR from the [Releases page](../../releases).
2. In the same folder as the JAR, create a file named `.env` containing one line (no quotes, no spaces around the `=`):

   ```
   FEATHERLESS_API_KEY=your-key-here
   ```

   Make sure the file is named exactly `.env` and not `.env.txt`. See `.env.example` in this repo for the template.
3. Run it:

   ```
   java -jar edu-link-0.0.1-SNAPSHOT.jar
   ```
4. Open <http://localhost:8080>.

Instead of a `.env` file, you can set the key in your terminal first:

```powershell
# Windows PowerShell
$env:FEATHERLESS_API_KEY="your-key-here"
```

```bash
# macOS / Linux
export FEATHERLESS_API_KEY=your-key-here
```

### Good to know

- **A search takes roughly 15 to 25 seconds.** arXiv asks API users to wait about 3 seconds between requests, and EduLink respects that, so the button will say "Finding connections..." for a while.
- **Notes are limited to 8,000 characters per subject.**
- Use a different port with `--server.port=9090` after the JAR name.
- Use a different model with `--featherless.model=<model-id>` after the JAR name. The default is `Qwen/Qwen2.5-7B-Instruct`.

### Troubleshooting

| Problem | Likely cause and fix |
|---|---|
| `Could not resolve placeholder 'FEATHERLESS_API_KEY'` | The key wasn't found. Check the `.env` name and location (next to the JAR, and run the command from that folder), or set the environment variable. |
| `Port 8080 was already in use` | Another program (or an earlier copy of EduLink) is using the port. Stop it, or use `--server.port`. |
| `UnsupportedClassVersionError` | Your Java is too old. Install Java 21 or newer. |
| You see an old version of the page after updating | Your browser cached it. Hard refresh with Ctrl+Shift+R (Cmd+Shift+R on Mac). |
| "The AI service didn't return usable results" | The model returned something unusable twice in a row, or the API call failed. Try again, and check the console for the reason. |

## Run from source (development)

**Requirements:** JDK 21+, Node.js 20.19+ (or 22.12+), and the `.env` file described above in the repo root.

Start the backend (port 8080):

```bash
cd backend
./mvnw spring-boot:run      # on Windows: .\mvnw.cmd spring-boot:run
```

Start the frontend (port 5173) in a second terminal:

```bash
cd frontend
npm install
npm run dev
```

Open <http://localhost:5173>. The Vite dev server proxies `/api` requests to the backend on port 8080.

## Build the JAR yourself

On Windows, from the repo root:

```powershell
.\build.ps1
```

The script builds the React app, copies it into the Spring Boot project's static resources, and packages everything into one runnable JAR in `backend/target/`. Use the JAR that does not end in `.original`.

## How it works

1. **Connections.** The backend sends both sets of notes to a Featherless-hosted model and asks for bridging concepts, each with an explanation and an academic search query, returned as JSON.
2. **Papers.** For each query, the backend searches the arXiv API (every keyword must match, sorted by relevance, at most one request every 3 seconds) and attaches up to five papers per query. Papers are de-duplicated across concepts.
3. **Extras.** The project generator and Ed use the same LLM service with their own prompts.

### API

| Endpoint | Purpose |
|---|---|
| `POST /api/connect` | Notes for two subjects in, connections and papers out |
| `POST /api/project-idea` | Concepts and constraints in, a project idea out |
| `POST /api/summary` | Notes for two subjects in, Ed's summary out |

## Known limitations

- LLM output can be imperfect or generic. Treat the connections and project ideas as starting points, and verify any facts (including Ed's fun facts) before relying on them.
- Paper relevance depends on arXiv's keyword search, so some queries return few or no results.
- Your Featherless plan may limit how many requests can run at once.
- There is no database, so nothing is saved between searches.

## Tech stack

- **Frontend:** React and Vite
- **Backend:** Java 21 and Spring Boot
- **LLM:** [Featherless AI](https://featherless.ai) (default model: Qwen2.5-7B-Instruct)
- **Research data:** the [arXiv API](https://info.arxiv.org/help/api/)

## Credits

- Ed the T-Rex icon: <a href="https://www.flaticon.com/free-icons/dinosaur" title="dinosaur icons">Dinosaur icons created by Magnific - Flaticon</a>
- Thank you to arXiv for use of its open access interoperability.
- LLM inference provided through Featherless AI.
