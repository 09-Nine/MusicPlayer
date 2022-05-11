package com.nine.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.nine.musicplayer.manager.MediaManager;
import com.nine.musicplayer.manager.SongManager;
import com.nine.musicplayer.model.MessageEvent;
import com.nine.musicplayer.model.Song;

import org.greenrobot.eventbus.EventBus;

public class MusicService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action = intent.getIntExtra("action", 0);
        Log.e("out", action + "");
        if (action != 0) {;
            switch (action) {
                case Utils.ACTION_RESUME:
                    MediaManager.getInstance().resumeMedia(MediaManager.getInstance().getProgress());
                    break;
                case Utils.ACTION_PAUSE:
                    MediaManager.getInstance().pauseMedia();
                    break;
                case Utils.ACTION_START:
                    startMusic(SongManager.getInstance().getCurrentSong());
                    break;
                case Utils.ACTION_NEXT:
                    SongManager.getInstance().nextSong();
                    break;
                case Utils.ACTION_PREVIOUS:
                    SongManager.getInstance().previousSong();
                    break;
            }
            EventBus.getDefault().post(new MessageEvent(action));
            pushNotification(SongManager.getInstance().getCurrentSong());
        }


        return START_NOT_STICKY;
    }

    private void startMusic(Song s) {
        MediaManager.getInstance().resetMedia();
        MediaManager.getInstance().playMusic(getApplicationContext(), s.getUri(), new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                SongManager.getInstance().nextSong();
                EventBus.getDefault().post(new MessageEvent(Utils.ACTION_NEXT));
                startMusic(SongManager.getInstance().getCurrentSong());
                pushNotification(SongManager.getInstance().getCurrentSong());
            }
        });
    }


    private void pushNotification(Song song) {
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, "tag");
        MediaSessionCompat.Token token = mediaSessionCompat.getSessionToken();
        Notification notification;


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(song.getThumbnail())
                .setSubText("Music Player")
                .setContentTitle(song.getName())
                .setContentText(song.getArtist())
                .addAction(R.drawable.ic_previous, "Previous", getIntent(Utils.ACTION_PREVIOUS))
                .addAction(MediaManager.getInstance().isPause() ? R.drawable.ic_play : R.drawable.ic_pause,
                        MediaManager.getInstance().isPause() ? "Resume" : "Pause",
                        MediaManager.getInstance().isPause() ? getIntent(Utils.ACTION_RESUME) : getIntent(Utils.ACTION_PAUSE))
                .addAction(R.drawable.ic_next, "Next", getIntent(Utils.ACTION_NEXT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            notification = builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0,1,2,3))
                    .build();
        } else {
            notification = builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0,1,2,3)
                    .setMediaSession(token))
                    .build();
        }
        startForeground(1, notification);
    }

    private PendingIntent getIntent(int action) {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("action", action);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.getService(this, action, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            return PendingIntent.getService(this, action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }
}
