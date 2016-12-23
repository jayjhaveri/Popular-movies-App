package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Jay on 12/20/2016.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private static final String TAG = TrailerAdapter.class.getSimpleName();

    private final TrailerAdapterOnClickHandler mClickHandler;
    private String[] mTrailerData;
    private Context mContext;

    public TrailerAdapter(Context context, TrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        mContext = context;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trailer_list, parent, false);
        return new TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        holder.mTrailerTextView.setText("Trailer "+(position + 1));
    }

    @Override
    public int getItemCount() {
        if (mTrailerData==null) return 0;
        return mTrailerData.length;
    }

    public interface TrailerAdapterOnClickHandler {
        void onClick(String trailer);
    }

    public void setTrailerData(String[] trailerData) {
        mTrailerData = trailerData;
        notifyDataSetChanged();
    }


    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTrailerTextView;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            mTrailerTextView = (TextView) itemView.findViewById(R.id.trailer_tv);
            Log.d(TAG,"onViewHolder");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            String trailer = mTrailerData[adapterPosition];
            mClickHandler.onClick(trailer);
        }
    }
}
