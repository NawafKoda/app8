package com.example.app8;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * VideoAdapter:
 * - Binds a list of VideoItem objects to a RecyclerView (TV-friendly).
 * - Ensures each row is focusable/clickable for DPAD navigation (OK/Enter).
 * - Launches PlayerActivity when an item is selected.
 * - Uses stable IDs to keep focus behavior consistent while scrolling.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VH> {

    private final Context ctx;
    private final List<VideoItem> items;

    /**
     * @param ctx   Activity/Context used to inflate views and start PlayerActivity
     * @param items List of videos to render
     */
    public VideoAdapter(Context ctx, List<VideoItem> items) {
        this.ctx = ctx;
        this.items = items;
        setHasStableIds(true); // Improves focus stability for TV lists
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate a single row layout (item_video.xml)
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_video, parent, false);

        // Ensure the row itself is focusable/clickable for TV DPAD navigation
        v.setFocusable(true);
        v.setFocusableInTouchMode(true);
        v.setClickable(true);

        // Map DPAD_CENTER / ENTER to performClick() so onClick below is triggered
        v.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
                    view.performClick();
                    return true; // consume key so it doesn't propagate
                }
            }
            return false; // allow other keys (e.g., UP/DOWN) to bubble for scrolling
        });

        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        // Bind title + optional thumbnail
        VideoItem it = items.get(pos);
        h.title.setText(it.title);

        // If a thumbnail resource is provided, show it (0 means "no image")
        if (it.thumbResId != 0) {
            h.thumb.setImageResource(it.thumbResId);
        }

        // Launch the player on click (also invoked via DPAD_CENTER/ENTER above)
        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(ctx, PlayerActivity.class);
            i.putExtra("videoUri", it.uri);
            i.putExtra("title", it.title);
            ctx.startActivity(i);
        });
    }

    @Override
    public long getItemId(int position) {
        // Stable ID = position (sufficient for a static list; use a real ID if data changes)
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder:
     * - Caches row views (thumbnail + title).
     * - Reasserts focusable/clickable on the root view for TV friendliness.
     */
    public static class VH extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView title;

        public VH(@NonNull View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.videoTitle);

            // Safety: keep the row focusable/clickable even if XML changes later
            itemView.setFocusable(true);
            itemView.setFocusableInTouchMode(true);
            itemView.setClickable(true);
        }
    }
}
