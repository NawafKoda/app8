package com.example.app8;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import java.util.Locale;

/**
 * PlayerActivity:
 * - Plays video using Media3 ExoPlayer (v1.8.0).
 * - Shows a loading indicator while buffering.
 * - Saves playback position (resume) and prompts on next entry.
 * - DPAD Right/Left = +10s / -10s seek.
 */
public class PlayerActivity extends AppCompatActivity {

    private static final String TAG = "PlayerActivity";

    private ExoPlayer player;
    private PlayerView playerView;
    private ProgressBar loading;

    private String videoUri;
    private String resumeKey;
    private long savedPos = 0L;
    private boolean askedResume = false;

    // Resume thresholds
    private static final long RESUME_MIN_MS = 1_000;      // Don't prompt if less than 1s
    private static final long CLEAR_IF_WITHIN_MS = 5_000; // Clear if within last 5s
    private static final long SEEK_MS = 10_000;           // DPAD seek step (10s)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Bind UI elements
        playerView = findViewById(R.id.player_view);
        loading    = findViewById(R.id.loading);

        // Get video info from intent
        videoUri = getIntent().getStringExtra("videoUri");
        String title = getIntent().getStringExtra("title");
        if (title != null) {
            ((TextView) findViewById(R.id.title)).setText(title);
        }

        // Load last resume position
        resumeKey = ResumeStore.keyFor(videoUri != null ? videoUri : "");
        savedPos  = ResumeStore.load(this, resumeKey);

        // Create Media3 ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        playerView.setUseController(true);

        // Prepare media item (guard null)
        if (videoUri == null || videoUri.isEmpty()) {
            Toast.makeText(this, "Video URI is empty.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Empty videoUri from intent");
            return;
        }
        MediaItem item = MediaItem.fromUri(Uri.parse(videoUri));
        player.setMediaItem(item);

        // Loading indicator + error diagnostics
        player.addListener(new Player.Listener() {
            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                loading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                loading.setVisibility(View.GONE);
                Toast.makeText(PlayerActivity.this,
                        "Playback error: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "onPlayerError", error);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        player.prepare();

        if (!askedResume && savedPos >= RESUME_MIN_MS) {
            askedResume = true;
            showResumeDialog(savedPos);
        } else {
            player.play(); // start immediately
        }
    }

    private void showResumeDialog(long positionMs) {
        long sec = positionMs / 1000;
        String msg = String.format(
                Locale.getDefault(),
                "المتابعة من %d:%02d؟", (sec / 60), (sec % 60)
        );

        new AlertDialog.Builder(this)
                .setTitle("استمرار المتابعة")
                .setMessage(msg)
                .setPositiveButton("نعم", (d, w) -> {
                    player.seekTo(positionMs);
                    player.play();
                })
                .setNegativeButton("من " +
                        "البداية", (d, w) -> {
                    player.seekTo(0);
                    player.play();
                })
                .setOnCancelListener(d -> {
                    player.seekTo(positionMs);
                    player.play();
                })
                .show();
    }

    private void persistPosition() {
        if (player == null) return;
        long dur = player.getDuration();
        long pos = player.getCurrentPosition();

        // Near the end? Clear progress so next launch starts from 0
        if (dur > 0 && dur - pos <= CLEAR_IF_WITHIN_MS) {
            ResumeStore.clear(this, resumeKey);
        } else {
            ResumeStore.save(this, resumeKey, pos);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        persistPosition();
        if (player != null) player.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        persistPosition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    // DPAD Right/Left = seek ±10s
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (player != null && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                long pos = player.getCurrentPosition();
                long dur = player.getDuration();
                long to = (dur > 0) ? Math.min(pos + SEEK_MS, dur) : pos + SEEK_MS;
                player.seekTo(to);
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                long pos = player.getCurrentPosition();
                long to = Math.max(pos - SEEK_MS, 0);
                player.seekTo(to);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
