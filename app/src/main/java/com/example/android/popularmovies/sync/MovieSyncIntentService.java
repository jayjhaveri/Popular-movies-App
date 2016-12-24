package com.example.android.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Jay on 12/24/2016.
 */

public class MovieSyncIntentService extends IntentService {
    public MovieSyncIntentService() {
        super("MovieSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MovieSyncTask.syncMovie(this);
    }
}
