package com.example.ashishbahl.popularmovies;

/**
 * Created by Ashish Bahl on 19-Jul-16.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ReviewAdapter extends CursorRecyclerViewAdapter<ReviewAdapter.ViewHolder> {

    public ReviewAdapter(Context context, Cursor cursor){
        super(context,cursor);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView contentView;
        public TextView authorView;
        public ViewHolder(View view) {
            super(view);
            contentView = (TextView)view.findViewById(R.id.review_content);
            authorView = (TextView) view.findViewById(R.id.review_author);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_card, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        /*MyListItem myListItem = MyListItem.fromCursor(cursor);
        viewHolder.contentView.setText(myListItem.getName());*/
        String content = cursor.getString(ReviewFragment.COL_CONTENT);
        viewHolder.contentView.setText(content);
        String author = cursor.getString(ReviewFragment.COL_AUTHOR);
        viewHolder.authorView.setText(author);
    }
}
