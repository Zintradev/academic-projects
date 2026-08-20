# Roman Numeral Converter

![JavaScript](https://img.shields.io/badge/javascript-%23F7DF1E.svg?style=flat-square&logo=javascript&logoColor=black)
![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=flat-square&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=flat-square&logo=css3&logoColor=white)
![ESLint](https://img.shields.io/badge/eslint-3A3307?style=flat-square&logo=eslint&logoColor=white)
![Mocha](https://img.shields.io/badge/mocha-%238D6748.svg?style=flat-square&logo=mocha&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/github%20actions-%232088FF.svg?style=flat-square&logo=github-actions&logoColor=white)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)

A highly optimized, modular, and responsive web application that performs bidirectional conversions between standard integers (1 to 3999) and Roman numerals. This project is built using modern ES6+ features and has been refined for integration into a professional portfolio.

---

## Technical Highlights

This project demonstrates several advanced software engineering practices and patterns:

*   **Modular Architecture (ES Modules)**: The codebase utilizes native ES6 modules (`import`/`export`) to isolate scope. This completely avoids global namespace pollution and guarantees that functions do not leak into the global `window` object.
*   **Separation of Concerns & Single Responsibility Principle (SRP)**: 
    *   [`converter.js`](src/js/converter.js) is a pure model containing only the conversion logic and algorithmic mathematical mappings. It has zero dependencies on the DOM and can be imported in any JavaScript environment (e.g. Node.js or browser).
    *   [`ui.js`](src/js/ui.js) acts as the controller, handling DOM events, user inputs, outputs, and event tracking (Google Analytics integration).
*   **Bidirectional Canonical Round-Trip Validation**: Instead of building extremely complex, hard-to-maintain regular expressions to validate Roman numeral grammar (such as forbidding more than 3 consecutive identical letters, enforcing subtractive syntax rules, etc.), the algorithm converts the parsed Roman numeral back into an integer, and then converts it back to a Roman numeral. If the re-converted string does not match the original input (e.g., `VIIII` $\rightarrow$ $9$ $\rightarrow$ `IX`), it detects a non-canonical format and throws a validation error.
*   **Automated Headless Browser Testing**: Tests are executed via a headless Chrome environment using `mocha-headless-chrome`. This ensures that unit tests run in a real browser context mimicking production, validating frontend behavior under genuine JavaScript engines rather than mock CLI environments.
*   **Cross-Platform Portability**: Dev scripts utilize the `shx` wrapper. This ensures clean and build operations are shell-independent, allowing compilation and local deployment commands to run identically on Windows (Powershell/CMD) and Unix systems (Linux/macOS).

---

## UI Controls & Usage

| Control | Type | Description |
| :--- | :--- | :--- |
| **Conversion Mode** | Dropdown Select | Allows switching between **Integer to Roman** and **Roman to Integer** modes. |
| **Input Value** | Text Input | Accepts the value to convert (either a standard integer $1 \le x \le 3999$ or a valid Roman numeral string). |
| **Convert** | Button | Triggers the conversion logic, parsing the inputs and updating the DOM elements. |
| **Result display** | Div Block | Displays the converted result (e.g. `Roman Numeral: XIV` or `Integer: 14`). |
| **Error display** | Div Block | Displays red validation feedback in case of incorrect input format or values out of bounds. |

---

## Compilation, Build, & Execution

### Prerequisites
- Node.js (v18.x or v20.x recommended)
- npm (Node Package Manager)

### 1. Installation
Clone the repository and install the development dependencies (ESLint, Mocha, headless browser runner, and cross-platform build utility):
```bash
npm install
```

### 2. Linting
Verify code quality and clean syntax using ESLint:
```bash
npm run lint
```

### 3. Running Unit Tests
Execute the Mocha-Chai automated test suite inside the headless browser:
```bash
npm test
```
*Note: You can also open [`tests/test.html`](tests/test.html) directly in any web browser to view the interactive test suite results UI.*

### 4. Compiling/Building
To generate the production-ready build in the `dist/` folder:
```bash
npm run build
```
This creates a clean output folder containing the flattened, ready-to-deploy static assets.

### 5. Running the Application Locally
- **Option A (Simple)**: Double-click or open [`src/index.html`](src/index.html) in any web browser.
- **Option B (Server)**: Run a local static server to mock deployment:
  ```bash
  npx serve src
  ```
