You are a senior software engineer tasked with automating code review processes. Please follow these steps sequentially:

## Task Overview
Perform an automated code review workflow that includes generating git diffs and creating technical review reports.

## Step-by-Step Instructions

### Step 1: Extract Branch Information
1. Get the current Git branch name using: `git branch --show-current`
2. Extract the parent branch name from the provided format: `<rc>{parentBranch}</rc>`
    - Example: If given `<rc>release</rc>`, the parent branch is "release"
    - Store both branch names as variables for later use

### Step 2: Generate Diff File
1. Execute: `git diff {parentBranch} >> docs/{current_branch}_changes.md` (replace {parentBranch} with the extracted parent branch name, and current_branch with the current branch name)

### Step 3: Technical Code Review
Analyze the generated `{current_branch}_changes.md` file and create a comprehensive technical review covering:

#### Review Categories:
- **Code Quality**: Logic, readability, maintainability
- **Security**: Potential vulnerabilities, data handling
- **Performance**: Efficiency concerns, resource usage
- **Best Practices**: Coding standards, patterns, conventions
- **Testing**: Test coverage, test quality
- **Documentation**: Code comments, README updates
- **Dependencies**: New packages, version changes
- **Completeness**: Look for missing requirement or half implemented code.
- **Correctness**: Check if code is syntactically correct and each function is corrected implemented.

### Step 4: Generate Review Report
Create `docs/{current_branch}_review.md` with this structure:

```markdown
# Code Review Report: {current_branch}

**Review Date:** {current_date}
**Base Branch:** {parentBranch}
**Review Branch:** {current_branch}
**Reviewer:** Automated Code Review System

## Executive Summary
[Brief overview of changes and overall assessment]

## Files Changed
[List of modified files with brief descriptions]

## Detailed Analysis

### ✅ Strengths
[Positive aspects of the changes]

### ⚠️ Areas for Improvement
[Issues that should be addressed]

### 🔴 Critical Issues
[Serious problems that must be fixed]

### 📝 Recommendations
[Specific actionable suggestions]

## 👮🏼 Security Assessment
[Security-related findings]

## 🚅 Performance Impact
[Performance considerations]

## 🧪 Testing Recommendations
[Testing suggestions and requirements]

### 🐣 Completeness
[Missing requirement and half implemented code]

### 👍🏻 Correctness
[Wrong syntax and incorrect implementation]

## ✨ Overall Rating
[Score out of 10 with justification]

## 📈 Code Analysis Requirements
For each changed file, analyze:
1. **Function-level changes**: New/modified/deleted functions
2. **Dependency changes**: New imports, removed dependencies
3. **Configuration changes**: Environment variables, configs
4. **Database schema changes**: Migrations, model changes
5. **API changes**: Endpoints, request/response formats

## Prioritized Action Plan
### 🚨 Must Fix (Blocking)
- [ ] [Specific file:line] [Issue] [Suggested fix]

### ⚠️ Should Fix (Pre-merge)
- [ ] [Specific file:line] [Issue] [Suggested fix]

### 💡 Consider (Post-merge)
- [ ] [Specific file:line] [Improvement] [Suggested enhancement]
```

## Output Requirements
1. Confirm successful execution of each step
2. Provide the file paths where documents were created
3. Give a brief summary of the review findings
4. List any errors encountered during the process

## Important Notes
- Ensure the `docs/` directory exists before writing files
- Handle cases where no differences exist between branches
- Include proper markdown formatting and syntax highlighting
- Focus on constructive feedback that helps improve code quality

Please execute this workflow and provide status updates for each step.