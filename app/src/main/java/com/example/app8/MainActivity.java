package com.example.app8;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * MainActivity:
 * - Displays a vertical list of videos in a RecyclerView.
 * - Supports TV remote navigation (DPAD up/down) and selection (OK/Enter).
 * - Each item opens PlayerActivity to play the selected video.
 */
public class MainActivity extends AppCompatActivity {

    // RecyclerView reference for the main list
    RecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bind this Activity to activity_main.xml UI
        setContentView(R.layout.activity_main);

        // Get RecyclerView from layout
        list = findViewById(R.id.recyclerView);

        // NOTE: setLayoutManager is called twice below; the second call (with VERTICAL)
        // overrides this first one. Keeping this line is harmless but redundant.
        list.setLayoutManager(new LinearLayoutManager(this));

        // Build the data source for the list (VideoItem entries)
        ArrayList<VideoItem> data = new ArrayList<>();

        // Build URIs for local videos bundled under res/raw
        String v1 = "android.resource://" + getPackageName() + "/" + R.raw.video1;
        String v2 = "android.resource://" + getPackageName() + "/" + R.raw.video2;
        String v3 = "android.resource://" + getPackageName() + "/" + R.raw.video3;

        // Add list items: (title, thumbnail from drawable, video URI)
        data.add(new VideoItem("تطبيق ثمانية", R.drawable.thumb1, v1));
        data.add(new VideoItem("المقدمة الرسمية لكأس السوبر السعودي", R.drawable.thumb2, v2));
        data.add(new VideoItem("خارج التغطية", R.drawable.thumb3, v3));

        // (Redundant) Fetching the same RecyclerView again—safe but unnecessary
        list = findViewById(R.id.recyclerView);

        // Explicitly set a vertical LinearLayoutManager (final effective layout manager)
        list.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(
                this,
                androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
                false
        ));

        // Attach the adapter so items render in the RecyclerView
        list.setAdapter(new VideoAdapter(this, data));

        // Request focus on the first item after the list is laid out,
        // so TV remote navigation works immediately without touch input.
        list.post(() -> {
            RecyclerView.ViewHolder vh = list.findViewHolderForAdapterPosition(0);
            if (vh != null) {
                vh.itemView.requestFocus(); // highlight first row initially
            }
        });
    }
}
