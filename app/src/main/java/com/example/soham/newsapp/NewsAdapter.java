package com.example.soham.newsapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by soham on 30/12/17.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    //using a View Holder class to hold and reuse views
    static class ViewHolder {
        TextView holderTitleTextView;
        TextView holderSectionTextView;
        TextView holderDateTextView;
        TextView holderAuthorTextView;
    }

    public NewsAdapter(Activity context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_news, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.holderTitleTextView = convertView.findViewById(R.id.title_text_view);
            viewHolder.holderSectionTextView = convertView.findViewById(R.id.section_text_view);
            viewHolder.holderDateTextView = convertView.findViewById(R.id.date_text_view);
            viewHolder.holderAuthorTextView = convertView.findViewById(R.id.author_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        News currentNews = getItem(position);

        viewHolder.holderTitleTextView.setText(currentNews.getmTitle());
        viewHolder.holderSectionTextView.setText(currentNews.getmSection());
        viewHolder.holderDateTextView.setText(currentNews.getmPublishedDate());
        viewHolder.holderAuthorTextView.setText(currentNews.getmAuthor());

        return convertView;
    }
}
