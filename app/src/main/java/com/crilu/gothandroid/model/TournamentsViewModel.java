package com.crilu.gothandroid.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.crilu.gothandroid.data.TournamentDao;
import com.crilu.gothandroid.model.firestore.Tournament;

import java.util.List;

public class TournamentsViewModel extends AndroidViewModel {

    private MutableLiveData<List<Tournament>> mTournaments;

    public TournamentsViewModel(@NonNull Application application) {
        super(application);
        getTournaments().setValue(TournamentDao.getAllTournaments(application));
    }

    public MutableLiveData<List<Tournament>> getTournaments() {
        if (mTournaments == null) {
            mTournaments = new MutableLiveData<>();
        }
        return mTournaments;
    }

}
