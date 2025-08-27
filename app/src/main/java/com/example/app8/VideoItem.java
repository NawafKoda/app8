package com.example.app8;

/**
 * VideoItem:
 * - Simple data model representing a video entry in the app.
 * - Contains title (String), thumbnail resource ID (int), and URI (String).
 * - Used by VideoAdapter to bind data into RecyclerView rows.
 */
public class VideoItem {
    // Title of the video (displayed in the list)
    public String title;

    // Drawable resource ID for the video thumbnail (0 = no thumbnail)
    public int thumbResId;

    // URI of the video (e.g., "android.resource://...", file path, or streaming URL)
    public String uri;

    /**
     * Constructor to create a new VideoItem.
     *
     * @param title      The video title shown to the user
     * @param thumbResId Drawable resource ID for thumbnail image
     * @param uri        Video URI (local or remote)
     */
    public VideoItem(String title, int thumbResId, String uri) {
        this.title = title;
        this.thumbResId = thumbResId;
        this.uri = uri;
    }
}
