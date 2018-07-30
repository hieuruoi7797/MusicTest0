package vn.hieuruoi.myapplication.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.util.ArrayList;

import vn.hieuruoi.myapplication.R;
import vn.hieuruoi.myapplication.interfaces.OnSongListener;
import vn.hieuruoi.myapplication.main.Constant;
import vn.hieuruoi.myapplication.main.MainActivity;
import vn.hieuruoi.myapplication.main.Song;
import vn.hieuruoi.myapplication.utils.AddResource;


public class MusicService extends Service {

    private OnSongListener mOnSongListener;
    private final IBinder mBinder = new MusicBinder();
    private MediaPlayer mMediaPlayer;
    private int mPossition;
    private ArrayList<Song> mArraySong;

    NotificationCompat.Builder mBuider;
    public static final String TAG = "empty";
    private String divide = " - ";
    private static final String NOTIFICATION_CHANNEL_ID = "my_notification_channel";
    private static final int NOTIFICATION_ID = 1;


    public void setOnSongListener(OnSongListener onSongListener) {
        mOnSongListener = onSongListener;
    }

    //get media player
    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    //creating service
    @Override
    public void onCreate() {
        super.onCreate();
        mArraySong = new ArrayList<>();
        mArraySong = AddResource.addSong(this);
        mPossition = 0;
        mBuider = new NotificationCompat.Builder(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //get intent from activity
        if (intent != null) {
            mPossition = intent.getIntExtra("pos", -1);
            playPause();
        }
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        super.onDestroy();
    }


    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    //create a new media player
    public void createMediaplayer(final int position) {

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(this, mArraySong.get(mPossition).getUri());
        mOnSongListener.textSong(mArraySong.get(position));

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
                mOnSongListener.onSongStart(mArraySong.get(position));
                createNotification();
            }
        });

        //when a song finishes
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                nextMusic();
                mOnSongListener.onSongComplete();

            }
        });
    }

    //check for play/pause state
    public void playSong() {

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            mMediaPlayer.start();
        }
        mOnSongListener.onPlayPause();
    }

    public void playPause() {
        if (mMediaPlayer == null) createMediaplayer(mPossition);
        else playSong();
    }

    //next
    public void nextMusic() {
        try {
            mPossition++;
            if (mPossition > mArraySong.size() - 1) {
                mPossition = 0;
            }
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
            createMediaplayer(mPossition);
        } catch (Exception e) {
        }
    }

    //previous
    public void previousMusic() {
        try {
            mPossition--;
            if (mPossition < 0) {
                mPossition = mArraySong.size() - 1;
            }
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
            createMediaplayer(mPossition);
        } catch (Exception e) {
        }

    }

    public void createNotification() {
        Intent intent;
        PendingIntent pendingIntent;

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        RemoteViews notificationView = new RemoteViews(getPackageName(), R.layout.notification_layout);

        String notificationText = String.format("%s %s %s",
                mArraySong.get(mPossition).getmName(),
                divide,
                mArraySong.get(mPossition).getmArtist());
        notificationView.setTextViewText(R.id.text_notificationSongName, notificationText);

        // check for sdk version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "My Notifications", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setDescription("Channel description");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        mBuider.setSmallIcon(R.drawable.ic);
        mBuider.setCustomContentView(notificationView);
        mBuider.setWhen(System.currentTimeMillis());
        mBuider.setContentTitle(Constant.TITLE);
        mBuider.setContentText(Constant.TEXT);
        mBuider.setChannelId(NOTIFICATION_CHANNEL_ID);
        mBuider.build();

        intent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent
                .getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuider.setContentIntent(pendingIntent);

        notificationManager.notify(NOTIFICATION_ID, mBuider.build());
    }

}
