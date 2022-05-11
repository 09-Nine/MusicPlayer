package com.nine.musicplayer.manager;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

public class MediaManager {
    private MediaPlayer mediaPlayer;
    private boolean isPause;

    public static MediaManager getInstance() {
        return SingletonMediaHelper.INSTANCE;
    }

    private static class SingletonMediaHelper {
        private static final MediaManager INSTANCE = new MediaManager();
    }

    public void playMusic(Context context, Uri uri, MediaPlayer.OnCompletionListener completionListener) {

        try {
            isPause = false;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnCompletionListener(completionListener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public int pauseMedia() {
        if (mediaPlayer != null) {
            isPause = !isPause;
            mediaPlayer.pause();
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void resumeMedia(int progress) {
        if (mediaPlayer != null) {
            isPause = !isPause;
            mediaPlayer.seekTo(progress);
            mediaPlayer.start();
        }
    }

    public int getProgress() {
        if (mediaPlayer != null) {

            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void setProgress(int milli) {
        if (mediaPlayer != null) {

            mediaPlayer.seekTo(milli);
        }
    }

    public boolean isPause() {
        return isPause;
    }
}
