# Share Screen UI Components Specification

## Overview
This specification documents the reusable UI components for the Share Screen, which enables students to share Video Transcription URLs from their device and automatically convert them into structured study notes.

## Atomic Components
### URL Input Field
- **Description**: Text input field for entering a Video Transcription URL
- **Behavior**: 
  - Default state: Empty with placeholder text
  - Focus state: Highlighted border, cursor appears
  - Error state: Red border, error message displayed
  - Validation state: Real-time feedback as user types
- **Interactions**: 
  - User can type or paste a URL
  - Input triggers real-time validation with visual feedback
  - On focus: Field gains active state
  - On blur: Validates URL format and extracts video ID
- **Animations/UI Specs**: 
  - Smooth transition on focus/blur
  - Real-time character highlighting for invalid inputs
  - Progress indicator during validation

### Validate Button
- **Description**: Primary action button to initiate processing of the shared URL
- **Behavior**: 
  - Default state: Enabled, text shows "Validate"
  - Loading state: Disabled, shows spinner animation
  - Success state: Shows success confirmation message
  - Error state: Shows error message with retry option
- **Interactions**: 
  - Click triggers validation process and API call to transcription service
  - On successful processing: Redirects to Transcription View
  - On failure: Displays specific error messages (invalid URL, missing video ID)
  - Can be re-clicked after failure
- **Animations/UI Specs**: 
  - Smooth spinner animation during processing
  - Button color changes from primary to disabled during loading
  - Success state shows a green checkmark animation

### Status Display
- **Description**: Component that displays the current state of URL sharing
- **Behavior**: 
  - Default: "Status: Ready to share"
  - Processing: Shows "Processing..." with loading spinner
  - Success: Shows success message and enables navigation
  - Error: Displays error details and retry option
- **Interactions**: 
  - Updates automatically after URL validation or processing
  - Changes state based on API response
  - Triggers navigation flow changes when successful
- **Animations/UI Specs**: 
  - Smooth fade-in/fade-out transitions between states
  - Loading spinner appears during processing
  - Success animation shows a brief celebration effect

### Validation Feedback Indicator
- **Description**: Visual feedback element showing URL validation results
- **Behavior**: 
  - Default: Hidden
  - Valid state: Shows green checkmark with success message
  - Invalid state: Shows red cross with specific error details
  - Real-time state: Updates as user types
- **Interactions**: 
  - Appears immediately after URL input
  - Changes based on validation results
  - Provides guidance for fixing invalid inputs
- **Animations/UI Specs**: 
  - Smooth transition between states
  - Real-time character highlighting during typing
  - Error messages appear with subtle pulse animation

## Molecular Components
### URL Validation Form
- **Description**: Composed of input field, validation feedback, and validate button that together provide a complete URL sharing experience
- **Combined Behavior**: 
  - Validates URL format in real-time as user types
  - Extracts video ID from path component after parsing base domain
  - Triggers API call when Validate button is clicked
  - Provides immediate visual feedback for validation results
- **Interactions**: 
  - Input field and validate button interact to trigger validation process
  - Validation feedback updates based on input state
  - Form state changes from "ready" to "processing" to "success/failure"
  - Error messages guide user to fix invalid inputs
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
- **Reusability**: 
  - Can be reused across screens where URL input is needed
  - Adaptable for different content types with minimal changes
  - Serves as a template for other form-based sharing experiences

## Additional Notes
- All components follow consistent design language and accessibility principles (WCAG 2.1)
- Real-time validation provides immediate feedback to users, reducing errors
- The URL extraction logic follows standard patterns (e.g., /video/ or /transcript/) as defined in the product requirements
- Error messages provide specific guidance on what needs fixing
- Components are designed to be responsive across mobile and desktop devices with appropriate layout adjustments
- The validation feedback indicator is critical for guiding users through the sharing process
- All components support keyboard navigation and screen reader compatibility with proper ARIA labels