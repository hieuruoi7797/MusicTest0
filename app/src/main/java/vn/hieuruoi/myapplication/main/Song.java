package vn.hieuruoi.myapplication.main;

public class Song {
    private String mName;
    private String mArtist;
    private int uri;

    public Song(String mName, String mArtist, int uri) {
        this.mName = mName;
        this.mArtist = mArtist;
        this.uri = uri;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmArtist() {
        return mArtist;
    }

    public void setmArtist(String mArtist) {
        this.mArtist = mArtist;
    }

    public int getUri() {
        return uri;
    }

    public void setUri(int uri) {
        this.uri = uri;
    }
}
