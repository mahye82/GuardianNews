package com.example.android.guardiannews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by MHye on 24/10/2016.
 */

public class NewsItemLoader extends AsyncTaskLoader<List<NewsItem>> {

    private static final String LOG_TAG = NewsItemLoader.class.getName();

    public NewsItemLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG, "onStartLoading()");
        forceLoad();
    }

    @Override
    public List<NewsItem> loadInBackground() {
        // Get the list of news items to display
        return QueryUtils.fetchNewsItems();
    }


}
