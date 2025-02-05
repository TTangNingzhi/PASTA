# Declarative vs Procedural Prompting for LLM Code Modification

_Ningzhi Tang @ SaNDwich Lab_

Historically, there have been two paradigms in the design of programming languages. **Declarative programming** (e.g.,
SQL) abstracts implementation details, letting developers specify what to achieve, offering simplicity and readability
at the cost of limited control. **Procedural programming** (e.g., C) requires step-by-step instructions for how to solve
a problem, enabling granular control but demanding deeper technical expertise. While declarative paradigms excel at
rapid development, procedural approaches provide precision for complex logic.

Drawing inspiration from the two paradigms of “programming” used to instruct computers, we design a study to explore two
paradigms of “prompting” to modify LLM-generated code.

<p align="center">
    <img src="static/screenshot.png" max-width="100%" alt="System Overview">
</p>

Specifically, current methods often rely on **“declarative prompts”**, e.g., “Fix the layout”, alongside pasted code,
asking the LLM to regenerate it. However, these prompts can be vague and may fail to specify concrete steps, resulting
in modifications by the LLM that do not align well with user intentions.

Inspired by recent work on steering generative AI by mapping AI’s output back into an editable natural language
utterance to further instruct AI (e.g., [grounded abstraction matching](https://dl.acm.org/doi/10.1145/3544548.3580817)), we designed **“procedural prompts”** to enable
users to specify how they want the code to behave by modifying code summaries.
