package vn.hieuruoi.myapplication.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import vn.hieuruoi.myapplication.R;
import vn.hieuruoi.myapplication.interfaces.OnSongListener;
import vn.hieuruoi.myapplication.services.MusicService;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, OnSongListener {

    private TextView mTextSongName, mTextPlayingTime, mTextSongLength;
    private SeekBar mSeekBarSong;
    private ImageButton mImagebuttonPrevious, mImagebuttonPlay, mImagebuttonNext;
    private MusicService musicService;
    private boolean isBound = false;
    private String mDivide = Constant.DIVIDE;
    private SimpleDateFormat mTimeSong = new SimpleDateFormat("mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find view by id
        findID();

        //set on click
        mImagebuttonNext.setOnClickListener(this);
        mImagebuttonPlay.setOnClickListener(this);
        mImagebuttonPrevious.setOnClickListener(this);
        //seekbar on change
        mSeekBarSong.setOnSeekBarChangeListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    //button onClick
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_next:
                musicService.nextMusic();
                mSeekBarSong.setProgress(0);
                break;

            case R.id.button_play:
                musicService.playPause();
                updateTimeSong();
                break;
            case R.id.button_previous:
                musicService.previousMusic();
                mSeekBarSong.setProgress(0);
                break;
        }
    }

    // start service
    @Override
    public ComponentName startForegroundService(Intent service) {
        return super.startForegroundService(service);
    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) iBinder;
            musicService = binder.getService();
            musicService.setOnSongListener(MainActivity.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //on seekbar change listener

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //seek to another possition in the song
        try {
            musicService.getMediaPlayer().seekTo(mSeekBarSong.getProgress());
        } catch (Exception e) {
            mSeekBarSong.setProgress(0);
        }
    }

    //set text for song's time and set max for seekbar
    @Override
    public void onSongStart(Song song) {
        mTextSongLength.setText(mTimeSong.format(musicService.getMediaPlayer().getDuration()));
        mSeekBarSong.setMax(musicService.getMediaPlayer().getDuration());
        onPlayPause();
    }

    //check the playing state of music service
    @Override
    public void onPlayPause() {
        if (musicService.getMediaPlayer().isPlaying()) {
            mImagebuttonPlay.setImageResource(R.drawable.ic_pause_black_24dp);
        } else {
            mImagebuttonPlay.setImageResource(R.drawable.play);
        }
    }

    //set text for song's name
    @Override
    public void textSong(Song song) {
        mTextSongName
                .setText(String.format("%s%s%s", song.getmName(), mDivide, song.getmArtist()));
    }

    //when a song finishes
    @Override
    public void onSongComplete() {
        mSeekBarSong.setProgress(0);
        updateTimeSong();
    }

    //update time for mTextPlayingTime and seekbar
    private void updateTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTextPlayingTime
                        .setText(mTimeSong
                                .format(musicService.getMediaPlayer().getCurrentPosition()));
                mSeekBarSong
                        .setProgress(musicService.getMediaPlayer().getCurrentPosition());
                handler.postDelayed(this, 500);
            }
        }, 100);
    }

    //find view by id
    private void findID() {
        mTextSongName = findViewById(R.id.text_songName);
        mTextPlayingTime = findViewById(R.id.text_playing);
        mTextSongLength = findViewById(R.id.text_length);
        mSeekBarSong = findViewById(R.id.seekbar_musicSeekBar);
        mImagebuttonPrevious = findViewById(R.id.button_previous);
        mImagebuttonPlay = findViewById(R.id.button_play);
        mImagebuttonNext = findViewById(R.id.button_next);
    }

}
