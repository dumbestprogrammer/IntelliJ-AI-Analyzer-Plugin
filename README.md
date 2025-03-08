# AI Code Analyzer - IntelliJ Plugin
A powerful AI-powered code analysis plugin for IntelliJ IDEA.
<br>

## > Overview
- AI Code Analyzer is a full-fledged IntelliJ plugin that integrates **Large Language Models (LLMs)** to analyze Java code, detect issues, and suggest fixes. 
- It helps developers identify syntax errors, formatting inconsistencies, and potential bugs without leaving the IDE.

<br>

## > Features
- **AI-Powered Code Analysis -** Scans Java files and detects syntax errors, logical mistakes, and best practice violations.
- **Issue Highlighting -** Categorizes issues into errors, warnings, and suggestions.
- **Fix Suggestions -** Provides context-aware fixes for detected issues.
- **IntelliJ IDE Integration -** Runs as a tool inside IntelliJ.
- **On-Demand Analysis -** Manually trigger analysis via the Tools menu.


<br>


## > Usage
### 1. Open any Java project in IntelliJ IDEA.
### 2. Go to Tools → Run AI Code Analysis.
### 3. The AnalysisResultsDialog will display issues and suggested fixes.


<br>
<br>


## > Technical Details
### Core Components

- **CodeAnalyzerAction.java -** Handles action triggering.
- **LLMApiClient.java -** Communicates with the LLM API.
- **LLMResponseParser.java -** Parses AI responses into structured issues.
- **AnalysisResultsDialog.java -** Displays issues in a UI dialog.
- **ProjectScanner.java -** Scans project files for analysis.

<br>

## > Tech Stack
- **Language:** Java
- **Framework:** IntelliJ Platform SDK
- **Build Tool:** Gradle
- **Logging:** SLF4J + Logback
- **Parsing:** Gson, Jackson


<br>

## 📌 Class-by-Class Breakdown

### 🔹 1. Issue.java (Defines a single issue found in the code)
This class represents a detected issue in the analyzed code.
<br>

It contains metadata like:
<br>


- **Line number (int line) →** Where the issue occurred.
- **Issue type (String type) →** Syntax Error, Code Smell, etc.
- **Description (String description) →** Explanation of the issue.
- **Suggestion (String suggestion) →** AI-recommended fix.
- **File reference (VirtualFile file) →** Points to the actual file.

<br>

#### 📌 How it works:

An Issue object is created for each problem detected by the LLM. <br>

These objects are later collected in lists and displayed in the UI.

<br>
<br>

### 🔹 2. ProjectScanner.java (Scans the project for Java files)
This class scans the user’s IntelliJ project to find Java source files for analysis.

#### 📌 Key Features:

- Excludes non-code directories (.idea, build, etc.).
- Avoids large files (>20MB files are ignored).
- Filters out test files (unless includeTests is true).
- Uses IntelliJ's PSI (Program Structure Interface) to analyze the code structure.

<br>

#### 📌 How it works:

- Iterates through project files (ProjectFileIndex.iterateContent).
- Checks if the file is valid (Java, non-hidden, within size limits).
- Returns a list of files that will be sent to AI for analysis.

<br>
<br>

### 🔹 3. CodeAnalyzerAction.java (Main entry point—triggers AI analysis)
This class is the heart of the plugin. It’s an IntelliJ action that:
<br>

- Extracts project files using ProjectScanner.
- Calls OpenAI via LLMApiClient to analyze each file.
- Parses results and displays them in a UI dialog.

<br>

#### 📌 Key Features:

- Runs in the background (Task.Backgroundable) to prevent UI freezing.
- Uses multi-threading (CompletableFuture) for fast execution.
- Displays results in AnalysisResultsDialog.java.

<br>

#### 📌 How it works:

- Scans project (ProjectScanner.scanProject()).
- Sends code to AI (LLMApiClient.analyzeCode()).
- Processes AI response (LLMResponseParser.parseResponse()).
- Shows results in UI (AnalysisResultsDialog.show()).

<br>
<br>

### 🔹 4. LLMApiClient.java (Handles API communication with OpenAI)
This class sends code to OpenAI and retrieves analysis results.

- Uses Java’s HttpClient for API calls.
- Sends code as JSON in a structured system-user message format.
- Implements retry logic to handle API failures.

<br>

#### 📌 How it works:

- Formats request (createMessagesArray() → JSON).
- Sends it to OpenAI (executeWithRetry()).
- Receives response (validateResponse()).
- Extracts relevant data (parseOpenAIResponse()).

<br>

#### 📌 Why this is cool:

- Uses async calls (CompletableFuture) for non-blocking API requests.
- Handles error cases (invalid API key, rate limits, etc.).

<br>
<br>

### 🔹 5. LLMResponseParser.java (Parses AI’s JSON response)
Once OpenAI sends back a JSON response, this class:

- Extracts the “issues” array (parsing raw AI output).
- Creates Issue objects for each problem detected.

<br>

#### 📌 How it works:

- Parses JSON response → parseResponse().
- Extracts issues safely → parseIssues().
- Handles malformed responses (AI returning unexpected text).

<br>
<br>

### 🔹 6. AnalysisResultsDialog.java (UI that displays the issues)
- This is the front-end UI that shows analysis results.

- Uses a tree view to categorize issues by type.
- Allows manual selection of fixes.
- Provides an "Apply Fix" button for user action.

<br>

#### 📌 How it works:

- Receives issues list and categorizes them.
- Displays a tree structure (createResultsTree()).
- Shows issue details on selection (handleTreeSelection()).
- Handles user actions (showDialog()).

<br>

#### 📌 Why this is cool:

- Uses IntelliJ’s UI components (JBList, JBPanel, JBScrollPane).
- Supports dark/light mode (JBColor).
- Non-blocking—uses ApplicationManager.invokeLater() for UI updates.

<br>
<br>

### 🔹 7. NotificationUtil.java (Handles notifications)
This class manages IntelliJ notifications:

- Shows info pop-ups when analysis completes.
- Displays error messages if something goes wrong.


<br>

#### 📌 How it works:

- Creates notifications (showInfo(), showError()).
- Uses IntelliJ's NotificationGroupManager.

<br>
<br>

## ⚙️ How Everything Works Together
**Here’s the full workflow of your plugin in action:**

### 1️⃣ User runs "AI Code Analysis" action

- `CodeAnalyzerAction.actionPerformed()` is triggered.
- The analysis starts in the background `(Task.Backgroundable)`.

<br>

### 2️⃣ Project files are scanned
- `ProjectScanner.scanProject()` finds all relevant Java files.

<br>

### 3️⃣ Files are sent to OpenAI
- `LLMApiClient.analyzeCode()` sends each file’s content to the API.
- The AI responds with **JSON** containing detected issues.

<br>

### 4️⃣ AI’s response is parsed
- `LLMResponseParser.parseResponse()` extracts issues into Issue objects.

<br>

### 5️⃣ Results are displayed in UI
- `AnalysisResultsDialog.show()` presents a tree structure of issues.
- Users can manually review and apply fixes.

<br>

### 6️⃣ User gets notified
- If no issues, `NotificationUtil.showInfo()` shows "No issues found."
- If errors occur, `NotificationUtil.showError()` alerts the user.

<br>
<br>


## 📸 Demo

- IntelliJ Plugin Marketplace
![my Plugin on marketplace](https://github.com/user-attachments/assets/7b8c3e2b-1a35-4328-8a8f-06e78af01039)

<br>

- A small code with errors.

![error code -2 but small](https://github.com/user-attachments/assets/bff4b676-59a1-465c-b225-82bdde91f739)

<br>

- I clicked my AI plugin on the tools menu to use it.

![tool window step-1](https://github.com/user-attachments/assets/1e5fd417-18ed-4cad-bef1-c92650468c09)

<br>

- Analysis begins.

![code analysis begins](https://github.com/user-attachments/assets/ad78e5a2-e589-4986-a0f2-f533473f76f8)

<br>

- Errors were displayed for that piece of code.

![errors display for code-2](https://github.com/user-attachments/assets/23b25195-dc77-4765-8870-14e967991a11)

<br>

- Code full or errors.

![error code -1](https://github.com/user-attachments/assets/e600febe-4d3f-4674-aacd-8101330315bc)

<br>

- Different types of errors were categorized.

![shows errors for code-1](https://github.com/user-attachments/assets/622c89cc-11a4-4f50-8f6f-af951b4ad854)

<br>

- On expanding, you can see all the errors that are listed.

![expande errors for code -1](https://github.com/user-attachments/assets/fb97ca4b-f890-478e-9047-0bf61e77d677)

<br>

- An error-free code and no issues/errors at all.

![error free code -3+ no issues found](https://github.com/user-attachments/assets/dbeabe65-5fba-4f9c-8938-da036e3be0f5)

<br>

- ![no issues found](https://github.com/user-attachments/assets/d9c2091d-b208-49d9-be4f-659b68ba237a)

<br>

<hr>

### Working Video:



https://github.com/user-attachments/assets/6d36f256-58a0-48bd-a006-3684cdc3d7d1










<br>

<hr>

## > Why It’s Not on JetBrains Marketplace
**Since the plugin relies on an AI model and API calls, maintaining a public version would require constant API credits.**
<br>

**To avoid unmanageable costs, I’ve kept it private for now.**

<br>



## 🔹 Closing Statement for GitHub README
This repository showcases the architecture, design, and implementation details of an AI-powered IntelliJ plugin for Java code analysis. <br>
While the core structure and logic are provided, some internal mechanisms have been omitted for brevity.
<br>
<br>


This plugin integrates AI-powered static analysis, requiring an external API for operation. 
<br>
Since API usage incurs costs, I’ve chosen to keep the full implementation private for now. <br>
However, I have included:

- ✅ A detailed breakdown of the architecture and design.
- ✅ Screenshots & a demo showcasing the plugin in action.
- ✅ Explanations of key components and how they work together.

<br>

I am currently working on new features to enhance the plugin’s capabilities. Once those are finalized, I may consider opening up more of the implementation.
<br>

For now, this serves as a demonstration of my ability to build a full-fledged IntelliJ plugin that integrates LLMs for advanced static analysis. If you're interested in learning more, feel free to reach out!

<br>

## 📢 Contact
For any questions, feedback, or contributions, feel free to reach out: <br>
**Email:** *anukulmaurya18@gmail.com* <br>
**This README will be updated as the project progresses. Stay tuned!**


