# Search Screen UI Components Specification

## Overview
The search screen enables users to find existing study notes by URL or content, improving accessibility and helping students locate previously created notes. This specification details the atomic and molecular components that make up the interface.

## Atomic Components
### URL Input Field
- **Description**: Text input for entering a video transcription URL
- **Behavior**: Shows real-time validation feedback as user types; displays error message for invalid URLs
- **Interactions**: Click to focus, Enter to submit search, blur triggers validation
- **Animations/UI Specs**: Smooth border color change on focus with visual feedback

### Content Input Field
- **Description**: Text input for entering content keywords to search notes by
- **Behavior**: Shows real-time suggestions as user types; validates against existing note content
- **Interactions**: Click to focus, Enter to submit search, blur triggers validation
- **Animations/UI Specs**: Gradient background on active state with subtle animation

### Search Button
- **Description**: Primary action button to initiate the search process
- **Behavior**: Disabled when fields are empty; enables on valid input; shows loading state during operation
- **Interactions**: Click triggers search request, tap on mobile triggers action
- **Animations/UI Specs**: Smooth color transition from disabled to enabled states with loading spinner

### Results List Item
- **Description**: Individual entry in results list showing note metadata
- **Behavior**: Displays URL and associated content summary; clickable to view full note
- **Interactions**: Click opens detailed study note view
- **Animations/UI Specs**: Subtle fade-in animation on appearance, smooth hover effects

### Filter Options
- **Description**: Buttons for filtering results by date or sorting by relevance
- **Behavior**: Changes state when selected; updates results list accordingly
- **Interactions**: Tap to apply filter/sort, click to toggle state
- **Animations/UI Specs**: Smooth transition between states with visual feedback

### Navigation Buttons
- **Description**: [Back], [Create New Note], and [Clear Search] buttons
- **Behavior**: Back navigates to previous screen; Create New starts transcription workflow; Clear resets search fields
- **Interactions**: Tap to execute action, mobile touch target optimized
- **Animations/UI Specs**: Solid button style with consistent spacing and padding

## Molecular Components
### Search Form Molecule
- **Description**: Composite component combining URL and content inputs with a search button
- **Combined Behavior**: Validates both fields before enabling the search button; shows real-time feedback on input changes
- **Interactions**: Input errors disable the search button; successful validation enables it
- **ASCII Representation**:
```
+----------------------------------------+
|  Find Study Notes                    |
+----------------------------------------+
| Search by:                           |
| [URL]   [Content]                   |
| https://example.com/video/xyz123    |
|                                     ▶ |
| [Search]                            |
+----------------------------------------+
```
- **Reusability**: Can be reused across other search screens or content discovery flows

### Results Display Molecule
- **Description**: Component showing list of found study notes with metadata
- **Combined Behavior**: Dynamically updates results as search criteria change; maintains consistent layout and spacing
- **Interactions**: Click on a note opens its detailed view; filtering options update the result set
- **ASCII Representation**:
```
| Results:                            |
| • https://example.com/video/xyz123  |
|   - Neural networks and deep learning|
| • https://example.com/video/abc456  |
|   - Introduction to AI fundamentals |
+----------------------------------------+
```
- **Reusability**: Can be reused for any content search or list display

### Navigation Bar Molecule
- **Description**: Composite component containing back, create new, and clear options
- **Combined Behavior**: Provides consistent navigation across the app; enables easy return to previous screens or creation of new notes
- **Interactions**: All buttons respond to tap/press events with appropriate actions
- **ASCII Representation**:
```
| [Back] - Return to main app screen
| [Create New Note] - Initiate new transcription-to-note workflow
| [Clear Search] - Reset search fields
+----------------------------------------+
```
- **Reusability**: Can be reused across various screens requiring navigation controls

## Additional Notes
- All components follow accessibility guidelines with proper ARIA labels and keyboard navigation support
- Real-time search updates provide immediate user feedback during queries
- Error handling is integrated throughout the flow with clear, actionable messages
- The design maintains consistency with the overall app's visual language and component library
- Dynamic behaviors are clearly defined to help developers implement loading states, progress indicators, and real-time updates
- Search suggestions appear when typing (if applicable) to improve user experience
- Results are sorted by relevance (most recent first) and can be filtered by date
- On mobile devices, search fields stack vertically with simplified interface; on desktop, full-width display is used
- The results list adapts to available screen space (scrollable on narrow screens)
