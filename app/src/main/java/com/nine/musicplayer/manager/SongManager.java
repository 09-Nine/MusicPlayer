package com.nine.musicplayer.manager;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.nine.musicplayer.R;
import com.nine.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SongManager {
    private final List<Song> songList = new ArrayList<>();
    private Song currentSong;

    public static SongManager getInstance() {
        return SingletonSongHelper.INSTANCE;
    }

    private static class SingletonSongHelper {
        private static final SongManager INSTANCE = new SongManager();
    }

    public void getSong(Context context) {
        songList.clear();
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
        };

        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";

        Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                null,
                null,
                null
        );

        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
        int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
        int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
        int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);

        while (cursor.moveToNext()) {
            long id = cursor.getLong(idColumn);
            String name = cursor.getString(nameColumn);
            int duration = cursor.getInt(durationColumn);
            String artist = cursor.getString(artistColumn);
            Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
            Bitmap art = null;
            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                byte[] rawArt;
                BitmapFactory.Options bfo=new BitmapFactory.Options();
                mmr.setDataSource(context, contentUri);
                rawArt = mmr.getEmbeddedPicture();
                if (null != rawArt) {
                    art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
                } else {
                    art = BitmapFactory.decodeResource(context.getResources(), R.drawable.music_placeholder);
                }
            } catch (RuntimeException ex) {
                Log.i("TAG", "Do not have thumbnail");
            }
            songList.add(new Song(name, artist, duration, contentUri, art));
        }

        cursor.close();
    }

    public List<Song> getSongList() {
        return songList;
    }

    public int getIndexOfSong(Song song) {
        return songList.indexOf(song);
    }


    public void nextSong() {
        int pos = getIndexOfSong(currentSong);
        if (pos == songList.size() - 1) {
            currentSong = songList.get(0);
        } else {
            currentSong = songList.get(pos + 1);
        }

    }

    public void previousSong() {
        int pos = getIndexOfSong(currentSong);
        if (pos == 0) {
            currentSong = songList.get(songList.size() - 1);
        } else {
            currentSong = songList.get(pos - 1);
        }
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(Song currentSong) {
        this.currentSong = currentSong;
    }

}
