package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by Jay on 12/3/2016.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {



    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";

    private final MovieAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;
    private Context mContext;

    public MovieAdapter(Context context, MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        mContext = context;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);


        Glide.with(mContext)
                .load(BASE_IMAGE_URL + mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_POSTER_PATH)))
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mPoster);
        holder.mPoster.setContentDescription(mCursor.getString(mCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_TITLE)));

//        Log.d("MovieAdapte",""+mContext.getResources().getSystem().getDisplayMetrics().widthPixels);
}

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCurosr) {
        mCursor = newCurosr;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(int movie_id);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mPoster;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mPoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int movie_id =  mCursor.getInt(mCursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID));
            mClickHandler.onClick(movie_id);
        }
    }
}
