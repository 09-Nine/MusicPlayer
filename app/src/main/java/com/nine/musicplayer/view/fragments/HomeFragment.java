package com.nine.musicplayer.view.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nine.musicplayer.R;
import com.nine.musicplayer.Utils;
import com.nine.musicplayer.adapters.SongAdapter;
import com.nine.musicplayer.interfaces.AdapterListener;
import com.nine.musicplayer.interfaces.OnActionCallBack;
import com.nine.musicplayer.manager.SongManager;
import com.nine.musicplayer.model.Song;


public class HomeFragment extends Fragment {

    private SongAdapter songAdapter;
    private OnActionCallBack callBack;

    public void setCallBack(OnActionCallBack callBack) {
        this.callBack = callBack;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        SongManager.getInstance().getSong(requireContext().getApplicationContext());
        RecyclerView songRecycleView = view.findViewById(R.id.song_recycler_view);
        songRecycleView.setHasFixedSize(true);
        songRecycleView.setLayoutManager(new LinearLayoutManager(requireContext()));
        songAdapter = new SongAdapter(SongManager.getInstance().getSongList());
        songRecycleView.setAdapter(songAdapter);
        songAdapter.setListener(new AdapterListener() {
            @Override
            public void onClick(Song song) {
                SongManager.getInstance().setCurrentSong(song);
                gotoSongPlayerFragment();
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "OKK", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Fuck you", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void gotoSongPlayerFragment() {
        callBack.callBack(Utils.KEY_SHOW_PLAYER, null);
    }
}