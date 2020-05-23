package com.example.newsfeedapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<News>> {
    private static final int NEWS_LOADER_ID = 1;
    private static final String APIKEY = "&api-key=test"; //This is requested by the Guardian API.

    private TextView emtpyText;
    private EditText editText_keyword;
    private ImageButton imageButton_clickSearch;
    private ListView listView_news;
    private News_Adaptor adapter;
    private ProgressBar loading;

    private String guardianRequestUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        guardianRequestUrl = "https://content.guardianapis.com/search";
        Log.i("main", "the url is " + guardianRequestUrl);

        getViews();

        listView_news.setEmptyView(emtpyText);
        adapter = new News_Adaptor(this, new ArrayList<News>());
        listView_news.setAdapter(adapter);

        setListener();

        if (hasNetwork()){
            LoaderManager loaderManager = LoaderManager.getInstance(this);
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        }
        else {
            loading.setVisibility(View.GONE);
            emtpyText.setText(R.string.no_network);
        }
    }

    /**
     * Find views in layout
     */
    private void getViews(){
        emtpyText = (TextView) findViewById(R.id.emtyText);
        editText_keyword = (EditText) findViewById(R.id.editText_keyword);
        imageButton_clickSearch = (ImageButton) findViewById(R.id.imageButton_clickSearch);
        listView_news = (ListView) findViewById(R.id.listView_news);
        loading = (ProgressBar) findViewById(R.id.loading);
    }

    /**
     * Set listener for each views
     */
    private void setListener(){
        imageButton_clickSearch.setOnClickListener(imgButtonListener);
        listView_news.setOnItemClickListener(listViewListener);
    }

    /**
     * Go to relevant website when clicking the news.
     */
    ListView.OnItemClickListener listViewListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            News news = adapter.getItem(position); //Get the {@link News} object
            Uri uri = Uri.parse(news.getNewsUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    };

    private ImageButton.OnClickListener imgButtonListener = new ImageButton.OnClickListener(){
        @Override
        public void onClick(View v) {
            String keyword = editText_keyword.getText().toString();

            guardianRequestUrl = "https://content.guardianapis.com/search?q=" + keyword + "&order-by=relevance";

            LoaderManager loaderManager = LoaderManager.getInstance(MainActivity.this);
            loaderManager.restartLoader(NEWS_LOADER_ID, null, MainActivity.this);

            editText_keyword.setText("");
        }
    };

    @NonNull
    @Override
    public Loader<ArrayList<News>> onCreateLoader(int id, @Nullable Bundle args) {
        SharedPreferences sharedPres = PreferenceManager.getDefaultSharedPreferences(this);
        String section = sharedPres.getString(
                getString(R.string.section_key),
                getString(R.string.section_default_value));

        Uri baseUri = Uri.parse(guardianRequestUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("section", section);
        uriBuilder.appendQueryParameter("order-by", "newest");

        return new NewsLoader(this, uriBuilder.toString() + APIKEY);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<News>> loader, ArrayList<News> data) {
        loading.setVisibility(View.GONE);

        if (hasNetwork()){
            emtpyText.setText(R.string.no_news);
            adapter.clear();
            if (data == null || !data.isEmpty()) {
                adapter.addAll(data);
            }
        }
        else{
            emtpyText.setText(R.string.no_network);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<News>> loader) {
        adapter.clear();
    }

    /**
     * Helper method to check whether the device has network.
     */
    private boolean hasNetwork(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (networkCapabilities != null){ //Network available
                return true;
            }
            else {
                return false;
            }
        }
        else {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()){ //Network available
                return true;
            }
            else {
                return false;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_news){
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
