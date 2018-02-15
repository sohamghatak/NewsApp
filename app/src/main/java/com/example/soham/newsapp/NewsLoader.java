package com.example.soham.newsapp;

import android.app.Activity;
import android.content.AsyncTaskLoader;

import java.util.List;

/**
 * Created by soham on 2/1/18.
 */

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private String mURL;

    public NewsLoader(Activity context, String url) {
        super(context);
        mURL = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    //Load in Background method for Loader
    @Override
    public List<News> loadInBackground() {

        if (mURL == null) {
            return null;
        }
        List<News> newsResult = NewsUtils.getNewsData(mURL);
        return newsResult;
    }
}
