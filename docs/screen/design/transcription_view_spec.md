# Transcription View Specification

## Purpose
Display the extracted audio transcript with accurate timing information, allowing users to view and copy the complete transcription before it's converted into structured study notes.

## ASCII Layout
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
| Copy Transcript                      |
| Save Transcript                     |
+----------------------------------------+
```

## User Interactions
- View complete transcript with timestamp markers
- Click "Copy Transcript" to copy text to clipboard
- Click "Save Transcript" to store in local storage for later reference
- System displays loading state during transcription extraction

## Navigation Options
- [Back] - Return to Share Screen
- [Generate Study Notes] - Proceed to create structured study notes from transcript
- [Search] - Find existing study notes by URL or content

## Responsive Behavior
- On mobile: text wraps within container with reduced font size
- On desktop: full-width display with proper line spacing and margins
- Timestamps adjust position based on screen width (left-aligned on wide screens)

## Dynamic Behaviors
- Loading spinner appears during transcription extraction process
- Progress bar shows completion percentage (0% → 100%)
- Real-time updates as transcript loads

## Device Adaptation
- Desktop: Full-width display with proper line spacing and timestamp alignment
- Mobile: Text wraps automatically, timestamps appear at top of each segment
- Tablet: Balanced layout between content width and navigation elements

## Additional Notes
- Timestamps are displayed in MM:SS format for easy reference
- Transcript maintains original formatting including pauses, emphasis, and speaker changes
- Loading states provide clear feedback during API calls to transcription service