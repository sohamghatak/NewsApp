package com.example.soham.newsapp;

/**
 * Created by soham on 30/12/17.
 */

public class News {

    private String mTitle;
    private String mSection;
    private String mPublishedDate;
    private String mWebUrl;
    private String mAuthor;

    public News(String title, String section, String publishedDate, String webUrl, String author) {
        mTitle = title;
        mSection = section;
        mPublishedDate = publishedDate;
        mWebUrl = webUrl;
        mAuthor = author;
    }

    public String getmPublishedDate() {
        return mPublishedDate;
    }

    public String getmSection() {
        return mSection;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmWebURL() {
        return mWebUrl;
    }

    public String getmAuthor() {
        return mAuthor;
    }
}

