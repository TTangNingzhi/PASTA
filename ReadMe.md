# PASTA: A JetBrains Plugin for Prompting-Based Code Modification

_Ningzhi Tang @ SaNDwich Lab, University of Notre Dame_

PASTA is a research prototype plugin for JetBrains IDEs (e.g., PyCharm, WebStorm) that enables developers to interact with large language models (LLMs) to modify code through two prompting techniques:
- 📝 **Summary-Mediated Prompting**
- 💬 **Direct Instruction Prompting**

This plugin was developed to support an empirical study on how developers perceive and use prompting to guide LLMs in code modification tasks.

<p align="center">
    <img src="static/screenshot.png" max-width="100%" alt="System Overview">
</p>

## ✨ Features

### Summary-Mediated Prompting
- Generate editable summaries for selected code snippets
- Edit the summaries to specify intended changes
- View before/after summary differences
- Commit modified summaries to trigger LLM-based code transformation

### Direct Instruction Prompting
- Write free-form prompts directly to modify selected code
- Ideal for developers who prefer direct and concise commands

### Diff-Based Results
- All LLM-generated modifications are shown in a side-by-side diff view
- Allows comparison, validation, and selective acceptance of changes

## 🛠️ Implementation

PASTA is built using:
- JetBrains [IntelliJ Platform Plugin SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- [Java Diff Utils](https://java-diff-utils.github.io/java-diff-utils/) for natural language summary comparison
- Built-in JetBrains diff package for code changes
- [OpenAI GPT-4o](https://openai.com/index/hello-gpt-4o/) for LLM-based summarization and code modification

## 📐 Design Decisions

- **Selection-Based Prompting**: Easy and natural interaction within the IDE; aligns with existing tools like Cursor and Copilot Chat.
- **Context-Aware Requests**: Includes current file as context to enhance model quality; simple but effective baseline adopted from prior work.

## ✉️ Contact

For questions or collaboration inquiries, please contact Ningzhi Tang at ntang@nd.edu or ningzhitang2001@gmail.com.