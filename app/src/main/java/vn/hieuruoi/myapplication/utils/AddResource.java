package vn.hieuruoi.myapplication.utils;

import android.content.Context;

import java.util.ArrayList;

import vn.hieuruoi.myapplication.R;
import vn.hieuruoi.myapplication.main.Song;


public class AddResource {
    public static ArrayList<Song> addSong(Context context){
        ArrayList<Song> mArraySong = new ArrayList<>();
        mArraySong.add(new
                Song(context.getString(R.string.song_name1),
                context.getString(R.string.artist_1),
                R.raw.mua_mua_ngau_nam_canh));
        mArraySong.add(new
                Song(context.getString(R.string.song_name2),
                context.getString(R.string.artist_2),
                R.raw.vevoiemdi));
        mArraySong.add(new
                Song(context.getString(R.string.song_name3),
                context.getString(R.string.artist_3),
                R.raw.motnha));
        return mArraySong;
    }
}
