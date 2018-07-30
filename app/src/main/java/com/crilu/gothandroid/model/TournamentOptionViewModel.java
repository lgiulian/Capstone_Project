package com.crilu.gothandroid.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.crilu.gothandroid.GothandroidApplication;
import com.crilu.opengotha.model.TournamentOptions;

public class TournamentOptionViewModel extends AndroidViewModel {

    private TournamentOptions mTournamentOptions;

    public TournamentOptionViewModel(@NonNull Application application) {
        super(application);
        this.mTournamentOptions = GothandroidApplication.getTournamentOptionsInstance();
    }

    public TournamentOptions getTournamentOptions() {
        return mTournamentOptions;
    }

}
