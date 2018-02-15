package com.example.soham.newsapp;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by soham on 4/1/18.
 */

public class NewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<News>> {

    public static final String ARG_NEWS_STORY = "news_story";
    public static final String LOG_TAG = NewsFragment.class.getSimpleName();
    private NewsAdapter mNewsAdapter;
    private static final int NEWS_LOADER_ID = 1;
    private String newsStory = "";
    private static final String NEWS_REQUEST_URL = "http://content.guardianapis.com/search";
    private TextView mEmptyTextView;
    private View progressLoader;

    public NewsFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        int i = getArguments().getInt(ARG_NEWS_STORY);
        newsStory = getResources().getStringArray(R.array.news_titles)[i];
        mEmptyTextView = rootView.findViewById(R.id.empty_text_view);
        progressLoader = rootView.findViewById(R.id.progress_view);

        if (savedInstanceState != null) {
            progressLoader.setVisibility(View.GONE);
            getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
        }

        if (isNetworkConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            final ListView newsListView = rootView.findViewById(R.id.list);
            final ArrayList<News> newsArray = new ArrayList<>();
            mNewsAdapter = new NewsAdapter(getActivity(), newsArray);
            newsListView.setAdapter(mNewsAdapter);
            //Start a browser intent to redirect to the new article.
            newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    News news = newsArray.get(position);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(news.getmWebURL()));
                    if (browserIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        getActivity().startActivity(browserIntent);
                    }
                }
            });

        } else {
            //Using a pop up alert to display no internet connection
            if (savedInstanceState == null) {
                try {
                    progressLoader.setVisibility(View.GONE);
                    //Setup Alert message for no internet connection
                    final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                    alertDialog.setTitle("Info");
                    alertDialog.setMessage(getString(R.string.no_connection));
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);

                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();

                        }
                    });
                    alertDialog.show();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Exception in Alert Dialogue", e);
                }
            }
        }
        return rootView;
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(NEWS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", newsStory);
        uriBuilder.appendQueryParameter("api-key", "test");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        return new NewsLoader(getActivity(), uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsData) {
        progressLoader.setVisibility(View.GONE);
        //Clear adapter on Loader refresh
        if (mNewsAdapter != null) {
            mNewsAdapter.clear();
            mEmptyTextView.setText("");
        }
        if (newsData != null && !newsData.isEmpty() && isNetworkConnected()) {
            mNewsAdapter.addAll(newsData);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        if (mNewsAdapter != null) {
            mNewsAdapter.clear();
        }
    }

    //Method to check if there is internet connectivity

    public final boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

}
