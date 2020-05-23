package com.example.newsfeedapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class News_Adaptor extends ArrayAdapter<News> {
    public News_Adaptor(@NonNull Context context, ArrayList<News> newsList) {
        super(context, 0, newsList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_news, parent, false);

            News news = getItem(position); //Get the news at that position.

            TextView textView_newsSection = (TextView) view.findViewById(R.id.textView_newsSection);
            textView_newsSection.setText(news.getNewsSection());

            TextView textView_newsDate = (TextView) view.findViewById(R.id.textView_newsDate);
            textView_newsDate.setText(news.getNewsDate());

            TextView textView_newsTitle = (TextView) view.findViewById(R.id.textView_newsTitle);
            textView_newsTitle.setText(news.getNewsTitle());
        }
        return view;
    }
}
