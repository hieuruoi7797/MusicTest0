package vn.hieuruoi.myapplication.interfaces;


import vn.hieuruoi.myapplication.main.Song;

public interface OnSongListener {

    //start playing a song
    void onSongStart(Song song);

    void onPlayPause();

    void textSong(Song song);

    void onSongComplete();

}
