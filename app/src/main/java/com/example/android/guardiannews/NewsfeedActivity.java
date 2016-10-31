package com.example.android.guardiannews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static android.R.id.empty;
import static android.os.Build.VERSION_CODES.N;

public class NewsfeedActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<NewsItem>> {

    private NewsfeedArrayAdapter adapter;

    private ProgressBar loadingIndicator;

    private TextView emptyStateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        // Setup the UI initially, which can then be updated with new data, each time
        // the onCreate() method is called
        setupUI();

        // If there's an internet connection, start the loader
        // Otherwise turn off the loading indicator and have the empty state TextView explain
        // there is no internet
        if (isNetworkAvailable()) {
            getLoaderManager().initLoader(0, null, this);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            emptyStateView.setText(R.string.no_internet);
        }
    }

    /**
     * Checks if there is an internet connection.
     * @return true if there is an internet connection
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Creates the ListView and Adapter which is used to update the display. It also sets the
     * OnItemClickListener for the ListView, which sends the user to the URL if they click
     * on an item in the list.
     */
    private void setupUI() {
        // Get the ListView
        ListView listView = (ListView) findViewById(R.id.newsfeed_list_view);

        // Find the TextView that should be the empty state view for the ListView
        emptyStateView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(emptyStateView);

        // Find the ProgressBar View
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);

        // Make the data source for the ListView
        final List<NewsItem> newsItems = new ArrayList<>();

        // Create an adapter to manage the data set in this activity
        adapter = new NewsfeedArrayAdapter(this, newsItems);

        // Set the adapter to the ListView
        listView.setAdapter(adapter);

        // Set the onItemClickListener for this ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the NewsItem that was clicked
                NewsItem currentItem = newsItems.get(position);

                // Create a URI from the URL for this NewsItem
                Uri newsItemURI = Uri.parse(currentItem.getUrl());

                // Create an implicit intent to view the URI in some web browser
                Intent intent = new Intent(Intent.ACTION_VIEW, newsItemURI);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public Loader<List<NewsItem>> onCreateLoader(int id, Bundle args) {
        return new NewsItemLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItem>> loader, List<NewsItem> data) {
        // When the Loader is done loading, ensure the empty state View reads "No results found."
        emptyStateView.setText(R.string.no_results_found);

        // When the load is complete, we can hide the loading indicator and set it so that it
        // doesn't take up any more room for layout purposes
        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Clear the adapter of its old data set
        adapter.clear();
        // Update the adapter with new data set
        if (data != null && !data.isEmpty()) {
            adapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItem>> loader) {
        adapter.clear();
    }
}
