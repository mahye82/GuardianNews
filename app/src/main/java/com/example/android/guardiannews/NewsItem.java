package com.example.android.guardiannews;

import android.graphics.Bitmap;

/**
 * This class represents an individual news item.
 */
public class NewsItem {
    /** The URL leading to the news item on the Guardian website. */
    private String url;
    /** The headline of the news item. */
    private String webTitle;
    /** The {@link Bitmap} thumbnail for the news item. */
    private Bitmap thumbnail;
    /** The section of the site that this news item belongs to. */
    private String sectionName;
    /** The date when the news item was published on the web, as a String. */
    private String time;
    /** The name of the contributor. */
    private String contributor;

    public NewsItem(String url, String webTitle, Bitmap thumbnail, String sectionName, String time, String contributor) {
        this.url = url;
        this.webTitle = webTitle;
        this.thumbnail = thumbnail;
        this.sectionName = sectionName;
        this.time = time;
        this.contributor = contributor;
    }

    public String getUrl() {
        return url;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getTime() {
        return time;
    }

    public String getContributor() {
        return contributor;
    }
}
