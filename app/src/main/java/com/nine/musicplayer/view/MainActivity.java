package com.nine.musicplayer.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.nine.musicplayer.MusicService;
import com.nine.musicplayer.view.fragments.HomeFragment;
import com.nine.musicplayer.R;
import com.nine.musicplayer.view.fragments.SongPlayerFragment;
import com.nine.musicplayer.Utils;
import com.nine.musicplayer.interfaces.OnActionCallBack;

public class MainActivity extends AppCompatActivity implements OnActionCallBack {
    HomeFragment homeFragment = new HomeFragment();
    private SongPlayerFragment songPlayerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callBack(Utils.KEY_SHOW_HOME, null);
    }


    @Override
    public void callBack(String key, Object data) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (key) {
            case Utils.KEY_SHOW_HOME:
                homeFragment.setCallBack(this);
                ft.replace(R.id.fragment_container, homeFragment);
                ft.commit();
                break;
            case Utils.KEY_SHOW_PLAYER:
                if (songPlayerFragment == null) {
                    songPlayerFragment = new SongPlayerFragment();
                    songPlayerFragment.setCallBack(this);
                    ft.add(R.id.fragment_container, songPlayerFragment);
                } else {
                    songPlayerFragment.updateSong();
                    ft.show(songPlayerFragment);
                }
                ft.commit();
                break;
            case Utils.KEY_SHOW_SERVICE:
                if (data != null) {
                    Intent intent = new Intent(this, MusicService.class);
                    intent.putExtra("action", (int) data);
                    startService(intent);
                }

                break;
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof SongPlayerFragment) {
            ((SongPlayerFragment) currentFragment).backFrag();
        }
    }
}