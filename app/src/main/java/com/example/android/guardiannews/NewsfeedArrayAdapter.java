package com.example.android.guardiannews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.R.attr.thumbnail;
import static android.R.id.list;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * A {@link NewsfeedArrayAdapter} knows how to inflate the layout for each row in a
 * {@link android.widget.ListView}. Each row of the list uses a data source (a list of
 * {@link NewsItem} objects) to populates the ListView.
 */
public class NewsfeedArrayAdapter extends ArrayAdapter<NewsItem> {

    private static final String LOG_TAG = NewsfeedArrayAdapter.class.getName();

    // When displaying the results, append "by" to the start of any NewsItem that has a contributor
    // name that is not empty or null
    private static final String BY_ = "by ";

    /**
     * Constructs a new {@link NewsfeedArrayAdapter}.
     * @param context the {@link Context} of the app
     * @param newsItems the list of {@link NewsItem}s, which is the data source for the adapter
     */
    public NewsfeedArrayAdapter(Context context, List<NewsItem> newsItems) {
        super(context, 0, newsItems);
    }

    /**
     * Returns a list item view that displays information about the news item at the given position
     * in the list of {@link NewsItem}s.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        // Check if an existing view is being reused, otherwise inflate the view
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.newsfeed_list_item, parent, false);
        }

        // Get the current NewsItem
        NewsItem currentNewsItem = getItem(position);

        // Update the title TextView for the current NewsItem
        updateTitleView(listItemView, currentNewsItem);

        // Update the section TextView for the current NewsItem
        updateSectionView(listItemView, currentNewsItem);

        // Update the web publication date and time TextView for the current NewsItem
        updateDateTimeView(listItemView, currentNewsItem);

        // Update the contributor TextView for the current NewsItem
        updateContributorView(listItemView, currentNewsItem);

        // Update the thumbnail ImageView for the current NewsItem
        updateThumbnailView(listItemView, currentNewsItem);

        return listItemView;
    }

    private void updateThumbnailView(View listItemView, NewsItem currentNewsItem) {
        // Update the thumbnail ImageView for the NewsItem
        ImageView thumbnail = (ImageView)
                listItemView.findViewById(R.id.newsfeed_list_item_image);
        // If the NewsItem has an associated thumbnail, display it
        // Otherwise, use the default thumbnail
        if (currentNewsItem.getThumbnail() != null) {
            thumbnail.setImageBitmap(currentNewsItem.getThumbnail());
        } else {
            thumbnail.setImageResource(R.drawable.null_thumbnail);
        }
    }

    private void updateContributorView(View listItemView, NewsItem currentNewsItem) {
        // Update the contributor name for the NewsItem
        TextView contributorView = (TextView)
                listItemView.findViewById(R.id.newsfeed_list_item_contributor);
        // Stick "by " at the start of the contributor's name before updating the TextView if
        // the contributor name string is non-empty.
        String contributorName = currentNewsItem.getContributor();
        if (contributorName != "") {
            contributorName = BY_ + contributorName;
        }
        contributorView.setText(contributorName);
    }

    private void updateDateTimeView(View listItemView, NewsItem currentNewsItem) {
        // Update the web publication date TextView for the NewsItem
        TextView dateView = (TextView)
                listItemView.findViewById(R.id.newsfeed_list_item_date);
        String formattedTime = formatDate(currentNewsItem.getTime());
        dateView.setText(formattedTime);
    }

    private void updateSectionView(View listItemView, NewsItem currentNewsItem) {
        // Update the section TextView for the NewsItem
        TextView sectionView = (TextView)
                listItemView.findViewById(R.id.newsfeed_list_item_section);
        sectionView.setText(currentNewsItem.getSectionName().toUpperCase());
    }

    private void updateTitleView(View listItemView, NewsItem currentNewsItem) {
        // Update the title TextView for the NewsItem
        TextView titleView = (TextView)
                listItemView.findViewById(R.id.newsfeed_list_item_title);
        titleView.setText(currentNewsItem.getWebTitle());
    }

    /**
     * Converts a String representing a datetime in the format "2015-08-05T19:24:32Z" to a String
     * representing the date and time in the format "Sat, 05 Aug 07:24 PM"
     * @param datetime a String in the format "2015-08-05T19:24:32Z"
     * @return a String in the format "Sat, 05 Aug 07:24 PM"
     */
    private String formatDate(String datetime) {
        // Define the expected format of the datetime String that needs to be parsed
        // Expected format is something like 2015-08-05T19:24:32Z
        SimpleDateFormat expectedFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        // The datetime to be returned after formatting
        String formattedDateTime = "";
        Log.i(LOG_TAG, datetime);

        try {
            Date dateAndTime = expectedFormat.parse(datetime);

            // Define the format of the String that we want as output
            // Desired format is something like Sun, 20 Aug 05:00 PM
            SimpleDateFormat desiredFormat = new SimpleDateFormat("EEE, d MMM yyyy 'at' hh:mm aaa");
            formattedDateTime = desiredFormat.format(dateAndTime);

        } catch (ParseException e) {
            Log.e(LOG_TAG, "Problem parsing date and time", e);
        }

        return formattedDateTime;
    }
}
