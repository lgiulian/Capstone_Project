package com.crilu.gothandroid.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.crilu.gothandroid.data.GothaContract;
import com.crilu.gothandroid.data.TournamentDao;
import com.crilu.gothandroid.model.firestore.Tournament;

import java.util.List;

import timber.log.Timber;

public class TournamentsViewModel extends AndroidViewModel {

    private MutableLiveData<List<Tournament>> mTournaments;
    private final ContentObserver mContentObserver;

    public TournamentsViewModel(@NonNull Application application) {
        super(application);
        Timber.d("In viewModel constructor. Calling setValue on viewModel");
        getTournaments().setValue(TournamentDao.getAllTournaments(application));

        mContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                Timber.d("ContentObserver.onChange() called. Calling setValue on viewModel");
                getTournaments().setValue(TournamentDao.getAllTournaments(getApplication()));
            }
        };
        ContentResolver contentResolver = application.getContentResolver();
        /* Register a content observer to be notified of changes to data at a given URI */
        contentResolver.registerContentObserver(
                GothaContract.TournamentEntry.CONTENT_URI,
                true,
                mContentObserver);
    }

    public MutableLiveData<List<Tournament>> getTournaments() {
        if (mTournaments == null) {
            mTournaments = new MutableLiveData<>();
        }
        return mTournaments;
    }

    @Override
    protected void finalize() throws Throwable {
        if (mContentObserver != null) {
            getApplication().getContentResolver().unregisterContentObserver(mContentObserver);
        }
        super.finalize();
    }
}
