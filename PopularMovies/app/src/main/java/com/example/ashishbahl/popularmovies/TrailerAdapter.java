package com.example.ashishbahl.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by Ashish Bahl on 21-Jul-16.
 */
public class TrailerAdapter extends CursorRecyclerViewAdapter<TrailerAdapter.ViewHolder> {
    private Context mContext;
    public TrailerAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.trailer_thumb);
            mTextView = (TextView) itemView.findViewById(R.id.trailer_name);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer_card, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        String name = cursor.getString(TrailerFragment.COL_TITLE);
        viewHolder.mTextView.setText(name);
        final String url = cursor.getString(TrailerFragment.COL_THUMB_URL);
        Picasso
                .with(mContext)
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(viewHolder.mImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(mContext)
                                .load(url)
                                .error(R.drawable.photo)
                                .into(viewHolder.mImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.v("Picasso", "Could not fetch image");
                                    }
                                });
                    }
                });
    }

}
