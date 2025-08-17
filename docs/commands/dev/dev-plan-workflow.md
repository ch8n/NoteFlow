Create a comprehensive development plan based on the provided requirement document with integrated testing and checklist-based execution.

**Input Requirement Documents:**
- Input PRD content mentioned in `<prd>{content}</prd>`
- Input Technical Design Document(TRD) mentioned in `<trd>{content}</trd>`
- Input Technical Overview mentioned in `<tech>{content}</tech>`
- System Design Document mentioned in `<sys>{content}</sys>`
- App Navigation Document mentioned in `<nav>{content}</nav>`
- Specs of target Screen to Development in `<screen>{content}</screen>`
- Specs of ui components of Screen to Development in `<comp>{content}</comp>`

**Instructions:**
Analyze the requirement documents and generate a development plan that includes:

## 0. Scope Analysis
- Break down large requirements into implementable chunks
- Identify MVP vs. future phases
- Estimate complexity levels (1-5 scale)

## 1. Technical Overview
- Concise technical summary (2-3 sentences max)
- Key technical components involved
- Architecture/system areas affected

## 2. File Analysis & Modifications
List all files that need to be modified, created, or removed:
- **New Files:** List files to be created with brief purpose
- **Modified Files:** List existing files requiring changes with exact functions/classes/modules affected
- **Deprecated Files:** List files to be removed or archived (if any)

## 3. Function/Method Analysis
For each file requiring modification, identify:
- **New Functions:** Functions to be created with their purpose
- **Modified Functions:** Existing functions requiring changes with modification type (logic update, parameter changes, etc.)
- **Removed Functions:** Functions to be deleted (if any)

## 4. Database/Schema Changes
If applicable:
- New tables/collections
- Modified schemas
- Data migration requirements
- Index changes

## 5. API/Interface Changes
If applicable:
- New endpoints/methods
- Modified endpoints/methods
- Request/response format changes
- Authentication/authorization updates

## 6. Dependencies & Libraries
- New dependencies to add
- Dependency version updates
- Library modifications

## 7. Step-by-Step Development Plan with Checklists
**EXECUTION RULE: Complete ALL checklist items and their unit tests before proceeding to the next step.**

For each development step, provide:
### Step X: [Step Name] - [PRIORITY LEVEL]
**Objective:** Brief description of what this step accomplishes
**Dependencies:** Previous steps that must be completed

**Checklist Items:**
- [ ] **Item 1:** Specific implementation task
    - **Unit Test:** `test_[specific_functionality]()`
    - **Test Cases:**
        - ✅ Test case 1: Description
        - ✅ Test case 2: Description
        - ✅ Test case 3: Edge case description
    - **Validation:** How to verify this item is complete

- [ ] **Item 2:** Next specific implementation task
    - **Unit Test:** `test_[another_functionality]()`
    - **Test Cases:**
        - ✅ Test case 1: Description
        - ✅ Test case 2: Description
    - **Validation:** How to verify this item is complete

**Step Completion Criteria:**
- [ ] All checklist items implemented
- [ ] All unit tests passing (100% pass rate required)
- [ ] Code review completed
- [ ] Integration tests passing
- [ ] Documentation updated

**Rollback Plan:** If this step fails, how to revert changes

---

## 8. Testing Strategy & Test Development Plan
**Unit Test Creation Schedule:**
- Tests must be written BEFORE implementing each checklist item
- Test-Driven Development (TDD) approach mandatory
- Each test case must include:
    - Setup/Arrange phase
    - Action/Act phase
    - Assertion/Assert phase
    - Cleanup/Teardown phase

**Test Categories:**
- **Unit Tests:** Individual function/method testing
- **Integration Tests:** Component interaction testing
- **System Tests:** End-to-end workflow testing
- **Edge Case Tests:** Boundary condition testing
- **Error Handling Tests:** Exception and error scenario testing

## 9. Configuration & Environment
- Environment variables changes
- Configuration file updates
- Feature flags/toggles
- Infrastructure modifications

## 10. Security & Compliance
- Security implications
- Permission/role changes
- Data privacy considerations
- Audit trail requirements

## 11. Quality Gates & Checkpoints
**Mandatory Quality Checks Between Steps:**
- [ ] Code coverage minimum 85%
- [ ] All linting rules passing
- [ ] Static analysis clean
- [ ] Security scan clean
- [ ] Performance benchmarks met

**Checkpoint Actions:**
- Run full test suite
- Validate against acceptance criteria
- Check code quality metrics
- Verify no regression issues
- Document completion status

**Output Format:**
1. Create `docs/{screen_name}_dev_plan.md` with this structure:

Provide a structured, technical development plan focusing on implementation with integrated testing approach.

**Execution Rules:**
1. **Sequential Execution:** Complete each checklist item one at a time
2. **Test-First Approach:** Write unit tests before implementing functionality
3. **Gate-Controlled Progress:** Move to next item only when ALL tests pass
4. **Continuous Validation:** Run regression tests after each checklist item completion
5. **Rollback Ready:** Each step must include rollback procedures
6. **DONT INCLUDE CODE SNIPPETS** in plan.

**Format Requirements:**
- Use consistent markdown formatting with checkboxes
- Provide specific file paths: `src/services/userService.kt`
- Include exact test function names: `test_user_creation_with_valid_data()`
- Use clear action verbs: CREATE, MODIFY, DELETE, REFACTOR, TEST
- Mark test status with ✅ (passing) or ❌ (failing)

**Detail Level:**
- Specific function names and signatures
- Exact file paths and line numbers where applicable
- Detailed test case scenarios
- Step-by-step implementation guidance

**Prioritization:** Mark items as [CRITICAL], [HIGH], [MEDIUM], [LOW] priority

**Risk Assessment:** Flag high-risk changes that could break existing functionality

**Testing Requirements:**
- Minimum 3 test cases per checklist item
- Edge case coverage mandatory
- Error handling test coverage
- Performance impact testing where applicable

## Important Notes
- Ensure the `docs/` directory exists before writing files
- Each checklist item must have corresponding unit tests
- Progress tracking through test status indicators
- No parallel development - strictly sequential execution
- All tests must pass before proceeding to next checklist item
- Don't Include code snippets in plan.