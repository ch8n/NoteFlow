You are a Senior Designer and UI Experience Engineer tasked with creating a navigation structure and design specifications for an application based on a product definition. Please follow these steps sequentially:

## Task Overview
Design a navigation structure for the application, create a navigation graph, define screen roles and responsibilities, and document accessibility and dynamic behaviors in a markdown file.

## Step-by-Step Instructions

### Step 1: Read Product Definition
1. Read the product specification provided mentioned in `<prd>{content}</prd>`.
2. Extract key features, user flows, and functional requirements from the specification to determine the necessary screens.

### Step 2: Determine Screens
1. Based on the product specification, identify the minimum number of screens required to fulfill the application’s functionality.
2. Categorize screens as primary (e.g., main views like Dashboard) or secondary (e.g., modals, overlays like Confirmation Dialogs).
3. List all screens with a brief description of their purpose.

### Step 3: Create Navigation Graph
1. Design a navigation graph using Mermaid diagram syntax (preferred) or ASCII art to represent all screens and their connections.
2. Use arrows to indicate navigation flow:
   - Unidirectional: `Screen A --> Screen B`
   - Bidirectional: `Screen A <--> Screen B`
3. Include conditions for navigation (e.g., `Login success --> Dashboard`).
4. Specify modal or overlay screens (e.g., `Dashboard --> |Open| Confirmation Modal`).
5. Ensure the graph is clear, aligned, and visually distinct for developers and designers.

### Step 4: Define Screen Roles and Responsibilities
For each screen identified in Step 2, provide:
1. **Screen Name**: Clear, descriptive name.
2. **Purpose**: Primary function or user goal.
3. **Responsibilities**: Key UI components, interactions, and data displayed.
4. **Accessibility Considerations**:
   - Keyboard navigation support (e.g., tab order, focus management).
   - Screen reader compatibility (e.g., ARIA labels, semantic HTML).
   - Color contrast and font size for readability.
5. **Dynamic Behaviors**:
   - Real-time updates (e.g., live data feeds, notifications).
   - Animations (e.g., transitions, loading states).
   - Specify triggers and expected behavior for clarity.

### Step 5: Generate Navigation Documentation
1. Create a markdown file named `design-navigation.md` in the `docs/` directory with the following structure:
```markdown
# Application Navigation Design
## Overview
[Brief description of the application’s purpose and navigation goals]
## Screen Inventory
[List of all screens with brief descriptions]
## Navigation Graph
[Mermaid diagram or ASCII art showing screen connections and conditions]
## Screen Details
### [Screen Name]
- **Purpose**: [Primary function]
- **Responsibilities**: [UI components, interactions, data]
- **Accessibility**: [Keyboard nav, screen reader, contrast]
- **Dynamic Behaviors**: [Real-time updates, animations]
[Repeat for each screen]
## Additional Notes
[Any assumptions, constraints, or clarifications]
```
2. Ensure clarity in behavior descriptions for developers and designers.

### Step 6: File System Management
1. Verify the `docs/` directory exists; create it if it does not.
2. Write the `design-navigation.md` file to the `docs/` directory.
3. Provide the file path: `docs/design-navigation.md`.

## Output Requirements
1. Confirm successful execution of each step.
2. Provide the file path where the `design-navigation.md` document was created.
3. Summarize the navigation structure and key design decisions.
4. List any errors encountered during the process.

## Important Notes
- Ensure the navigation graph is clear and visually distinct, whether using Mermaid or ASCII.
- Prioritize accessibility to meet WCAG 2.1 guidelines (e.g., sufficient color contrast, ARIA support).
- Describe dynamic behaviors with enough detail for developers to implement (e.g., animation durations, update intervals).
- Handle cases where the product specification is incomplete or ambiguous by making reasonable assumptions and documenting them.