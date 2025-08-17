# Share Screen Specification

## Purpose
Enable students to share Video Transcription URLs from their device, which will be automatically transcribed and converted into structured study notes. This is the primary entry point for the video transcription-to-study-notes feature.

## ASCII Layout
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

## User Interactions
- Enter a valid Video Transcription URL in the input field
- System validates URL format and extracts video ID in real-time with visual feedback
- Click "Validate" button to initiate processing
- On success: user receives confirmation message and is redirected to Transcription View
- On failure: error message displays validation issues (invalid URL, missing video ID)

## Navigation Options
- [Go back] - Return to app home screen
- [Share again] - Re-enter a new URL

## Responsive Behavior
- On mobile: input field stacks vertically with compact layout
- On desktop: full-width form with clear visual hierarchy
- All elements maintain consistent padding and spacing across devices

## Dynamic Behaviors
- Real-time validation feedback as user types (highlighting invalid characters)
- Loading spinner appears while processing share request
- Success/failure states update immediately after submission

## Device Adaptation
- Desktop: Full-width layout with clear section separation
- Mobile: Vertical stacking with condensed form elements and simplified navigation
- Tablet: Responsive grid that maintains readability while adapting to screen size

## Additional Notes
- URL validation checks for standard video transcription service patterns (e.g., /video/ or /transcript/)
- System extracts video ID from the path component after parsing the base domain
- Error messages provide specific guidance on what needs fixing