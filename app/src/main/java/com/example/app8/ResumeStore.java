package com.example.app8;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * ResumeStore:
 * - Utility class to persist and retrieve video playback positions.
 * - Uses SharedPreferences to store last watched position for each video.
 * - Provides methods to save, load, and clear resume positions.
 */
public class ResumeStore {
    // Name of the SharedPreferences file
    private static final String PREF = "resume_positions";

    /**
     * Save the playback position (in milliseconds) for a given key.
     *
     * @param ctx   Context to access SharedPreferences
     * @param key   Unique key for this video (e.g., hash of the URI)
     * @param posMs Playback position in milliseconds
     */
    public static void save(Context ctx, String key, long posMs) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit().putLong(key, posMs).apply();
    }

    /**
     * Load the last saved playback position for a given key.
     *
     * @param ctx Context to access SharedPreferences
     * @param key Key for this video
     * @return Saved position in milliseconds, or 0 if none
     */
    public static long load(Context ctx, String key) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getLong(key, 0L);
    }

    /**
     * Clear the saved playback position for a given key.
     *
     * @param ctx Context to access SharedPreferences
     * @param key Key for this video
     */
    public static void clear(Context ctx, String key) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit().remove(key).apply();
    }

    /**
     * Generate a unique key for storing resume position of a video,
     * based on the hash of its URI.
     *
     * @param uri The video URI
     * @return A string key derived from the URI
     */
    public static String keyFor(String uri) {
        return "pos_" + uri.hashCode();
    }
}
