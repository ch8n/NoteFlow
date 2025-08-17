# Search Screen Specification

## Purpose
Enable users to find existing study notes by URL or content, improving accessibility and helping students locate previously created notes.

## ASCII Layout
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
| Results:                            |
| • https://example.com/video/xyz123  |
|   - Neural networks and deep learning|
| • https://example.com/video/abc456  |
|   - Introduction to AI fundamentals |
+----------------------------------------+
| [Filter by Date] [Sort by Relevance] |
+----------------------------------------+
```

## User Interactions
- Enter search criteria in URL or content fields
- Click "Search" button to find matching notes
- Browse through results with metadata (URL, timestamps)
- Click on a note to view its detailed content

## Navigation Options
- [Back] - Return to main app screen
- [Create New Note] - Initiate new transcription-to-note workflow
- [Clear Search] - Reset search fields

## Responsive Behavior
- On mobile: search fields stack vertically with simplified interface
- On desktop: full-width display with proper section separation
- Results list adapts to available screen space (scrollable on narrow screens)

## Dynamic Behaviors
- Real-time search as user types (results update immediately)
- Loading indicator appears during search operations
- Search suggestions appear when typing (if applicable)

## Device Adaptation
- Desktop: Full-width display with proper section separation and responsive layout
- Mobile: Vertical stacking of elements with simplified navigation
- Tablet: Balanced layout between content width and navigation elements

## Additional Notes
- Supports both URL-based and content-based searches
- Results are sorted by relevance (most recent first)
- Filtering options allow users to narrow results by date or other criteria
- Search functionality is optimized for common user queries and patterns