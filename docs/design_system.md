# Global Design System Specification

## Overview
This design system establishes consistent UI components and interactions across the NoteFlow application. It promotes reusability, accessibility, and maintainability by centralizing atomic and molecular components used throughout the video transcription-to-study-notes workflow.

## Atomic Components
### URL Input Field
- **Description**: Text input field for entering a Video Transcription URL
- **Behavior**: 
  - Default state: Empty with placeholder text
  - Focus state: Highlighted border, cursor appears
  - Error state: Red border, error message displayed
  - Validation state: Real-time feedback as user types
- **Properties**: Size (full width), states (default, focus, error, validation)
- **Animations/Dynamic Behaviors**: Smooth transition on focus/blur, real-time character highlighting for invalid inputs, progress indicator during validation
- **Accessibility Features**: Keyboard navigation, screen reader compatibility with ARIA labels
- **ASCII Representation**:
```
+----------------------------------------+
|  Enter Video Transcription URL:       |
| https://example.com/video/xyz123     |
|                                     ▶ |
| [Validate]                          |
|                                     ▶ |
| Real-time validation feedback       |
| (URL format, video ID extraction)   |
+----------------------------------------+
```
- **Reusability**: Can be reused across screens where URL input is needed

### Validate Button
- **Description**: Primary action button to initiate processing of the shared URL
- **Behavior**: 
  - Default state: Enabled, text shows "Validate"
  - Loading state: Disabled, shows spinner animation
  - Success state: Shows success confirmation message
  - Error state: Shows error message with retry option
- **Properties**: Size (medium), states (default, loading, success, error)
- **Animations/Dynamic Behaviors**: Smooth spinner animation during processing, button color changes from primary to disabled during loading, success state shows a green checkmark animation
- **Accessibility Features**: Keyboard navigation, screen reader compatibility with ARIA labels
- **ASCII Representation**:
```
+----------------------------------------+
|  Video Transcription to Study Notes   |
|  Share a URL to get your notes        |
+----------------------------------------+
| Enter Video Transcription URL:       |
| https://example.com/video/xyz123     |
|                                     ▶ |
| [Validate]                          |
|                                     ▶ |
| Real-time validation feedback       |
| (URL format, video ID extraction)   |
+----------------------------------------+
| Status: Ready to share               |
+----------------------------------------+
```
- **Reusability**: Can be reused across screens where URL processing is needed

### Status Display
- **Description**: Component that displays the current state of URL sharing
- **Behavior**: 
  - Default: "Status: Ready to share"
  - Processing: Shows "Processing..." with loading spinner
  - Success: Shows success message and enables navigation
  - Error: Displays error details and retry option
- **Properties**: Size (full width), states (default, processing, success, error)
- **Animations/Dynamic Behaviors**: Smooth fade-in/fade-out transitions between states, loading spinner appears during processing, success animation shows a brief celebration effect
- **Accessibility Features**: Keyboard navigation, screen reader compatibility with ARIA labels
- **ASCII Representation**:
```
+----------------------------------------+
|  Video Transcription to Study Notes   |
|  Share a URL to get your notes        |
+----------------------------------------+
| Enter Video Transcription URL:       |
| https://example.com/video/xyz123     |
|                                     ▶ |
| [Validate]                          |
|                                     ▶ |
| Real-time validation feedback       |
| (URL format, video ID extraction)   |
+----------------------------------------+
| Status: Ready to share               |
+----------------------------------------+
```
- **Reusability**: Can be reused across screens requiring state updates

### Validation Feedback Indicator
- **Description**: Visual feedback element showing URL validation results
- **Behavior**: 
  - Default: Hidden
  - Valid state: Shows green checkmark with success message
  - Invalid state: Shows red cross with specific error details
  - Real-time state: Updates as user types
- **Properties**: Size (compact), states (hidden, valid, invalid)
- **Animations/Dynamic Behaviors**: Smooth transition between states, real-time character highlighting during typing, error messages appear with subtle pulse animation
- **Accessibility Features**: Keyboard navigation, screen reader compatibility with ARIA labels
- **ASCII Representation**:
```
+----------------------------------------+
|  Video Transcription to Study Notes   |
|  Share a URL to get your notes        |
+----------------------------------------+
| Enter Video Transcription URL:       |
| https://example.com/video/xyz123     |
|                                     ▶ |
| [Validate]                          |
|                                     ▶ |
| Real-time validation feedback       |
| (URL format, video ID extraction)   |
+----------------------------------------+
| Status: Ready to share               |
+----------------------------------------+
```
- **Reusability**: Critical for guiding users through the sharing process

### Timestamp Marker
- **Description**: Displays timecodes in MM:SS format to indicate when specific segments of speech occur
- **Behavior**: Default state shows timecode; hover state may highlight the segment visually (optional)
- **Properties**: Size (compact), format (MM:SS)
- **Animations/Dynamic Behaviors**: Smooth fade-in animation on text load, subtle color change on active segment
- **Accessibility Features**: Keyboard navigation, screen reader compatibility with ARIA labels
- **ASCII Representation**:
```
[00:00] "Hello everyone, welcome to   |
|  our video on machine learning..."
```
- **Reusability**: Can be reused across different transcription screens

### Text Content Block
- **Description**: Displays actual spoken content from the audio transcription
- **Behavior**: Default state shows full text; real-time updates during loading (partial content)
- **Properties**: Size (full width), formatting preserves original pauses and emphasis
- **Animations/Dynamic Behaviors**: Smooth transition when new segment loads, maintains original formatting including pauses and emphasis
- **Accessibility Features**: Clickable to select and copy text segments, keyboard navigation supported
- **ASCII Representation**:
```
[00:00] "Hello everyone, welcome to   |
|  our video on machine learning..."
```
- **Reusability**: Can be reused across different transcription screens with content variations

### Copy Transcript Button
- **Description**: Enables users to copy the complete transcript to clipboard
- **Behavior**: Default state shows button label; on click, triggers copy action with success toast
- **Properties**: Size (medium), states (default, loading)
- **Animations/Dynamic Behaviors**: Subtle pulse animation on successful copy, slight elevation change on hover
- **Accessibility Features**: Keyboard navigation, screen reader compatibility with ARIA labels
- **ASCII Representation**:
```
| Copy Transcript                      |
| Save Transcript                     |
+----------------------------------------+
```
- **Reusability**: Can be reused in any screen requiring transcript copying

### Save Transcript Button
- **Description**: Allows users to save the transcript for later reference in local storage
- **Behavior**: Default state shows button label; on click, triggers save operation with confirmation dialog
- **Properties**: Size (medium), states (default, loading)
- **Animations/Dynamic Behaviors**: Loading spinner appears during save process, fade-in animation after successful save
- **Accessibility Features**: Keyboard navigation, screen reader compatibility with ARIA labels
- **ASCII Representation**:
```
| Copy Transcript                      |
| Save Transcript                     |
+----------------------------------------+
```
- **Reusability**: Can be reused in any screen requiring transcript saving

### Loading Spinner
- **Description**: Indicates active processing of transcription extraction
- **Behavior**: Appears when API call is in progress; disappears upon completion
- **Properties**: Size (medium), position (top center)
- **Animations/Dynamic Behaviors**: Smooth rotating animation, positioned prominently at the top center
- **Accessibility Features**: Visible to all users regardless of screen reader
- **ASCII Representation**:
```
+----------------------------------------+
|  Video Transcription Transcript       |
|  URL: https://example.com/video/xyz123 |
+----------------------------------------+
| [Loading...]                        |
+----------------------------------------+
```
- **Reusability**: Can be used as a loading state indicator across multiple screens

### Progress Bar
- **Description**: Shows completion percentage during transcription extraction process
- **Behavior**: Starts at 0% and animates to 100% as content loads; updates in real-time
- **Properties**: Size (full width), color gradient from start to end
- **Animations/Dynamic Behaviors**: Smooth linear animation with subtle color gradient from start to end
- **Accessibility Features**: Visible to all users, provides clear progress indication
- **ASCII Representation**:
```
+----------------------------------------+
|  Video Transcription Transcript       |
|  URL: https://example.com/video/xyz123 |
+----------------------------------------+
| [0%]─────────────────────────────────[100%] |
+----------------------------------------+
```
- **Reusability**: Can be used as a progress indicator across multiple screens

### Text Label
- **Description**: Displays descriptive text such as note title, key points, and summary content
- **Behavior**: Default state shows regular text; hover state may have subtle background color change
- **Properties**: Size (medium), formatting consistent with application style
- **Animations/Dynamic Behaviors**: Smooth transition on focus with slight border highlighting
- **Accessibility Features**: Keyboard navigation, screen reader compatibility with ARIA labels
- **ASCII Representation**:
```
| Study Notes - https://example.com/   |
| video/xyz123                         |
```
- **Reusability**: Can be reused across different note views

### Edit Button
- **Description**: Allows users to modify any note element (key point, summary)
- **Behavior**: Default state shows as text; when clicked, enables edit mode
- **Properties**: Size (medium), states (default, active)
- **Animations/Dynamic Behaviors**: Slight scale animation on click with visual feedback
- **Accessibility Features**: Keyboard navigation, screen reader compatibility with ARIA labels
- **ASCII Representation**:
```
| • Key Point 1: Neural networks are    |
|   fundamental building blocks in      |
|   machine learning systems.           |
|                                     ▶ |
| [Edit]                               |
```
- **Reusability**: Can be reused across different note types that require editing

### Save Button
- **Description**: Saves changes back to RoomDB with timestamp metadata
- **Behavior**: Default state shows as text; when clicked, validates content before saving
- **Properties**: Size (medium), states (default, loading)
- **Animations/Dynamic Behaviors**: Fade-in animation on successful save, loading spinner during processing
- **Accessibility Features**: Keyboard navigation, screen reader compatibility with ARIA labels
- **ASCII Representation**:
```
| [Save]                               |
```
- **Reusability**: Can be reused across any note that requires saving changes

### Delete Button
- **Description**: Removes the study note with confirmation
- **Behavior**: Default state shows as text; when clicked, displays confirmation dialog
- **Properties**: Size (medium), states (default, hover)
- **Animations/Dynamic Behaviors**: Subtle pulse animation on hover with clear visual feedback
- **Accessibility Features**: Keyboard navigation, screen reader compatibility with ARIA labels
- **ASCII Representation**:
```
| [Delete]                            |
```
- **Reusability**: Can be reused across all note views requiring deletion

### Navigation Buttons ([Back], [Search], [Share])
- **Description**: Provides navigation between screens
- **Behavior**: Default state shows as text; when clicked, navigates to respective screen
- **Properties**: Size (medium), states (default)
- **Animations/Dynamic Behaviors**: Smooth fade transition on navigation
- **Accessibility Features**: Keyboard navigation, screen reader compatibility with ARIA labels
- **ASCII Representation**:
```
| [Back] - Return to previous screen   |
| [Search] - Find existing notes      |
| [Share] - Share this note           |
```
- **Reusability**: Can be reused across all screens requiring navigation

## Molecular Components
### URL Validation Form
- **Description**: Composed of input field, validation feedback, and validate button that together provide a complete URL sharing experience
- **Composition from atoms**: URL Input Field + Validation Feedback Indicator + Validate Button + Status Display
- **Behavior**: 
  - Validates URL format in real-time as user types
  - Extracts video ID from path component after parsing base domain
  - Triggers API call when Validate button is clicked
  - Provides immediate visual feedback for validation results
- **Interactions**: Input field and validate button interact to trigger validation process; validation feedback updates based on input state; form state changes from "ready" to "processing" to "success/failure"
- **Animations/Dynamic Behaviors**: Smooth transitions between states with real-time feedback during typing
- **ASCII Representation**:
```
+----------------------------------------+
|  Video Transcription to Study Notes   |
|  Share a URL to get your notes        |
+----------------------------------------+
| Enter Video Transcription URL:       |
| https://example.com/video/xyz123     |
|                                     ▶ |
| [Validate]                          |
|                                     ▶ |
| Real-time validation feedback       |
| (URL format, video ID extraction)   |
+----------------------------------------+
| Status: Ready to share               |
+----------------------------------------+
```
- **Reusability**: Can be reused across screens where URL input is needed; adaptable for different content types with minimal changes

### Transcript Display Container
- **Description**: Combines timestamp markers and text content blocks into a cohesive presentation of the full transcript
- **Composition from atoms**: Timestamp Marker + Text Content Block
- **Behavior**: Displays all segments in chronological order with accurate timing information
- **Interactions**: Content updates dynamically as transcription loads; timestamps adjust based on screen width
- **Animations/Dynamic Behaviors**: Smooth transition when new segment loads, maintains original formatting including pauses and emphasis
- **ASCII Representation**:
```
+----------------------------------------+
|  Video Transcription Transcript       |
|  URL: https://example.com/video/xyz123 |
+----------------------------------------+
| [00:00] "Hello everyone, welcome to   |
|  our video on machine learning..."    |
|                                     ▶ |
| [00:05] "Today we'll cover three       |
|  key topics: neural networks,        |
|  deep learning architectures, and... |
|                                     ▶ |
| [00:12] "Let's dive into the first     |
|  topic - how neural networks work..."|
|                                     ▶ |
+----------------------------------------+
```
- **Reusability**: Can be used across different video transcription screens with URL and timestamp variations

### Navigation Panel
- **Description**: Combines back, generate notes, and search actions into a unified navigation interface
- **Composition from atoms**: Navigation Buttons ([Back], [Generate Study Notes], [Search])
- **Behavior**: Provides clear pathways to return to previous screen or proceed to next steps in the workflow
- **Interactions**: Back button returns user to Share Screen; Generate Study Notes triggers note creation process; Search opens search functionality
- **Animations/Dynamic Behaviors**: Smooth fade transition on navigation
- **ASCII Representation**:
```
| Copy Transcript                      |
| Save Transcript                     |
+----------------------------------------+
| [Back] - Return to Share Screen      |
| [Generate Study Notes] - Create notes|
| [Search] - Find existing notes      |
+----------------------------------------+
```
- **Reusability**: Can be used in other screens requiring navigation between related features (e.g., note editing, search results)

### Note Card Container
- **Description**: Encloses all note content including key points, summary, and navigation controls
- **Composition from atoms**: Text Label + Key Points + Summary + Navigation Buttons
- **Behavior**: Presents a cohesive view of the study notes with proper spacing and layout
- **Interactions**: All components within container respond to user interactions
- **Animations/Dynamic Behaviors**: Smooth fade transition when note is viewed or edited
- **ASCII Representation**:
```
+----------------------------------------+
|  Study Notes - https://example.com/   |
|  video/xyz123                         |
+----------------------------------------+
| • Key Point 1: Neural networks are    |
|   fundamental building blocks in      |
|   machine learning systems.           |
|                                     ▶ |
| • Key Point 2: Deep learning         |
|   architectures include CNNs, RNNs,  |
|   and transformers.                  |
|                                     ▶ |
| • Summary: This video covers the     |
|   foundational concepts of neural    |
|   networks and deep learning        |
|   frameworks.                        |
+----------------------------------------+
| [Edit] [Save] [Delete]               |
+----------------------------------------+
```
- **Reusability**: Can be reused across different note types with minimal changes to content structure

### Edit Form Molecule
- **Description**: Combines edit controls with real-time validation
- **Composition from atoms**: Edit Button + Save Button + Text Label (editable)
- **Behavior**: Validates content meets minimum requirements before saving (e.g., non-empty fields)
- **Interactions**: Real-time validation highlights errors; form updates immediately when editing
- **Animations/Dynamic Behaviors**: Smooth transitions between edit states with real-time feedback
- **ASCII Representation**:
```
+----------------------------------------+
|  Study Notes - https://example.com/   |
|  video/xyz123                         |
+----------------------------------------+
| • Key Point 1: [editable text]        |
|                                     ▶ |
| • Summary: [editable text]           |
+----------------------------------------+
| [Edit] [Save] [Delete]               |
+----------------------------------------+
```
- **Reusability**: Can be reused for any note type that requires user editing

### Action Toolbar
- **Description**: Contains all primary actions (save, delete) and navigation controls
- **Composition from atoms**: Save Button + Delete Button + Navigation Buttons
- **Behavior**: Provides consistent access to key operations across all notes
- **Interactions**: Buttons respond independently to user clicks with appropriate feedback
- **Animations/Dynamic Behaviors**: Smooth transitions between states with visual feedback on actions
- **ASCII Representation**:
```
+----------------------------------------+
| [Back] [Search] [Share]              |
| [Edit] [Save] [Delete]               |
+----------------------------------------+
```
- **Reusability**: Can be reused across all note views with appropriate context

## Guidelines
- **Consistency Rules**: 
  - Use the URL Validation Form for any screen requiring URL input and processing
  - Use the Transcript Display Container for all transcription views
  - Use the Note Card Container for all study note views
  - Maintain consistent spacing, typography, and visual hierarchy across components
  - All buttons follow a consistent style with clear labels and feedback states

- **Accessibility Standards**: 
  - All interactive elements support keyboard navigation
  - All form fields have appropriate ARIA labels for screen readers
  - Color contrast ratios meet WCAG 2.1 AA standards
  - Error messages provide specific guidance on what needs fixing
  - Loading states are clearly visible to all users
  - Dynamic content updates are announced to screen readers when changes occur

- **Implementation Notes**: 
  - All components should be implemented with responsive design principles
  - Use the provided ASCII representations as visual references for component layouts
  - Ensure proper state management for all interactive elements
  - Implement real-time validation feedback for form inputs
  - Maintain consistent loading states across all screens
  - All components follow accessibility guidelines with keyboard navigation and screen reader compatibility
  - Components should be reusable across different content types with minimal changes to structure or styling

## Additional Notes
- All components have been standardized from the various specifications to ensure consistency across the application
- Variants of components (e.g., different content types) are managed through contextual variations rather than creating new component types
- The design system promotes reusability by defining clear component boundaries and interactions
- Components have been designed with accessibility in mind, following WCAG 2.1 guidelines for keyboard navigation, screen reader compatibility, and color contrast
- Dynamic behaviors provide immediate feedback to users during interactive operations
- All components support responsive behavior across mobile, tablet, and desktop devices
- The system prioritizes clear visual hierarchy and consistent spacing to ensure usability
- Error handling is integrated throughout all flows with user-friendly messages displayed appropriately
- Components are designed to be easily maintainable and extensible as new features are added