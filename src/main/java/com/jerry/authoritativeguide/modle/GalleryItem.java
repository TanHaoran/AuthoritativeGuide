package com.jerry.authoritativeguide.modle;

/**
 * Created by Jerry on 2017/1/12.
 */

public class GalleryItem {

    private String id;
    private String title;
    private String url_s;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl_s() {
        return url_s;
    }

    public void setUrl_s(String url_s) {
        this.url_s = url_s;
    }

    @Override
    public String toString() {
        return "GalleryItem{" +
                "title='" + title + '\'' +
                '}';
    }
}
