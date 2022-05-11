package com.nine.musicplayer.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nine.musicplayer.GrandMotionLayout;
import com.nine.musicplayer.MusicService;
import com.nine.musicplayer.R;
import com.nine.musicplayer.Utils;
import com.nine.musicplayer.interfaces.OnActionCallBack;
import com.nine.musicplayer.manager.MediaManager;
import com.nine.musicplayer.manager.SongManager;
import com.nine.musicplayer.model.MessageEvent;
import com.nine.musicplayer.model.Song;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SongPlayerFragment extends Fragment {

    private TextView playerSongName, playerSongArtist, playerSongDuration, playerSongProgress;
    private ImageView playerSongImage, playerPauseOrResumeButton;
    private SeekBar songPlayerSeekbar;
    private GrandMotionLayout rootMotionLayout;
    private Handler handler;
    private Runnable runnable;
    private OnActionCallBack callBack;

    public void setCallBack(OnActionCallBack callBack) {
        this.callBack = callBack;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_player, container, false);

        Song song = SongManager.getInstance().getCurrentSong();
        playerSongName = view.findViewById(R.id.player_song_name);
        playerSongArtist = view.findViewById(R.id.player_song_artist);
        playerSongDuration = view.findViewById(R.id.player_song_duration);
        playerSongProgress = view.findViewById(R.id.player_song_progress);
        playerSongImage = view.findViewById(R.id.player_song_image);
        songPlayerSeekbar = view.findViewById(R.id.song_player_seekbar);
        rootMotionLayout = view.findViewById(R.id.root_motion_layout);
        ImageView playerPreviousButton = view.findViewById(R.id.player_previous_button);
        ImageView playerNextButton = view.findViewById(R.id.player_next_button);
        playerPauseOrResumeButton = view.findViewById(R.id.player_pause_or_resume_button);

        playSong(song);


        playerPauseOrResumeButton.setOnClickListener(v -> handlePauseResumeMusic());
        playerNextButton.setOnClickListener(v -> gotoService(Utils.ACTION_NEXT));
        playerPreviousButton.setOnClickListener(v -> gotoService(Utils.ACTION_PREVIOUS));

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        switch (messageEvent.getMessage()) {
            case Utils.ACTION_PAUSE:
                playerPauseOrResumeButton.setImageResource(R.drawable.play);
                handler.removeCallbacks(runnable);
                break;
            case Utils.ACTION_RESUME:
                playerPauseOrResumeButton.setImageResource(R.drawable.pause_button);
                handler.post(runnable);
                break;
            case Utils.ACTION_NEXT:
            case Utils.ACTION_PREVIOUS:
                playSong(SongManager.getInstance().getCurrentSong());
                break;
        }
    }

    private void handlePauseResumeMusic() {
        if (MediaManager.getInstance().isPause()) {
            playerPauseOrResumeButton.setImageResource(R.drawable.pause_button);
            handler.post(runnable);
            gotoService(Utils.ACTION_RESUME);
        } else {
            playerPauseOrResumeButton.setImageResource(R.drawable.play);
            handler.removeCallbacks(runnable);
            gotoService(Utils.ACTION_PAUSE);
        }
    }


    private void playSong(Song song) {
        playerSongName.setText(song.getName());
        playerSongArtist.setText(song.getArtist());
        playerSongDuration.setText(Utils.convertMilliToMinuteString(song.getDuration()));
        playerSongProgress.setText("00:00");
        playerPauseOrResumeButton.setImageResource(R.drawable.pause_button);
        playerSongImage.setImageBitmap(song.getThumbnail());
        gotoService(Utils.ACTION_START);
        intiSeekBar(song);
    }

    private void intiSeekBar(Song song) {
        songPlayerSeekbar.setMax(song.getDuration());
        runnable = new Runnable() {
            @Override
            public void run() {
                songPlayerSeekbar.setProgress(MediaManager.getInstance().getProgress());
                playerSongProgress.setText(Utils.convertMilliToMinuteString(MediaManager.getInstance().getProgress()));
                handler.postDelayed(this, 1000);
            }
        };
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        handler = new Handler();
        handler.post(runnable);

        songPlayerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MediaManager.getInstance().setProgress(progress);
                    playerSongProgress.setText(Utils.convertMilliToMinuteString(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void updateSong() {
        Song song = SongManager.getInstance().getCurrentSong();
        playSong(song);
        rootMotionLayout.transitionToStart();
    }

    public void backFrag() {
        rootMotionLayout.transitionToEnd();
    }

    private void gotoService(int action) {
        callBack.callBack(Utils.KEY_SHOW_SERVICE, action);
    }
}
