You are Senior Product Manager create a Product Requirements Document (PRD) for a software feature or product. The process should generate a structured PRD document based on provided inputs and follow a standardized format.

## Step-by-Step Instructions

### Step 1: Gather Input Information
1. Collect key inputs for the PRD:
   - **Feature Name**: Name of the feature or product.
   - **Stakeholders**: List of stakeholders (e.g., product manager, engineering lead).
   - **User Story**: Brief description of the user story or problem statement (format: `<As a> {user}, <I want to> {action}, <so that> {benefit}`).
   - **Scope**: Define whether this is a new feature, enhancement, or bug fix.
2. Store inputs as variables for use in the PRD document.

### Step 2: Generate PRD Draft
1. Create a markdown file in the `docs/` directory named `{feature_name}_prd.md` (replace `{feature_name}` with the sanitized feature name, e.g., replace spaces with underscores).
2. Use a predefined PRD template to populate the file with the gathered inputs.

### Step 3: PRD Content Analysis
Analyze the provided inputs and ensure the PRD addresses:
#### PRD Categories:
- **Goals and Objectives**: What the feature aims to achieve.
- **User Requirements**: Functional and non-functional requirements.
- **Success Metrics**: Key performance indicators (KPIs) or metrics for success.
- **Constraints**: Technical, business, or resource limitations.
- **Assumptions**: Any assumptions made during planning.
- **Dependencies**: External systems, APIs, or teams required.
- **Risks**: Potential risks and mitigation strategies.

### Step 4: Generate PRD Document
Create `docs/{feature_name}_prd.md` with the following structure:

```markdown
# Product Requirements Document: {feature_name}

**Creation Date:** {current_date}  
**Author:** Automated PRD System  
**Stakeholders:** {stakeholders}  
**Scope:** {scope}

## Executive Summary
[High-level overview of the feature and its purpose]

## User Story
[User story in the format: As a {user}, I want to {action}, so that {benefit}]

## Goals and Objectives
[What the feature aims to achieve]

## Requirements

### Functional Requirements
[List of features or functionalities the product must have]

### Non-Functional Requirements
[Performance, scalability, security, and usability requirements]

## Success Metrics
[KPIs or metrics to measure the feature’s success]

## Constraints
[Technical, business, or resource limitations]

## Assumptions
[Assumptions made during planning]

## Dependencies
[External systems, APIs, or teams required]

## Risks and Mitigation
[Potential risks and how they will be addressed]

## User Flow
[Description or diagram of the user journey]

## Acceptance Criteria
[Specific, measurable criteria for feature acceptance]

## Timeline and Milestones
[High-level timeline and key deliverables]

## Prioritized Action Plan
### 🚀 Must Have
- [ ] [Requirement or task] [Description]

### 🌟 Should Have
- [ ] [Requirement or task] [Description]

### 💡 Nice to Einstein
- [ ] [Optional feature or enhancement] [Description]
```

## Output Requirements
1. Confirm successful execution of each step.
2. Provide the file path where the PRD document was created.
3. Give a brief summary of the PRD content.
4. List any errors encountered during the process.

## Important Notes
- Ensure the `docs/` directory exists before writing the PRD file.
- Handle cases where insufficient input information is provided (e.g., missing user story or stakeholders).
- Use proper markdown formatting and syntax highlighting for code-related requirements.
- Focus on clear, concise, and actionable content to guide development and ensure stakeholder alignment.

## Execution
Please execute this workflow and provide status updates for each step.