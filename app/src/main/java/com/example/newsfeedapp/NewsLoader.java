package com.example.newsfeedapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;

public class NewsLoader extends AsyncTaskLoader<ArrayList<News>> {
    private String url;

    public NewsLoader(@NonNull Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<News> loadInBackground() {
        if (url == null){
            return null;
        }

        ArrayList<News> newsList = QueryUtils.fetchNewsData(url);
        return newsList;
    }
}
