package vn.edu.tlu.cse.hyn.soundplay.data.model;

public class MusicItem {
    private String title;
    private int imageRes;

    public MusicItem(String title, int imageRes) {
        this.title = title;
        this.imageRes = imageRes;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
