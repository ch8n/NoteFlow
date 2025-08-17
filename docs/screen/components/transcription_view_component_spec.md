# Transcription View UI Components Specification

## Overview
This document specifies the reusable UI components for the Transcription View screen, which displays extracted audio transcripts with accurate timing information. The design focuses on clear presentation of content with interactive elements and loading states.

## Atomic Components
### Timestamp Marker
- **Description**: Displays timecodes in MM:SS format to indicate when specific segments of speech occur
- **Behavior**: Default state shows timecode; hover state may highlight the segment visually (optional)
- **Interactions**: No direct interactions, but serves as visual reference for content segmentation
- **Animations/UI Specs**: Smooth fade-in animation on text load, subtle color change on active segment

### Text Content Block
- **Description**: Displays actual spoken content from the audio transcription
- **Behavior**: Default state shows full text; real-time updates during loading (partial content)
- **Interactions**: Clickable to select and copy text segments
- **Animations/UI Specs**: Smooth transition when new segment loads, maintains original formatting including pauses and emphasis

### Copy Transcript Button
- **Description**: Enables users to copy the complete transcript to clipboard
- **Behavior**: Default state shows button label; on click, triggers copy action with success toast
- **Interactions**: Click triggers copy operation, displays confirmation message upon completion
- **Animations/UI Specs**: Subtle pulse animation on successful copy, slight elevation change on hover

### Save Transcript Button
- **Description**: Allows users to save the transcript for later reference in local storage
- **Behavior**: Default state shows button label; on click, triggers save operation with confirmation dialog
- **Interactions**: Click opens confirmation modal, user must confirm before saving
- **Animations/UI Specs**: Loading spinner appears during save process, fade-in animation after successful save

### Loading Spinner
- **Description**: Indicates active processing of transcription extraction
- **Behavior**: Appears when API call is in progress; disappears upon completion
- **Interactions**: No direct interactions, serves as loading state indicator
- **Animations/UI Specs**: Smooth rotating animation, positioned prominently at the top center

### Progress Bar
- **Description**: Shows completion percentage during transcription extraction process
- **Behavior**: Starts at 0% and animates to 100% as content loads; updates in real-time
- **Interactions**: No direct interactions, serves as progress indicator
- **Animations/UI Specs**: Smooth linear animation with subtle color gradient from start to end

## Molecular Components
### Transcript Display Container
- **Description**: Combines timestamp markers and text content blocks into a cohesive presentation of the full transcript
- **Combined Behavior**: Displays all segments in chronological order with accurate timing information
- **Interactions**: Content updates dynamically as transcription loads, timestamps adjust based on screen width
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
- **Combined Behavior**: Provides clear pathways to return to previous screen or proceed to next steps in the workflow
- **Interactions**: Back button returns user to Share Screen; Generate Study Notes triggers note creation process; Search opens search functionality
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

## Additional Notes
- All components follow accessibility guidelines with proper ARIA labels for keyboard navigation and screen reader compatibility
- Timestamps maintain original formatting including pauses, emphasis, and speaker changes as per user requirements
- Loading states provide clear feedback during API calls to transcription service
- Responsive behavior ensures text wraps properly on mobile devices while maintaining timestamp alignment across device types
- Components are designed with consistent spacing, typography, and visual hierarchy to ensure uniformity across the application
- Error handling is integrated throughout the flow (e.g., invalid URLs, network issues) with user-friendly messages displayed appropriately
