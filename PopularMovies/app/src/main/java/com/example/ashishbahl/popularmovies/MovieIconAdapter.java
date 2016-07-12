package com.example.ashishbahl.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Ashish Bahl on 31-May-16.
 */
public class MovieIconAdapter extends CursorAdapter {
    ImageView posterView ;
    private final String LOG_TAG = MovieIconAdapter.class.getSimpleName();

    public MovieIconAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.movie_item;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String poster_path = cursor.getString(MainFragment.COL_MOVIE_POSTER_PATH);
        posterView = (ImageView) view.findViewById(R.id.movie_image);
        Picasso
                .with(context)
                .load(poster_path)
                .into(posterView);
    }

}
