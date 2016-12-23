package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.data.Review;

/**
 * Created by Jay on 12/20/2016.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private static final String TAG = TrailerAdapter.class.getSimpleName();

    private final ReviewAdapterOnClickHandler mClickHandler;
    private Review[] mReviewData;
    private Context mContext;

    public ReviewAdapter(Context context, ReviewAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
        mContext = context;
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_list, parent, false);
        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        Review review = mReviewData[position];
        holder.mAuthor.setText("Author: "+review.getAuthor());
        String test = review.getContent();
        test = test.replaceAll("_","");
        holder.mReviewContent.setText(test);

    }

    @Override
    public int getItemCount() {
        if (mReviewData==null) return 0;
        return mReviewData.length;
    }

    public interface ReviewAdapterOnClickHandler {
        void onClick(Review review);
    }

    public void setReviewData(Review[] reviewData) {
        mReviewData = reviewData;
        notifyDataSetChanged();
    }


    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mReviewContent;
        private TextView mAuthor;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            mAuthor = (TextView) itemView.findViewById(R.id.review_author);
            mReviewContent = (TextView) itemView.findViewById(R.id.review_content);
            Log.d(TAG,"onViewHolder");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Review review = mReviewData[adapterPosition];
            mClickHandler.onClick(review);
        }
    }
}
